package ru.malik.bank.StartBank.controller.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.malik.bank.StartBank.entity.Account;
import ru.malik.bank.StartBank.entity.Deposit;
import ru.malik.bank.StartBank.entity.User;
import ru.malik.bank.StartBank.service.AccountService;
import ru.malik.bank.StartBank.service.DepositService;
import ru.malik.bank.StartBank.service.UserService;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/user/{userId}/deposit")
public class RestDepositController {

    private final DepositService depositService;
    private final UserService userService;
    private final AccountService accountService;

    @Autowired
    public RestDepositController(DepositService depositService, UserService userService, AccountService accountService) {
        this.depositService = depositService;
        this.userService = userService;
        this.accountService = accountService;
    }

    /**
     * Получение информации о депозите пользователя.
     */
    @GetMapping
    public ResponseEntity<?> getDeposits(@PathVariable Long userId) {
        Optional<User> userOptional = userService.findById(userId);
        if (userOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        }
        User user = userOptional.get();
        List<Deposit> deposits = depositService.getDepositsByUserId(userId);
        List<Account> accounts = accountService.getAccountsByUser(user);

        Map<String, Object> response = new HashMap<>();
        response.put("user", user);
        response.put("deposits", deposits);
        response.put("accounts", accounts);

        return ResponseEntity.ok(response);
    }

    /**
     * Создание нового депозита.
     */
    @PostMapping("/take")
    public ResponseEntity<?> createDeposit(@PathVariable Long userId,
                                           @RequestParam("amount") BigDecimal amount,
                                           @RequestParam("term") Integer term,
                                           @RequestParam("accountId") Long accountId) {
        Optional<User> userOptional = userService.findById(userId);
        if (userOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        }
        User user = userOptional.get();
        Deposit deposit = depositService.createDeposit(amount, term, accountId, user);
        return ResponseEntity.status(HttpStatus.CREATED).body(deposit);
    }

    /**
     * Снятие средств с депозита.
     */
    @PostMapping("/{depositId}/withdraw")
    public ResponseEntity<?> withdrawDeposit(@PathVariable Long userId,
                                             @PathVariable Long depositId,
                                             @RequestParam("accountId") Long accountId,
                                             @RequestParam("withdrawalAmount") BigDecimal withdrawalAmount) {
        Optional<User> userOptional = userService.findById(userId);
        if (userOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        }
        User user = userOptional.get();
        try {
            Deposit updatedDeposit = depositService.withdrawDeposit(depositId, accountId, withdrawalAmount, user);
            return ResponseEntity.ok(updatedDeposit);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }
}