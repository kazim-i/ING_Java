package com.kazimm.loanapi.loan;

import com.kazimm.loanapi.customer.Customer;
import com.kazimm.loanapi.customer.CustomerRepository;
import com.kazimm.loanapi.loaninstallment.LoanInstallment;
import com.kazimm.loanapi.loaninstallment.LoanInstallmentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class LoanServiceTest {

    @Mock
    private LoanRepository loanRepository;

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private LoanInstallmentRepository loanInstallmentRepository;

    @InjectMocks
    private LoanService loanService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreateLoan_WithValidData_ShouldReturnLoan() {
        // Arrange
        LoanRequestDTO request = new LoanRequestDTO();
        request.setLoanAmount(BigDecimal.valueOf(10000));
        request.setNumberOfInstallments(12);
        request.setInterestRate(BigDecimal.valueOf(0.2));

        Customer customer = new Customer();
        customer.setId(1L);
        customer.setCreditLimit(BigDecimal.valueOf(10000));
        customer.setUsedCreditLimit(BigDecimal.valueOf(0));

        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));
        when(loanRepository.save(any(Loan.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        Loan loan = loanService.createLoan(customer.getId(), request);

        // Assert
        assertNotNull(loan);
        assertEquals(BigDecimal.valueOf(10000), loan.getLoanAmount());
        assertEquals(12, loan.getNumberOfInstallments());
        verify(loanRepository, times(1)).save(loan);
    }

    @Test
    void testCreateLoan_WithInvalidInstallments_ShouldThrowException() {
        // Arrange
        LoanRequestDTO request = new LoanRequestDTO();
        request.setNumberOfInstallments(5); // Invalid installment count

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> loanService.createLoan(1L, request));
    }
}
