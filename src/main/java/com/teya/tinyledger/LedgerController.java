package com.teya.tinyledger;

import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/transactions")
public class LedgerController {

    private final LedgerService ledgerService;

    // Spring will inject LedgerService here
    public LedgerController(LedgerService ledgerService) {
        this.ledgerService = ledgerService;
    }

    @PostMapping("/deposit")
    public Transaction deposit(@RequestBody LedgerRequest request) {
        return ledgerService.deposit(request.getAmount(), request.getDescription());
    }

    @PostMapping("/withdraw")
    public Transaction withdraw(@RequestBody LedgerRequest request) {
        return ledgerService.withdraw(request.getAmount(), request.getDescription());
    }

    @GetMapping
    public List<Transaction> getAllTransactions() {
        return ledgerService.getAllTransactions();
    }

    @GetMapping("/balance")
    public BigDecimal getBalance() {
        return ledgerService.getCurrentBalance();
    }
}
