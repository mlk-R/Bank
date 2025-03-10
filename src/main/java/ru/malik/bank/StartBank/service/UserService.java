package ru.malik.bank.StartBank.service;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.malik.bank.StartBank.dto.loginRegister.LoginRequest;
import ru.malik.bank.StartBank.dto.loginRegister.RegisterRequest;
import ru.malik.bank.StartBank.entity.enumEntity.Role;
import ru.malik.bank.StartBank.entity.User;
import ru.malik.bank.StartBank.repository.UsersRepository;
import ru.malik.bank.StartBank.exception.UserAlreadyExistsException;


import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class UserService {

    private final UsersRepository usersRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserService(UsersRepository usersRepository, PasswordEncoder passwordEncoder) {
        this.usersRepository = usersRepository;
        this.passwordEncoder = passwordEncoder;
    }
    @Transactional(readOnly = true)
    public User findByUsername(String username) {
        return usersRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
    }

    @Transactional(readOnly = true)
    public Optional<User> findById(Long id) {
        return usersRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public User findByUsernameWithAccounts(String username) {
        return usersRepository.findByUsernameWithAccounts(username);
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
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setCreateAt(LocalDateTime.now());
        user.setRole(Role.USER);

        System.out.println("USER CREATING afterALL");

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

    @Transactional(readOnly = true)
    public User getCurrentUser() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof UserDetails) {
            String username = ((UserDetails) principal).getUsername();
            return findByUsername(username);
        }
        return null;
    }
}