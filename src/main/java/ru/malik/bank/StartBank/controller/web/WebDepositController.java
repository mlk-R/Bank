package ru.malik.bank.StartBank.controller.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.malik.bank.StartBank.entity.Account;
import ru.malik.bank.StartBank.entity.Deposit;
import ru.malik.bank.StartBank.entity.User;
import ru.malik.bank.StartBank.service.AccountService;
import ru.malik.bank.StartBank.service.DepositService;
import ru.malik.bank.StartBank.service.UserService;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/user/{userId}/deposit")
public class WebDepositController {

    private final DepositService depositService;
    private final UserService userService;
    private final AccountService accountService;

    @Autowired
    public WebDepositController(DepositService depositService, UserService userService, AccountService accountService) {
        this.depositService = depositService;
        this.userService = userService;
        this.accountService = accountService;
    }

    @GetMapping()
    public String depositPage(@PathVariable Long userId, Model model) {
        Optional<User> userOptional = userService.findById(userId);
        if (userOptional.isEmpty()) {
            return "error"; // Если пользователь не найден
        }
        User user = userOptional.get();
        List<Deposit> deposits = depositService.getDepositsByUserId(userId);
        List<Account> accounts = accountService.getAccountsByUser(user); // Загружаем карты пользователя

        model.addAttribute("user", user);
        model.addAttribute("deposits", deposits);
        model.addAttribute("accounts", accounts);

        return "deposits";
    }

    @PostMapping("/take")
    public String takeDeposit(@PathVariable Long userId,
                              @RequestParam("amount") BigDecimal amount,
                              @RequestParam("term") Integer term,
                              @RequestParam("accountId") Long accountId) {
        Optional<User> userOptional = userService.findById(userId);
        if (userOptional.isEmpty()) {
            return "error";
        }
        User user = userOptional.get();

        depositService.createDeposit(amount, term, accountId, user);
        return "redirect:/user/" + userId + "/deposit";
    }

    @PostMapping("/{depositId}/withdraw")
    public String withdrawDeposit(@PathVariable Long userId,
                                  @PathVariable Long depositId,
                                  @RequestParam("accountId") Long accountId,
                                  @RequestParam("withdrawalAmount") BigDecimal withdrawalAmount,
                                  Model model) {
        Optional<User> userOptional = userService.findById(userId);
        if (userOptional.isEmpty()) {
            return "error";
        }
        User user = userOptional.get();
        try {
            depositService.withdrawDeposit(depositId, accountId, withdrawalAmount, user);
            model.addAttribute("message", "Снятие средств с депозита прошло успешно!");
        } catch (RuntimeException e) {
            model.addAttribute("error", e.getMessage());
            return "error";
        }
        return "redirect:/user/" + userId + "/deposit";
    }
}
