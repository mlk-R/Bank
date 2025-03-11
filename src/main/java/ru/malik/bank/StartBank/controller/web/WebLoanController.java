package ru.malik.bank.StartBank.controller.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.malik.bank.StartBank.entity.Account;
import ru.malik.bank.StartBank.entity.Loan;
import ru.malik.bank.StartBank.entity.User;
import ru.malik.bank.StartBank.service.AccountService;
import ru.malik.bank.StartBank.service.LoanService;
import ru.malik.bank.StartBank.service.UserService;

import java.math.BigDecimal;
import java.util.List;

@Controller
@RequestMapping("/user/{userId}/loan")
public class WebLoanController {

    private final LoanService loanService;
    private final UserService userService;
    private final AccountService accountService;

    @Autowired
    public WebLoanController(LoanService loanService, UserService userService, AccountService accountService) {
        this.loanService = loanService;
        this.userService = userService;
        this.accountService = accountService;
    }

    @GetMapping()
    public String loanPage(@PathVariable Long userId, Model model) {
        User user = userService.findById(userId)
                .orElseThrow(() -> new RuntimeException("Пользователь не найден"));

        List<Loan> loans = loanService.getLoansByUserId(userId);
        List<Account> accounts = accountService.getAccountsByUser(user);

        model.addAttribute("user", user);
        model.addAttribute("loans", loans);
        model.addAttribute("accounts", accounts);

        return "loans";
    }

    @PostMapping("/take")
    public String takeLoan(@PathVariable Long userId, @RequestParam("amount") BigDecimal amount) {
        User user = userService.findById(userId)
                .orElseThrow(() -> new RuntimeException("Пользователь не найден"));

        Loan loan = new Loan();
        loanService.createLoan(loan, amount, user);

        return "redirect:/user/" + userId + "/loan";
    }

    @PostMapping("/{loanId}/pay")
    public String payLoan(@PathVariable Long userId,
                          @PathVariable Long loanId,
                          @RequestParam("accountId") Long accountId,
                          @RequestParam("paymentAmount") BigDecimal paymentAmount,
                          Model model) {
        User user = userService.findById(userId)
                .orElseThrow(() -> new RuntimeException("Пользователь не найден"));

        try {
            loanService.payLoan(loanId, accountId, paymentAmount, user);
            return "redirect:/user/" + userId + "/loan";
        } catch (RuntimeException e) {
            model.addAttribute("error", e.getMessage());
            return "error";
        }
    }
}

