package ru.malik.bank.StartBank.dto.loginRegister;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;



public class LoginRequest {
    @NotEmpty(message = "Имя не должно быть пустым")
    private String username;

    @NotEmpty
    @Size(min = 8, message = "Пароль должен содержать 8 символов!")
    private String password;


    public String getUsername() {
        return username;
    }
    public void setUsername(String username) {
        this.username = username;
    }
    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }
}

