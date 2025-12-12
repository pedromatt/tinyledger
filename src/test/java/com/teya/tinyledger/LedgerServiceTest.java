package com.teya.tinyledger;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class LedgerServiceTest {

    private LedgerService service;

    @BeforeEach
    void setUp() {
        service = new LedgerService();
    }

    @Test
    void initialBalance_isZero() {
        assertEquals(0, service.getCurrentBalance().compareTo(BigDecimal.ZERO));
    }

    @Test
    void deposit_createsTransaction_andUpdatesBalance() {
        var tx = service.deposit(new BigDecimal("100.00"), "Initial deposit");

        assertNotNull(tx);
        assertEquals(TransactionType.DEPOSIT, tx.getType());
        assertEquals(0, tx.getAmount().compareTo(new BigDecimal("100.00")));
        assertEquals("Initial deposit", tx.getDescription());

        assertEquals(0, service.getCurrentBalance().compareTo(new BigDecimal("100.00")));
        assertEquals(1, service.getAllTransactions().size());
    }

    @Test
    void withdraw_createsTransaction_andUpdatesBalance() {
        service.deposit(new BigDecimal("100.00"), "Initial deposit");

        var tx = service.withdraw(new BigDecimal("30.00"), "Groceries");

        assertNotNull(tx);
        assertEquals(TransactionType.WITHDRAWAL, tx.getType());
        assertEquals(0, tx.getAmount().compareTo(new BigDecimal("30.00")));
        assertEquals("Groceries", tx.getDescription());

        assertEquals(0, service.getCurrentBalance().compareTo(new BigDecimal("70.00")));
        assertEquals(2, service.getAllTransactions().size());
    }

    @Test
    void withdraw_moreThanBalance_throwsIllegalArgumentException() {
        service.deposit(new BigDecimal("100.00"), "Initial deposit");

        var ex = assertThrows(IllegalArgumentException.class,
                () -> service.withdraw(new BigDecimal("300.00"), "Too big"));

        // Optional if you want to assert message text:
        assertTrue(ex.getMessage().toLowerCase().contains("withdraw"));
        assertEquals(1, service.getAllTransactions().size(), "Failed withdraw should not add a transaction");
        assertEquals(0, service.getCurrentBalance().compareTo(new BigDecimal("100.00")));
    }

    @Test
    void deposit_zeroOrNegative_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class,
                () -> service.deposit(BigDecimal.ZERO, "Zero"));

        assertThrows(IllegalArgumentException.class,
                () -> service.deposit(new BigDecimal("-1.00"), "Negative"));

        assertEquals(0, service.getAllTransactions().size());
        assertEquals(0, service.getCurrentBalance().compareTo(BigDecimal.ZERO));
    }

    @Test
    void withdraw_zeroOrNegative_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class,
                () -> service.withdraw(BigDecimal.ZERO, "Zero"));

        assertThrows(IllegalArgumentException.class,
                () -> service.withdraw(new BigDecimal("-1.00"), "Negative"));

        assertEquals(0, service.getAllTransactions().size());
        assertEquals(0, service.getCurrentBalance().compareTo(BigDecimal.ZERO));
    }

    @Test
    void multipleTransactions_balanceIsCorrect() {
        service.deposit(new BigDecimal("100.00"), "A");
        service.deposit(new BigDecimal("50.00"), "B");
        service.withdraw(new BigDecimal("30.00"), "C");

        assertEquals(0, service.getCurrentBalance().compareTo(new BigDecimal("120.00")));
        assertEquals(3, service.getAllTransactions().size());
    }

    @Test
    void ids_areIncrementing() {
        var t1 = service.deposit(new BigDecimal("10.00"), "t1");
        var t2 = service.deposit(new BigDecimal("10.00"), "t2");
        var t3 = service.withdraw(new BigDecimal("5.00"), "t3");

        assertTrue(t2.getId() > t1.getId());
        assertTrue(t3.getId() > t2.getId());
    }
}
