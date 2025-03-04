package ru.malik.bank.StartBank.controller;

import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import ru.malik.bank.StartBank.dto.RegisterRequest;
import ru.malik.bank.StartBank.entity.User;
import ru.malik.bank.StartBank.service.UserService;
import ru.malik.bank.StartBank.exception.UserNotCreatedException;

import java.util.List;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserService userService;
    private final ModelMapper modelMapper;

    @Autowired
    public AuthController(UserService userService, ModelMapper modelMapper) {
        this.userService = userService;
        this.modelMapper = modelMapper;
    }

    @PostMapping("/registration")
    public ResponseEntity<RegisterRequest> register(@RequestBody @Valid RegisterRequest request, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            StringBuilder errors = new StringBuilder();

            List<FieldError> fieldErrors = bindingResult.getFieldErrors();
            for (FieldError fieldError : fieldErrors) {
                errors.append(fieldError.getField())
                        .append(":")
                        .append(fieldError.getDefaultMessage())
                        .append(";");
            }
            throw new UserNotCreatedException(errors.toString());
        }

    User user = convertRegisterRequestToUser(request);
    userService.registerUser(request);
    return ResponseEntity.ok(convertUserToRegisterRequest(user));
    }

    //Использование Маппера для DTO регистрации

    private User convertRegisterRequestToUser(RegisterRequest request) {
        return modelMapper.map(request, User.class);
    }

    private RegisterRequest convertUserToRegisterRequest(User user) {
        return modelMapper.map(user, RegisterRequest.class);
    }
}
