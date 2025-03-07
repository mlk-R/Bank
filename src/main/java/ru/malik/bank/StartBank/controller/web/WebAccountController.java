package ru.malik.bank.StartBank.controller.web;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import ru.malik.bank.StartBank.entity.Account;
import ru.malik.bank.StartBank.entity.User;
import ru.malik.bank.StartBank.service.AccountService;
import ru.malik.bank.StartBank.service.UserService;

import java.math.BigDecimal;
import java.security.Principal;

@Controller
@RequestMapping("/account")
public class WebAccountController {

    private final UserService userService;
    private final AccountService accountService;

    @Autowired
    public WebAccountController(UserService userService, AccountService accountService) {
        this.userService = userService;
        this.accountService = accountService;
    }

    @GetMapping
    public String accountPage(Model model, Principal principal) {
        User user = userService.findByUsernameWithAccounts(principal.getName());
        model.addAttribute("user", user);
        model.addAttribute("accounts", user.getAccounts());
        return "account";
    }

    @PostMapping("/open")
    public String createAccount(@RequestParam("accountType") String accountType,
                                @RequestParam("initialBalance") BigDecimal initialBalance,
                                Principal principal,
                                RedirectAttributes redirectAttributes) {

        User user = userService.findByUsername(principal.getName());
        try {
            accountService.createAccount(user, initialBalance, accountType);
            redirectAttributes.addFlashAttribute("message", "Аккаунт успешно создан");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Ошибка при создании аккаунта: " + e.getMessage());
        }
        return "redirect:/account";
    }

    @PostMapping("/top-up/{accountId}")
    public String topUpAccount(@PathVariable("accountId") Long accountId,
                               @RequestParam("amount") BigDecimal amount,
                               RedirectAttributes redirectAttributes) {
        try {
            Account account = accountService.getAccountById(accountId);
            accountService.topUpAccount(account, amount);
            redirectAttributes.addFlashAttribute("message", "Счет успешно пополнен");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", "Ошибка при пополнении счета: " + e.getMessage());
        }
        return "redirect:/account";
    }

    @PostMapping("/withdraw/{accountId}")
    public String withdrawFunds(@PathVariable("accountId") Long accountId,
                                @RequestParam("amount") BigDecimal amount,
                                RedirectAttributes redirectAttributes) {
        try {
            Account account = accountService.getAccountById(accountId);
            accountService.withdraw(account, amount);
            redirectAttributes.addFlashAttribute("message", "Средства успешно выведены");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", "Ошибка при снятии средств: " + e.getMessage());
        }
        return "redirect:/account";
    }
}

