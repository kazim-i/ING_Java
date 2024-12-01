package com.kazimm.loanapi.loaninstallment;

import com.kazimm.loanapi.loan.Loan;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface LoanInstallmentRepository extends JpaRepository<LoanInstallment, Long> {
    List<LoanInstallment> findByLoanId(Long loanId);

    List<LoanInstallment> findAllByLoanAndIsPaidFalseOrderByDueDate(Loan loan);

    long countByLoanAndIsPaidFalse(Loan loan);
}
