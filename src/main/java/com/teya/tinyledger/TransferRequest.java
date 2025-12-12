package com.teya.tinyledger;

import java.math.BigDecimal;

/**
 * To separate from previous work, added new class that will also handle requests but related to the new /transfer function
 */
public class TransferRequest {
    private BigDecimal amount;
    private String description;
    private int receiverId;

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

    public int getReceiverId() {
        return receiverId;
    }

    public void setReceiverId(int receiverId) {
        this.receiverId = receiverId;
    }



    public TransferRequest() {

    }
}
