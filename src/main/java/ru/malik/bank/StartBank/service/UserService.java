package ru.malik.bank.StartBank.service;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.malik.bank.StartBank.dto.RegisterRequest;
import ru.malik.bank.StartBank.entity.Role;
import ru.malik.bank.StartBank.entity.User;
import ru.malik.bank.StartBank.repository.UsersRepository;
import ru.malik.bank.StartBank.exception.UserAlreadyExistsException;

import java.time.LocalDateTime;

@Service
public class UserService {

    private final UsersRepository usersRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserService(UsersRepository usersRepository, PasswordEncoder passwordEncoder) {
        this.usersRepository = usersRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public void registerUser(RegisterRequest request) {
        if (usersRepository.existsByUsername(request.getUsername())) {
            throw new UserAlreadyExistsException("Пользователь с таким именем уже существует");
        }

        // Создаем нового пользователя на основе DTO
        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPassword(request.getPassword()); // Сразу пароля не шифруем!

        enrichUser(user);  // Применяем шифрование пароля и другие настройки

        usersRepository.save(user);
    }

    private void enrichUser(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setCreateAt(LocalDateTime.now());
        user.setRole(Role.USER); // Присваиваем роль по умолчанию
    }
}
