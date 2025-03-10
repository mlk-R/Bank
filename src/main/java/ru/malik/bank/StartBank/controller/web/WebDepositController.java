package ru.malik.bank.StartBank.controller.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.malik.bank.StartBank.entity.Deposit;
import ru.malik.bank.StartBank.entity.User;
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

    @Autowired
    public WebDepositController(DepositService depositService, UserService userService) {
        this.depositService = depositService;
        this.userService = userService;
    }

    // GET-метод для отображения страницы депозитов пользователя
    @GetMapping()
    public String depositPage(@PathVariable Long userId, Model model) {
        Optional<User> userOptional = userService.findById(userId);
        if (userOptional.isEmpty()) {
            return "error"; // Обработка случая, когда пользователь не найден
        }
        User user = userOptional.get();
        model.addAttribute("user", user);
        List<Deposit> deposits = depositService.getDepositsByUserId(userId);
        model.addAttribute("deposits", deposits);
        return "deposits"; // Имя Thymeleaf-шаблона для отображения депозитов (deposits.html)
    }

    // POST-метод для открытия нового депозита
    @PostMapping("/take")
    public String takeDeposit(@PathVariable Long userId,
                              @RequestParam("amount") BigDecimal amount,
                              @RequestParam("term") Integer term) {
        Optional<User> userOptional = userService.findById(userId);
        if (userOptional.isEmpty()) {
            return "error";
        }
        User user = userOptional.get();

        // Создаём новый депозит и заполняем необходимые поля
        Deposit deposit = new Deposit();
        deposit.setTerm(term); // срок депозита (например, количество месяцев)
        depositService.createDeposit(deposit, amount, user);
        return "redirect:/user/" + userId + "/deposits";
    }

    // POST-метод для снятия средств с депозита
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
            Deposit updatedDeposit = depositService.withdrawDeposit(depositId, accountId, withdrawalAmount, user);
            model.addAttribute("message", "Снятие средств с депозита прошло успешно!");
        } catch (RuntimeException e) {
            model.addAttribute("error", e.getMessage());
            return "error"; // Страница с сообщением об ошибке снятия средств
        }
        return "redirect:/user/" + userId + "/deposits";
    }
}