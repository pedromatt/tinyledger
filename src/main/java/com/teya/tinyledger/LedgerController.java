package com.teya.tinyledger;

import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/ledger")
public class LedgerController {

    private final LedgerService ledgerService;

    // Spring will inject LedgerService here
    public LedgerController(LedgerService ledgerService) {
        this.ledgerService = ledgerService;
    }

    @PostMapping("/{accountId}/deposit")
    public Transaction deposit(@PathVariable int accountId, @RequestBody LedgerRequest request) {
        return ledgerService.deposit(accountId, request.getAmount(), request.getDescription());
    }

    @PostMapping("/{accountId}/withdraw")
    public Transaction withdraw(@PathVariable int accountId, @RequestBody LedgerRequest request) {
        return ledgerService.withdraw(accountId, request.getAmount(), request.getDescription());
    }

    @GetMapping("/{accountId}/transactions")
    public List<Transaction> getAllTransactions(@PathVariable int accountId) {
        return ledgerService.getAllTransactionsFromAccount(accountId);
    }

    @GetMapping("/{accountId}/balance")
    public BigDecimal getBalance(@PathVariable int accountId) {
        return ledgerService.getCurrentBalance(accountId);
    }

    @PostMapping("/{accountId}/transfer")
    public List<Transaction> transfer(@PathVariable int accountId, @RequestBody TransferRequest request) {
        return ledgerService.transfer(accountId, request.getAmount(), request.getDescription(), request.getReceiverId());
    }
}
