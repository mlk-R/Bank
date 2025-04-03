package ru.malik.bank.StartBank.controller.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.malik.bank.StartBank.entity.UserAccountView;
import ru.malik.bank.StartBank.service.UserAccountService;

import java.util.List;

@RestController
@RequestMapping("/api/user-accounts")
public class RestUserAccountController {

    private final UserAccountService userAccountService;

    @Autowired
    public RestUserAccountController(UserAccountService userAccountService) {
        this.userAccountService = userAccountService;
    }

    @GetMapping
    public List<UserAccountView> getUserAccounts() {
        return userAccountService.getAllUserAccounts();
    }

    @GetMapping("/active")
    public List<UserAccountView> getActiveUserAccounts() {
        return userAccountService.getUserAccountsByBalance();
    }
}
