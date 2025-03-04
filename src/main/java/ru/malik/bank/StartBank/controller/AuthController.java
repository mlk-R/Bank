package ru.malik.bank.StartBank.controller;

import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import ru.malik.bank.StartBank.dto.LoginRequest;
import ru.malik.bank.StartBank.dto.RegisterRequest;
import ru.malik.bank.StartBank.entity.User;
import ru.malik.bank.StartBank.security.JWTUtil;
import ru.malik.bank.StartBank.service.UserService;
import ru.malik.bank.StartBank.exception.UserNotCreatedException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserService userService;
    private final ModelMapper modelMapper;
    private final JWTUtil jwtUtil;
    private final AuthenticationManager authenticationManager;

    @Autowired
    public AuthController(UserService userService, ModelMapper modelMapper, JWTUtil jwtUtil, AuthenticationManager authenticationManager) {
        this.userService = userService;
        this.modelMapper = modelMapper;
        this.jwtUtil = jwtUtil;
        this.authenticationManager = authenticationManager;
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

    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> login(@RequestBody LoginRequest loginRequest) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword())
            );
            SecurityContextHolder.getContext().setAuthentication(authentication);
            String token = jwtUtil.generateJWTToken(loginRequest.getUsername());

            Map<String, String> response = new HashMap<>();
            response.put("token", token);  // Возвращаем токен клиенту
            return ResponseEntity.ok(response);


        } catch (AuthenticationException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    //Использование Маппера для DTO регистрации

    private User convertRegisterRequestToUser(RegisterRequest request) {
        return modelMapper.map(request, User.class);
    }

    private RegisterRequest convertUserToRegisterRequest(User user) {
        return modelMapper.map(user, RegisterRequest.class);
    }
}
