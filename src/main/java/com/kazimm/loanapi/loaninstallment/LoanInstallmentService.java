package com.kazimm.loanapi.loaninstallment;

import com.kazimm.loanapi.loan.Loan;
import com.kazimm.loanapi.loan.LoanRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class LoanInstallmentService {

    @Autowired
    private LoanInstallmentRepository loanInstallmentRepository;

    @Autowired
    private LoanRepository loanRepository;

    public LoanInstallment createLoanInstallment(Long loanId, LoanInstallment installment) {
        Loan loan = loanRepository.findById(loanId)
                .orElseThrow(() -> new IllegalArgumentException("Loan not found"));

        installment.setLoan(loan);
        return loanInstallmentRepository.save(installment);
    }

    public List<LoanInstallment> getInstallmentsByLoanId(Long loanId) {
        return loanInstallmentRepository.findByLoanId(loanId);
    }
}
