package ru.malik.bank.StartBank.controller.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.malik.bank.StartBank.dto.TransferRequest;
import ru.malik.bank.StartBank.entity.Account;
import ru.malik.bank.StartBank.entity.Transaction;
import ru.malik.bank.StartBank.service.AccountService;
import ru.malik.bank.StartBank.service.TransactionService;

import java.util.List;

@RestController
@RequestMapping("/api/transactions")
public class RestTransactionController {

    private final AccountService accountService;
    private final TransactionService transactionService;

    @Autowired
    public RestTransactionController(AccountService accountService, TransactionService transactionService) {
        this.accountService = accountService;
        this.transactionService = transactionService;
    }

    @GetMapping("/account/{accountId}")
    public ResponseEntity<?> getTransactionsByAccount(@PathVariable Long accountId) {
        Account account = accountService.getAccountById(accountId);
        if (account == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Аккаунт не найден");
        }

        List<Transaction> transactions = transactionService.getTransactionsByAccountId(accountId);
        return ResponseEntity.ok(transactions);
    }

    @PostMapping("/transfer")
    public ResponseEntity<?> transferFunds(@RequestBody TransferRequest request) {
        try {
            Account sourceAccount = accountService.getAccountById(request.getSourceAccountId());
            if (sourceAccount == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Исходный аккаунт не найден");
            }

            accountService.transferFunds(sourceAccount, request.getTargetCard(), request.getAmount());
            return ResponseEntity.ok("Перевод успешно выполнен");
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }
}

