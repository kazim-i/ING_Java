package com.kazimm.loanapi.loan;

import java.math.BigDecimal;

public class LoanPaymentRequestDTO {
    private BigDecimal amount;

    // Getters and Setters
    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }
}
