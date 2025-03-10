package ru.malik.bank.StartBank.controller.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import ru.malik.bank.StartBank.entity.User;
import ru.malik.bank.StartBank.service.UserService;

@Controller
public class HomeController {

    @Autowired
    private UserService userService;

    @GetMapping("/home")
    public String homePage(Model model) {
        // Получаем текущего пользователя (предполагаем, что userService.getCurrentUser() возвращает Optional<User>)
        User user = userService.getCurrentUser();

        if (user != null) {
            // Передаем объект user в модель
            model.addAttribute("user", user);
            return "home"; // Возвращаем имя шаблона
        } else {
            // Если пользователь не найден, можно перенаправить на страницу ошибки или логина
            return "redirect:/login"; // Перенаправление на страницу входа
        }
    }
}