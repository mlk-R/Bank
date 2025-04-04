package ru.malik.bank.StartBank.controller.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.malik.bank.StartBank.dto.AccountRequest;
import ru.malik.bank.StartBank.entity.Account;
import ru.malik.bank.StartBank.entity.User;
import ru.malik.bank.StartBank.service.AccountService;
import ru.malik.bank.StartBank.service.UserService;

import java.math.BigDecimal;
import java.security.Principal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/accounts")
public class RestAccountController {

    private final AccountService accountService;
    private final UserService userService;


    @Autowired
    public RestAccountController(AccountService accountService, UserService userService) {
        this.accountService = accountService;
        this.userService = userService;
    }


    @GetMapping("/page")
    public ResponseEntity<Page<User>> getUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortField,
            @RequestParam(defaultValue = "asc") String sortDirection) {
        Page<User> usersPage = userService.getUsers(page, size, sortField, sortDirection);
        return ResponseEntity.ok(usersPage);
    }

    @GetMapping
    public ResponseEntity<List<Account>> getAccounts(Principal principal) {

        String username = principal.getName();

        User user = userService.findByUsernameWithAccounts(username);

        // Если пользователь не найден, возвращаем ошибку 401 Unauthorized
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }

        List<Account> accounts = user.getAccounts();

        // Если аккаунтов нет, возвращаем статус 204 No Content
        if (accounts.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(null);
        }

        // Возвращаем список аккаунтов с кодом 200 OK
        return ResponseEntity.ok(accounts);
    }

    // Создать новый счет
    @PostMapping("/open")
    public ResponseEntity<Account> createAccount(@RequestBody AccountRequest accountRequest, Principal principal) {

        //TODO сообщение об ошибке

        User user = userService.findByUsername(principal.getName());
        Account account = accountService.createAccount(user, accountRequest.getInitalBalance(), accountRequest.getAccountType());

        //Возвращаем статус 201
        return ResponseEntity.status(HttpStatus.CREATED).body(account);
    }

    // Пополнить счет
    @PostMapping("/top-up/{accountId}")
    public ResponseEntity<Account> topUpAccount(@PathVariable Long accountId,
                                                @RequestBody Map<String, BigDecimal> request) {
        BigDecimal amount = request.get("amount");
        Account account = accountService.findById(accountId);
        if (account == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        accountService.topUpAccount(account, amount);
        return ResponseEntity.ok(account);
    }

    // Снять деньги со счета
    @PostMapping("/withdraw/{accountId}")
    public ResponseEntity<Account> withdrawFromAccount(@PathVariable Long accountId,
                                                       @RequestBody Map<String, BigDecimal> requestBody) {
        BigDecimal amount = requestBody.get("amount");
        if (amount == null) {
            // Ошибка 404
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        Account account = accountService.findById(accountId);
        if (account == null) {

            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        try {
            accountService.withdraw(account, amount);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }

        return ResponseEntity.ok(account);
    }

    // Получить информацию о конкретном счете
    @GetMapping("/{accountId}")
    public ResponseEntity<Account> getAccount(@PathVariable Long accountId) {
        Account account = accountService.findById(accountId);
        if (account == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        return ResponseEntity.ok(account);
    }
}