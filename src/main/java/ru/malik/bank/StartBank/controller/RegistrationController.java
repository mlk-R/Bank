package ru.malik.bank.StartBank.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import ru.malik.bank.StartBank.dto.RegisterRequest;
import ru.malik.bank.StartBank.exception.UserAlreadyExistsException;
import ru.malik.bank.StartBank.exception.UserNotCreatedException;
import ru.malik.bank.StartBank.service.UserService;

@Controller
public class RegistrationController {

    private final UserService userService;


    @Autowired
    public RegistrationController(UserService userService) {
        this.userService = userService;
    }


    // Отображение страницы регистрации
    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        model.addAttribute("registerRequest", new RegisterRequest());
        return "register";
    }

    // Обработка данных регистрации
    @PostMapping("/register")
    public String proccessRegistration(@ModelAttribute("registerRequest") RegisterRequest registerRequest, Model model) {
        try {
            userService.registerUser(registerRequest);
            return "redirect:/login?registered"; // Редирект на страницу логина после успешной регистрации
        } catch (UserAlreadyExistsException | UserNotCreatedException e) {
            model.addAttribute("error", e.getMessage());  // Добавляем ошибку в модель
            return "register";  // Возвращаемся на страницу регистрации с сообщением об ошибке
        } catch (Exception e) {
            model.addAttribute("error", "Произошла ошибка: " + e.getMessage());  // Добавляем общую ошибку
            return "register";  // Возвращаемся на страницу регистрации с общей ошибкой
        }
    }
}
