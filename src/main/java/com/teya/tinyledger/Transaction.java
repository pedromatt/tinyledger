package com.teya.tinyledger;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Transaction {

    private final int id;
    private final TransactionType type;
    private final BigDecimal amount;
    private final LocalDateTime timestamp;
    private final String description;

    public Transaction(int id, TransactionType type, BigDecimal amount, LocalDateTime timestamp, String description) {
        this.id = id;
        this.type = type;
        this.amount = amount;
        this.timestamp = timestamp;
        this.description = description;
    }

    public int getId() {
        return id;
    }

    public TransactionType getType() {
        return type;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public String getDescription() {
        return description;
    }
}
