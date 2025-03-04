package ru.malik.bank.StartBank.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.malik.bank.StartBank.dto.RegisterRequest;
import ru.malik.bank.StartBank.service.UserService;

@Controller
public class LoginController {

    private final UserService userService;

    @Autowired
    public LoginController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/login")
    public String showLoginForm(Model model, @RequestParam(value = "registered", required = false) String registered) {
        if (registered != null) {
            model.addAttribute("message", "Вы успешно зарегистрировались! Пожалуйста, войдите в систему.");
        }
        return "login"; // Возвращаем страницу логина
    }

//    @PostMapping("/login")
//    public String processLogin(@RequestParam("username") String username,
//                               @RequestParam("password") String password,
//                               Model model) {
//        try {
//            // TODO UserService содержит логику аутентификации
//            boolean isAuthenticated = userService.authenticateUser(username, password);
//            if (isAuthenticated) {
//                return "redirect:/home"; // Переход на главную страницу после успешного логина
//            } else {
//                model.addAttribute("error", "Неверное имя пользователя или пароль");
//                return "login"; // Возвращаемся на страницу логина с ошибкой
//            }
//        } catch (Exception e) {
//            model.addAttribute("error", "Произошла ошибка при входе: " + e.getMessage());
//            return "login"; // Возвращаемся на страницу логина с общей ошибкой
//        }
//    }
}
