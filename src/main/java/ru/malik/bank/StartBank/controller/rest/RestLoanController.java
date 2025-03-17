package ru.malik.bank.StartBank.controller.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.malik.bank.StartBank.entity.Account;
import ru.malik.bank.StartBank.entity.Loan;
import ru.malik.bank.StartBank.entity.User;
import ru.malik.bank.StartBank.service.AccountService;
import ru.malik.bank.StartBank.service.LoanService;
import ru.malik.bank.StartBank.service.UserService;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/user/{userId}/loan")
public class RestLoanController {

    private final LoanService loanService;
    private final UserService userService;
    private final AccountService accountService;

    @Autowired
    public RestLoanController(LoanService loanService, UserService userService, AccountService accountService) {
        this.loanService = loanService;
        this.userService = userService;
        this.accountService = accountService;
    }

    /**
     * Получение информации о займах пользователя.
     */
    @GetMapping
    public ResponseEntity<?> getLoans(@PathVariable Long userId) {
        User user = userService.findById(userId)
                .orElseThrow(() -> new RuntimeException("Пользователь не найден"));

        List<Loan> loans = loanService.getLoansByUserId(userId);
        List<Account> accounts = accountService.getAccountsByUser(user);

        Map<String, Object> response = new HashMap<>();
        response.put("user", user);
        response.put("loans", loans);
        response.put("accounts", accounts);

        return ResponseEntity.ok(response);
    }

    /**
     * Создание нового займа.
     */
    @PostMapping("/take")
    public ResponseEntity<?> takeLoan(@PathVariable Long userId,
                                      @RequestParam("amount") BigDecimal amount) {
        User user = userService.findById(userId)
                .orElseThrow(() -> new RuntimeException("Пользователь не найден"));

        Loan loan = new Loan();
        loanService.createLoan(loan, amount, user);
        return ResponseEntity.status(HttpStatus.CREATED).body(loan);
    }

    /**
     * Погашение займа.
     */
    @PostMapping("/{loanId}/pay")
    public ResponseEntity<?> payLoan(@PathVariable Long userId,
                                     @PathVariable Long loanId,
                                     @RequestParam("accountId") Long accountId,
                                     @RequestParam("paymentAmount") BigDecimal paymentAmount) {
        User user = userService.findById(userId)
                .orElseThrow(() -> new RuntimeException("Пользователь не найден"));

        try {
            // Предполагается, что метод payLoan возвращает обновлённый объект Loan
            Loan updatedLoan = loanService.payLoan(loanId, accountId, paymentAmount, user);
            return ResponseEntity.ok(updatedLoan);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }
}