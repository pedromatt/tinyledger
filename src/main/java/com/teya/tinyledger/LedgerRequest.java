package com.teya.tinyledger;

import java.math.BigDecimal;

public class LedgerRequest {

    private BigDecimal amount;
    private String description;

    // Empty constructor is required by Spring/Jackson to deserialize JSON
    public LedgerRequest() {
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
