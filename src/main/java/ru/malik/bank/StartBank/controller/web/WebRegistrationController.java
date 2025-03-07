package ru.malik.bank.StartBank.controller.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import ru.malik.bank.StartBank.dto.loginRegister.RegisterRequest;
import ru.malik.bank.StartBank.exception.UserAlreadyExistsException;
import ru.malik.bank.StartBank.exception.UserNotCreatedException;
import ru.malik.bank.StartBank.service.UserService;

@Controller
public class WebRegistrationController {

    private final UserService userService;

    @Autowired
    public WebRegistrationController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        model.addAttribute("registerRequest", new RegisterRequest());
        return "register";
    }

    @PostMapping("/register")
    public String processRegistration(@ModelAttribute("registerRequest") RegisterRequest registerRequest, Model model) {
        try {
            userService.registerUser(registerRequest);
            return "redirect:/login?registered";
        } catch (UserAlreadyExistsException | UserNotCreatedException e) {
            model.addAttribute("error", e.getMessage());
            return "register";
        } catch (Exception e) {
            model.addAttribute("error", "Произошла ошибка: " + e.getMessage());
            return "register";
        }
    }
}

