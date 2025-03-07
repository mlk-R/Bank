package ru.malik.bank.StartBank.controller.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.malik.bank.StartBank.dto.loginRegister.LoginRequest;
import ru.malik.bank.StartBank.service.UserService;

@Controller
public class WebLoginController {

    private final UserService userService;

    @Autowired
    public WebLoginController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/login")
    public String showLoginForm(Model model, @RequestParam(value = "registered", required = false) String registered) {
        if (registered != null) {
            model.addAttribute("message", "Вы успешно зарегистрировались! Пожалуйста, войдите в систему.");
        }
        return "login"; // Отображение страницы логина
    }

    @PostMapping("/login")
    public String processLogin(@ModelAttribute("loginRequest") LoginRequest loginRequest, Model model) {
        try {
            userService.loginUser(loginRequest);
            return "redirect:/account";
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            return "login";
        }
    }
}
