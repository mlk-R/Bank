package ru.malik.bank.StartBank.service;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.malik.bank.StartBank.dto.LoginRequest;
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

    //Блок регистрации пользователя
    @Transactional
    public User registerUser(RegisterRequest request) {
        if (usersRepository.findByUsername(request.getUsername()).isPresent()) {
            throw new UserAlreadyExistsException("Пользователь с таким именем уже существует");
        }

        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setCreateAt(LocalDateTime.now());
        user.setRole(Role.USER);

        return usersRepository.save(user);
    }

    //Блок Авторизации пользователя
    @Transactional
    public void loginUser(LoginRequest request) {
        if (!usersRepository.existsByUsername(request.getUsername())) {
            throw new UsernameNotFoundException("Пользователь с именем "
                    + request.getUsername() + " не найден");
        }
        User user = usersRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new UsernameNotFoundException("Пользователь с именем "
                        + request.getUsername() + " не найден"));
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new BadCredentialsException("Неверный пароль");
        }

        System.out.println("Пользователь " + user.getUsername() + " успешно вошёл в систему");
    }
}