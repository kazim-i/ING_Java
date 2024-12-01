package com.kazimm.loanapi.loan;

import java.math.BigDecimal;

public class LoanPaymentResponseDTO {
    private int installmentsPaid;
    private BigDecimal totalAmountSpent;
    private boolean loanPaidCompletely;

    // Getters and Setters
    public int getInstallmentsPaid() {
        return installmentsPaid;
    }

    public void setInstallmentsPaid(int installmentsPaid) {
        this.installmentsPaid = installmentsPaid;
    }

    public BigDecimal getTotalAmountSpent() {
        return totalAmountSpent;
    }

    public void setTotalAmountSpent(BigDecimal totalAmountSpent) {
        this.totalAmountSpent = totalAmountSpent;
    }

    public boolean isLoanPaidCompletely() {
        return loanPaidCompletely;
    }

    public void setLoanPaidCompletely(boolean loanPaidCompletely) {
        this.loanPaidCompletely = loanPaidCompletely;
    }
}
