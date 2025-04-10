package ru.malik.bank.StartBank.dto.loginRegister;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;


public class RegisterRequest {
    @NotEmpty(message = "Имя не должно быть пустым")
    private String username;

    @NotEmpty(message = "Email не может быть пустым")
    @Email(message = "Некорректный email")
    private String email;

    @NotEmpty
    @Size(min = 8, message = "Пароль должен содержать 8 символов!")
    private String password;

    // Геттеры и сеттеры
    public String getUsername() {
        return username;
    }
    public void setUsername(String username) {
        this.username = username;
    }
    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }
    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }
}
