package ru.malik.bank.StartBank.controller.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.malik.bank.StartBank.dto.UserAccountDto;
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
    public Page<UserAccountDto> getUserAccounts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        return userAccountService.getAllUserAccounts(pageable);
    }

    @GetMapping("/active")
    public Page<UserAccountDto> getActiveUserAccounts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        return userAccountService.getUserAccountsByBalance(pageable);
    }
}

