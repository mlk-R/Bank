package ru.malik.bank.StartBank.controller.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.malik.bank.StartBank.entity.Loan;
import ru.malik.bank.StartBank.entity.User;
import ru.malik.bank.StartBank.service.LoanService;
import ru.malik.bank.StartBank.service.UserService;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/user/{userId}/loan")
public class WebLoanController {

    private final LoanService loanService;
    private final UserService userService;

    @Autowired
    public WebLoanController(LoanService loanService, UserService userService) {
        this.loanService = loanService;
        this.userService = userService;
    }

    @GetMapping()
    public String loanPage(@PathVariable Long userId, Model model) {
        Optional<User> userOptional = userService.findById(userId);
        if (userOptional.isEmpty()) {
            // Если пользователь не найден, можно вернуть страницу ошибки или перенаправить
            return "error";
        }
        User user = userOptional.get();
        model.addAttribute("user", user);
        List<Loan> loans = loanService.getLoansByUserId(userId);
        model.addAttribute("loans", loans);
        return "loans";
    }


    @PostMapping("/take")
    public String takeLoan(@PathVariable Long userId,
                           @RequestParam("amount") BigDecimal amount) {
        Optional<User> userOptional = userService.findById(userId);
        if (userOptional.isEmpty()) {
            return "error";
        }
        User user = userOptional.get();

        Loan loan = new Loan();
        loanService.createLoan(loan, amount, user);
        return "redirect:/user/" + userId + "/loans";
    }


    @PostMapping("/{loanId}/pay")
    public String payLoan(@PathVariable Long userId,
                          @PathVariable Long loanId,
                          @RequestParam("accountId") Long accountId,
                          @RequestParam("paymentAmount") BigDecimal paymentAmount,
                          Model model) {
        Optional<User> userOptional = userService.findById(userId);
        if (userOptional.isEmpty()) {
            return "error"; // Обработка случая, когда пользователь не найден
        }
        User user = userOptional.get();
        try {
            Loan updatedLoan = loanService.payLoan(loanId, accountId, paymentAmount, user);
            model.addAttribute("message", "Оплата кредита прошла успешно!");
        } catch (RuntimeException e) {
            model.addAttribute("error", e.getMessage());
            return "/error"; // Страница с ошибкой оплаты
        }
        return "redirect:/user/" + userId + "/loans";
    }
}
