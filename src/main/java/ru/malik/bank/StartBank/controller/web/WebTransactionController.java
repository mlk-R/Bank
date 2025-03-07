package ru.malik.bank.StartBank.controller.web;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import ru.malik.bank.StartBank.entity.Account;
import ru.malik.bank.StartBank.entity.Transaction;
import ru.malik.bank.StartBank.service.AccountService;
import ru.malik.bank.StartBank.service.TransactionService;

import java.math.BigDecimal;
import java.util.List;

@Controller
public class WebTransactionController {

    private final AccountService accountService;
    private final TransactionService transactionService;

    @Autowired
    public WebTransactionController(AccountService accountService, TransactionService transactionService) {
        this.accountService = accountService;
        this.transactionService = transactionService;
    }

    @GetMapping("/account/{accountId}/transaction")
    public String getTransactionsByAccount(@PathVariable Long accountId, Model model) {
        Account account = accountService.getAccountById(accountId);
        if (account == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Аккаунт не найден");
        }

        List<Transaction> transactions = transactionService.getTransactionsByAccountId(accountId);
        model.addAttribute("account", account);
        model.addAttribute("transactions", transactions);
        return "accountTransactions";
    }

    @PostMapping("/transfer")
    public String transferFunds(@RequestParam Long sourceAccountId,
                                @RequestParam String targetCard,
                                @RequestParam BigDecimal amount,
                                RedirectAttributes redirectAttributes) {
        try {
            Account sourceAccount = accountService.getAccountById(sourceAccountId);
            if (sourceAccount == null) {
                throw new RuntimeException("Исходный аккаунт не найден");
            }

            accountService.transferFunds(sourceAccount, targetCard, amount);
            redirectAttributes.addFlashAttribute("message", "Перевод успешно выполнен");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }

        return "redirect:/account/" + sourceAccountId + "/transaction";
    }
}

