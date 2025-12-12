package com.teya.tinyledger;

import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Service
public class LedgerService{

    private final Map<Integer, List<Transaction>> transactionsByAccount = new HashMap<>();
    private int currentID = 1;

    public Transaction deposit(int accountId, BigDecimal amount, String description) {
       validateAmount(amount);
       var transaction = new Transaction(incrementID(), TransactionType.DEPOSIT, amount, LocalDateTime.now(), description);
       accountTransactions(accountId).add(transaction);
       return transaction;
    }

    public Transaction withdraw(int accountId, BigDecimal amount, String description) {
        validateAmount(amount);
        if (amount.compareTo(getCurrentBalance(accountId)) > 0) {
            throw new IllegalArgumentException("Cannot withdraw more money than you have in your account balance.");
        }
        var transaction = new Transaction(incrementID(), TransactionType.WITHDRAWAL, amount, LocalDateTime.now(), description);
        accountTransactions(accountId).add(transaction);
        return transaction;
    }

    public BigDecimal getCurrentBalance(int accountId) {
        BigDecimal balance = BigDecimal.ZERO;
        for (Transaction t : accountTransactions(accountId)) {
            if (t.getType() == TransactionType.WITHDRAWAL) {
                balance = balance.subtract(t.getAmount());
            } else if (t.getType() == TransactionType.DEPOSIT) {
                balance = balance.add(t.getAmount());
            }
        }
        return balance;
    }

    public List<Transaction> getAllTransactionsFromAccount(int accountId) {
        return List.copyOf(accountTransactions(accountId));
    }

    public List<Transaction> transfer(int senderId, BigDecimal amount, String description, int receiverId) {
        validateAmount(amount);
        validateTransfer(senderId, receiverId);
        if (amount.compareTo(getCurrentBalance(senderId)) > 0) {
            throw new IllegalArgumentException("Cannot withdraw more money than you have in your account balance.");
        }

        var transferTransactions = new ArrayList<Transaction>(); //transaction list to return, separate from global transaction list

        //withdraw from sender logic:
        var transferToDesc = "Transfer to Account with ID " + receiverId + "---" + description;
        var withdrawTransaction = new Transaction(incrementID(), TransactionType.WITHDRAWAL, amount, LocalDateTime.now(), transferToDesc);
        accountTransactions(senderId).add(withdrawTransaction);
        transferTransactions.add(withdrawTransaction); //for return purposes so we can see both transactions when testing

        //deposit to receiver logic:
        var receivedFromDesc = "Received money from Account with ID " + senderId + "---" + description;
        var depositTransaction = new Transaction(incrementID(), TransactionType.DEPOSIT, amount, LocalDateTime.now(), receivedFromDesc);
        accountTransactions(receiverId).add(depositTransaction);
        transferTransactions.add(depositTransaction); //for return purposes so we can see both transactions when testing

        return transferTransactions;
    }

    // helper method
    private void validateAmount(BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Amount must be a positive value.");
        }
    }

    private void validateTransfer(int senderId, int receiverId) {
        if(senderId == receiverId) {
            throw new IllegalArgumentException("Cannot transfer money to the same account.");
        }
    }

    private int incrementID() {
        return currentID++;
    }

    private List<Transaction> accountTransactions(int accountId) {
        return transactionsByAccount.computeIfAbsent(accountId, id -> new ArrayList<>());
    }
}
