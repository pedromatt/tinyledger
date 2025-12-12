package com.teya.tinyledger;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class LedgerServiceTest {

    private LedgerService service;

    // Use fixed account IDs for clarity
    private static final int ACC1 = 1;
    private static final int ACC2 = 2;

    @BeforeEach
    void setUp() {
        service = new LedgerService();
    }

    @Test
    void initialBalance_isZero_forNewAccount() {
        assertEquals(0, service.getCurrentBalance(ACC1).compareTo(BigDecimal.ZERO));
        assertEquals(0, service.getAllTransactionsFromAccount(ACC1).size());
    }

    @Test
    void deposit_createsTransaction_andUpdatesBalance_forThatAccount() {
        var tx = service.deposit(ACC1, new BigDecimal("100.00"), "Initial deposit");

        assertNotNull(tx);
        assertEquals(TransactionType.DEPOSIT, tx.getType());
        assertEquals(0, tx.getAmount().compareTo(new BigDecimal("100.00")));
        assertEquals("Initial deposit", tx.getDescription());

        assertEquals(0, service.getCurrentBalance(ACC1).compareTo(new BigDecimal("100.00")));
        assertEquals(1, service.getAllTransactionsFromAccount(ACC1).size());

        // Other account remains untouched
        assertEquals(0, service.getCurrentBalance(ACC2).compareTo(BigDecimal.ZERO));
        assertEquals(0, service.getAllTransactionsFromAccount(ACC2).size());
    }

    @Test
    void withdraw_createsTransaction_andUpdatesBalance_forThatAccount() {
        service.deposit(ACC1, new BigDecimal("100.00"), "Initial deposit");

        var tx = service.withdraw(ACC1, new BigDecimal("30.00"), "Groceries");

        assertNotNull(tx);
        assertEquals(TransactionType.WITHDRAWAL, tx.getType());
        assertEquals(0, tx.getAmount().compareTo(new BigDecimal("30.00")));
        assertEquals("Groceries", tx.getDescription());

        assertEquals(0, service.getCurrentBalance(ACC1).compareTo(new BigDecimal("70.00")));
        assertEquals(2, service.getAllTransactionsFromAccount(ACC1).size());
    }

    @Test
    void withdraw_moreThanBalance_throwsIllegalArgumentException_andDoesNotAddTransaction() {
        service.deposit(ACC1, new BigDecimal("100.00"), "Initial deposit");

        var ex = assertThrows(IllegalArgumentException.class,
                () -> service.withdraw(ACC1, new BigDecimal("300.00"), "Too big"));

        // Your message text says "Cannot withdraw..." so this is safer than checking "withdraw"
        assertTrue(ex.getMessage().toLowerCase().contains("cannot withdraw"));

        assertEquals(1, service.getAllTransactionsFromAccount(ACC1).size(),
                "Failed withdraw should not add a transaction");
        assertEquals(0, service.getCurrentBalance(ACC1).compareTo(new BigDecimal("100.00")));
    }

    @Test
    void deposit_zeroOrNegative_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class,
                () -> service.deposit(ACC1, BigDecimal.ZERO, "Zero"));

        assertThrows(IllegalArgumentException.class,
                () -> service.deposit(ACC1, new BigDecimal("-1.00"), "Negative"));

        assertEquals(0, service.getAllTransactionsFromAccount(ACC1).size());
        assertEquals(0, service.getCurrentBalance(ACC1).compareTo(BigDecimal.ZERO));
    }

    @Test
    void withdraw_zeroOrNegative_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class,
                () -> service.withdraw(ACC1, BigDecimal.ZERO, "Zero"));

        assertThrows(IllegalArgumentException.class,
                () -> service.withdraw(ACC1, new BigDecimal("-1.00"), "Negative"));

        assertEquals(0, service.getAllTransactionsFromAccount(ACC1).size());
        assertEquals(0, service.getCurrentBalance(ACC1).compareTo(BigDecimal.ZERO));
    }

    @Test
    void multipleTransactions_balanceIsCorrect_perAccount() {
        service.deposit(ACC1, new BigDecimal("100.00"), "A");
        service.deposit(ACC1, new BigDecimal("50.00"), "B");
        service.withdraw(ACC1, new BigDecimal("30.00"), "C");

        service.deposit(ACC2, new BigDecimal("10.00"), "Other account");

        assertEquals(0, service.getCurrentBalance(ACC1).compareTo(new BigDecimal("120.00")));
        assertEquals(3, service.getAllTransactionsFromAccount(ACC1).size());

        assertEquals(0, service.getCurrentBalance(ACC2).compareTo(new BigDecimal("10.00")));
        assertEquals(1, service.getAllTransactionsFromAccount(ACC2).size());
    }

    @Test
    void transfer_createsTwoTransactions_andMovesMoneyBetweenAccounts() {
        service.deposit(ACC1, new BigDecimal("100.00"), "Initial");

        List<Transaction> result = service.transfer(ACC1, new BigDecimal("25.00"), "Payback", ACC2);

        assertNotNull(result);
        assertEquals(2, result.size());

        Transaction senderTx = result.get(0);
        Transaction receiverTx = result.get(1);

        assertEquals(TransactionType.WITHDRAWAL, senderTx.getType());
        assertEquals(0, senderTx.getAmount().compareTo(new BigDecimal("25.00")));

        assertEquals(TransactionType.DEPOSIT, receiverTx.getType());
        assertEquals(0, receiverTx.getAmount().compareTo(new BigDecimal("25.00")));

        // Balances updated
        assertEquals(0, service.getCurrentBalance(ACC1).compareTo(new BigDecimal("75.00")));
        assertEquals(0, service.getCurrentBalance(ACC2).compareTo(new BigDecimal("25.00")));

        // Transaction histories updated
        assertEquals(2, service.getAllTransactionsFromAccount(ACC1).size(),
                "ACC1 should have initial deposit + transfer withdrawal");
        assertEquals(1, service.getAllTransactionsFromAccount(ACC2).size(),
                "ACC2 should have transfer deposit");
    }

    @Test
    void transfer_moreThanBalance_throwsIllegalArgumentException_andDoesNotChangeAccounts() {
        service.deposit(ACC1, new BigDecimal("50.00"), "Initial");

        assertThrows(IllegalArgumentException.class,
                () -> service.transfer(ACC1, new BigDecimal("200.00"), "Too much", ACC2));

        // No changes should have been applied
        assertEquals(0, service.getCurrentBalance(ACC1).compareTo(new BigDecimal("50.00")));
        assertEquals(0, service.getCurrentBalance(ACC2).compareTo(BigDecimal.ZERO));
        assertEquals(1, service.getAllTransactionsFromAccount(ACC1).size());
        assertEquals(0, service.getAllTransactionsFromAccount(ACC2).size());
    }

    @Test
    void transfer_toSameAccount_throwsIllegalArgumentException_andDoesNotChangeBalance() {
        service.deposit(ACC1, new BigDecimal("50.00"), "Initial");

        assertThrows(IllegalArgumentException.class,
                () -> service.transfer(ACC1, new BigDecimal("10.00"), "Same", ACC1));

        assertEquals(0, service.getCurrentBalance(ACC1).compareTo(new BigDecimal("50.00")));
        assertEquals(1, service.getAllTransactionsFromAccount(ACC1).size());
    }

    @Test
    void ids_areIncrementing_acrossOperations() {
        var t1 = service.deposit(ACC1, new BigDecimal("10.00"), "t1");
        var t2 = service.deposit(ACC1, new BigDecimal("10.00"), "t2");
        var t3 = service.withdraw(ACC1, new BigDecimal("5.00"), "t3");

        assertTrue(t2.getId() > t1.getId());
        assertTrue(t3.getId() > t2.getId());

        service.deposit(ACC1, new BigDecimal("100.00"), "init");
        var transferTxs = service.transfer(ACC1, new BigDecimal("10.00"), "xfer", ACC2);

        assertEquals(2, transferTxs.size());
        assertTrue(transferTxs.get(0).getId() > t3.getId());
        assertTrue(transferTxs.get(1).getId() > transferTxs.get(0).getId());
    }
}
