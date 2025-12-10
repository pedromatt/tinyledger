package com.teya.tinyledger;

import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


@Service
public class LedgerService{

    private final List<Transaction> transactions = new ArrayList<>();
    private int currentID = 1;

    public Transaction deposit(BigDecimal amount, String description) {
       validateAmount(amount);
       var transaction = new Transaction(incrementID(), TransactionType.DEPOSIT, amount, LocalDateTime.now(), description);
       transactions.add(transaction);
       return transaction;
    }

    public Transaction withdraw(BigDecimal amount, String description) {
        validateAmount(amount);
        if (amount.compareTo(getCurrentBalance()) > 0) {
            throw new IllegalArgumentException("Cannot withdraw more money than you have in your account balance.");
        }
        var transaction = new Transaction(incrementID(), TransactionType.WITHDRAWAL, amount, LocalDateTime.now(), description);
        transactions.add(transaction);
        return transaction;
    }

    public BigDecimal getCurrentBalance() {
        BigDecimal balance = BigDecimal.ZERO;
        for (Transaction t : transactions) {
            if (t.getType() == TransactionType.WITHDRAWAL) {
                balance = balance.subtract(t.getAmount());
            } else if (t.getType() == TransactionType.DEPOSIT) {
                balance = balance.add(t.getAmount());
            }
        }
        return balance;
    }

    public List<Transaction> getAllTransactions() {
        return transactions;
    }

    // helper method
    private void validateAmount(BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Amount must be a positive value.");
        }
    }

    private int incrementID() {
        return currentID++;
    }
}
