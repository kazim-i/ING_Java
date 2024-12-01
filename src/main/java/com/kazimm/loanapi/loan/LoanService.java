package com.kazimm.loanapi.loan;

import com.kazimm.loanapi.customer.Customer;
import com.kazimm.loanapi.customer.CustomerRepository;
import com.kazimm.loanapi.loaninstallment.LoanInstallment;
import com.kazimm.loanapi.loaninstallment.LoanInstallmentRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
public class LoanService {

    private static final BigDecimal MIN_INTEREST_RATE = BigDecimal.valueOf(0.1);
    private static final BigDecimal MAX_INTEREST_RATE = BigDecimal.valueOf(0.5);
    private static final int MAX_PAYABLE_NUM_MONTHS = 3;
    private static final List<Integer> VALID_INSTALLMENT_COUNTS = Arrays.asList(6, 9, 12, 24);

    @Autowired
    private LoanRepository loanRepository;
    @Autowired
    private CustomerRepository customerRepository;
    @Autowired
    private LoanInstallmentRepository loanInstallmentRepository;

    @Transactional
    public Loan createLoan(Long customerId, LoanRequestDTO loanRequestDTO) {

        int numberOfInstallments = loanRequestDTO.getNumberOfInstallments();
        validateInstallmentCount(numberOfInstallments);
        BigDecimal interestRate = loanRequestDTO.getInterestRate();
        validateInterestRate(interestRate);

        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new IllegalArgumentException("Customer not found"));
        BigDecimal loanAmount = loanRequestDTO.getLoanAmount();
        validateLoanAgainstCreditLimit(customer, loanAmount);

        customer.setUsedCreditLimit(customer.getUsedCreditLimit().add(loanAmount));
        customerRepository.save(customer);

        Loan loan = new Loan();
        loan.setCustomer(customer);
        loan.setLoanAmount(loanRequestDTO.getLoanAmount());
        loan.setNumberOfInstallments(numberOfInstallments);
        loan.setCreateDate(LocalDate.now());
        loan.setInterestRate(interestRate);
        loan.setIsPaid(false);

        Loan savedLoan = loanRepository.save(loan);
        createLoanInstallments(savedLoan);

        return savedLoan;
    }

    public List<Loan> getLoansByCustomerId(Long customerId) {
        return loanRepository.findByCustomerId(customerId);
    }

    private void validateInstallmentCount(Integer numberOfInstallments) {
        if (numberOfInstallments == null || !VALID_INSTALLMENT_COUNTS.contains(numberOfInstallments)) {
            throw new IllegalArgumentException(
                    "Invalid number of installments. Allowed values: " + VALID_INSTALLMENT_COUNTS
            );
        }
    }

    private void validateLoanAgainstCreditLimit(Customer customer, BigDecimal loanAmount) {
        BigDecimal remainingCreditLimit = customer.getCreditLimit().subtract(customer.getUsedCreditLimit());
        if (loanAmount.compareTo(remainingCreditLimit) > 0) {
            throw new IllegalArgumentException(
                    "Loan amount exceeds the customer's available credit limit. " +
                            "Available credit limit: " + remainingCreditLimit
            );
        }
    }

    private void validateInterestRate(BigDecimal interestRate) {
        if (interestRate == null ||
                interestRate.compareTo(MIN_INTEREST_RATE) < 0 ||
                interestRate.compareTo(MAX_INTEREST_RATE) > 0) {
            throw new IllegalArgumentException(
                    "Invalid interest rate. Allowed range: " + MIN_INTEREST_RATE + " to " + MAX_INTEREST_RATE
            );
        }
    }

    private void createLoanInstallments(Loan loan) {
        BigDecimal totalLoanAmountWithInterest =
                loan.getLoanAmount().multiply(loan.getInterestRate().add(new BigDecimal(1)));
        BigDecimal installmentAmount = totalLoanAmountWithInterest
                .divide(BigDecimal.valueOf(loan.getNumberOfInstallments()), RoundingMode.HALF_EVEN);
        LocalDate dueDate = loan.getCreateDate().withDayOfMonth(1).plusMonths(1);


        List<LoanInstallment> installments = new ArrayList<>();
        for (int i = 1; i <= loan.getNumberOfInstallments(); i++) {
            LoanInstallment installment = new LoanInstallment();
            installment.setLoan(loan);
            installment.setAmount(installmentAmount);
            installment.setPaidAmount(BigDecimal.ZERO);
            installment.setDueDate(dueDate);
            installment.setIsPaid(false);
            installments.add(installment);

            // Increment due date by 1 month
            dueDate = dueDate.plusMonths(1);
        }

        loanInstallmentRepository.saveAll(installments);
    }

    @Transactional
    public LoanPaymentResponseDTO payLoan(Long loanId, BigDecimal paymentAmount) {
        Loan loan = loanRepository.findById(loanId)
                .orElseThrow(() -> new IllegalArgumentException("Loan not found"));
        Customer customer = loan.getCustomer();

        List<LoanInstallment> installments = loanInstallmentRepository.findAllByLoanAndIsPaidFalseOrderByDueDate(loan);

        BigDecimal totalPaid = BigDecimal.ZERO;
        int installmentsPaid = 0;

        for (LoanInstallment installment : installments) {
            if (installment.getDueDate().isAfter(LocalDate.now().plusMonths(MAX_PAYABLE_NUM_MONTHS))) {
                break; // Restrict to installments due within the next 3 months
            }

            if (paymentAmount.compareTo(installment.getAmount()) >= 0) {
                paymentAmount = paymentAmount.subtract(installment.getAmount());
                totalPaid = totalPaid.add(installment.getAmount());
                installmentsPaid++;

                installment.setPaidAmount(installment.getAmount());
                installment.setIsPaid(true);
                installment.setPaymentDate(LocalDate.now());
                loanInstallmentRepository.save(installment);
            } else {
                break; // Stop if the amount cannot pay the full installment
            }
        }

        if (installmentsPaid == 0) {
            throw new IllegalArgumentException("Invalid payment request.");
        }

        // Update Loan Status
        boolean allInstallmentsPaid = loanInstallmentRepository.countByLoanAndIsPaidFalse(loan) == 0;
        loan.setIsPaid(allInstallmentsPaid);
        loanRepository.save(loan);

        // Update Customer Credit Limit
        customer.setUsedCreditLimit(
                customer.getUsedCreditLimit().subtract(
                        totalPaid.divide(loan.getInterestRate().add(new BigDecimal(1)), RoundingMode.HALF_EVEN)));
        customerRepository.save(customer);

        // Prepare Response
        LoanPaymentResponseDTO response = new LoanPaymentResponseDTO();
        response.setInstallmentsPaid(installmentsPaid);
        response.setTotalAmountSpent(totalPaid);
        response.setLoanPaidCompletely(allInstallmentsPaid);

        return response;
    }
}
