package ru.malik.bank.StartBank.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import ru.malik.bank.StartBank.dto.loginRegister.LoginRequest;
import ru.malik.bank.StartBank.dto.loginRegister.RegisterRequest;
import ru.malik.bank.StartBank.entity.User;
import ru.malik.bank.StartBank.exception.UserAlreadyExistsException;
import ru.malik.bank.StartBank.repository.UsersRepository;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.never;
import static org.mockito.ArgumentMatchers.any;


import java.time.LocalDateTime;
import java.util.Optional;


import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;


@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UsersRepository usersRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    @Test
    public void testRegisterUser_Success() {
        // Arrange: Создаем объект запроса регистрации
        RegisterRequest request = new RegisterRequest();
        request.setUsername("testuser");
        request.setEmail("test@example.com");
        request.setPassword("password");

        // Предполагаем, что пользователь с таким именем отсутствует
        when(usersRepository.findByUsername("testuser")).thenReturn(Optional.empty());
        // Предполагаем, что пароль будет успешно закодирован
        when(passwordEncoder.encode("password")).thenReturn("encodedPassword");
        // Имитируем сохранение пользователя: возвращаем сохраненный объект с заполненным id и другими данными
        User savedUser = new User();
        savedUser.setId(1L);
        savedUser.setUsername("testuser");
        savedUser.setEmail("test@example.com");
        savedUser.setPassword("encodedPassword");
        savedUser.setCreateAt(LocalDateTime.now());
        // Здесь можно установить и роль
        when(usersRepository.save(any(User.class))).thenReturn(savedUser);

        // Act: Вызываем метод регистрации
        User result = userService.registerUser(request);

        // Assert: Проверяем, что возвращенный объект корректен
        assertNotNull(result);
        assertEquals("testuser", result.getUsername());
        assertEquals("test@example.com", result.getEmail());
        assertEquals("encodedPassword", result.getPassword());

        // Верифицируем вызовы зависимостей
        verify(usersRepository).findByUsername("testuser");
        verify(passwordEncoder).encode("password");
        verify(usersRepository).save(any(User.class));
    }

    @Test
    public void testRegisterUser_UserAlreadyExists() {
        // Arrange: Создаем запрос регистрации для существующего пользователя
        RegisterRequest request = new RegisterRequest();
        request.setUsername("testuser"); // или "existingUser", если хотите, чтобы имя было такое же
        request.setEmail("test@example.com");
        request.setPassword("password");

        User existingUser = new User();
        existingUser.setUsername("testuser"); // имя должно совпадать с запросом
        // Имитация того, что такой пользователь уже есть в системе
        when(usersRepository.findByUsername("testuser")).thenReturn(Optional.of(existingUser));

        // Act & Assert: Ожидаем, что метод выбросит исключение
        Exception exception = assertThrows(UserAlreadyExistsException.class, () -> {
            userService.registerUser(request);
        });

        assertEquals("Пользователь с таким именем уже существует", exception.getMessage());
        verify(usersRepository).findByUsername("testuser");
        verify(passwordEncoder, never()).encode(any());
        verify(usersRepository, never()).save(any());
    }

    @Test
    public void testLoginUser_Success() {
        // Arrange: Создаем объект запроса входа
        LoginRequest request = new LoginRequest();
        request.setUsername("testuser");
        request.setPassword("password");

        User user = new User();
        user.setUsername("testuser");
        user.setPassword("encodedPassword");

        // Предполагаем, что пользователь существует
        when(usersRepository.existsByUsername("testuser")).thenReturn(true);
        when(usersRepository.findByUsername("testuser")).thenReturn(Optional.of(user));
        // Предполагаем, что пароль корректный
        when(passwordEncoder.matches("password", "encodedPassword")).thenReturn(true);

        // Act: Вызываем метод входа
        userService.loginUser(request);

        // Assert: Если исключений нет, считаем тест успешным
        verify(usersRepository).existsByUsername("testuser");
        verify(usersRepository).findByUsername("testuser");
        verify(passwordEncoder).matches("password", "encodedPassword");
    }

    @Test
    public void testLoginUser_InvalidPassword() {

        LoginRequest request = new LoginRequest();
        request.setUsername("testuser");
        request.setPassword("wrongPassword");

        User user = new User();
        user.setUsername("testuser");
        user.setPassword("encodedPassword");

        when(usersRepository.existsByUsername("testuser")).thenReturn(true);
        when(usersRepository.findByUsername("testuser")).thenReturn(Optional.of(user));

        when(passwordEncoder.matches("wrongPassword", "encodedPassword")).thenReturn(false);


        Exception exception = assertThrows(BadCredentialsException.class, () -> {
            userService.loginUser(request);
        });

        assertEquals("Неверный пароль", exception.getMessage());
        verify(usersRepository).existsByUsername("testuser");
        verify(usersRepository).findByUsername("testuser");
        verify(passwordEncoder).matches("wrongPassword", "encodedPassword");
    }
}