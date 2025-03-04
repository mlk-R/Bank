package ru.malik.bank.StartBank.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;


@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/admin").hasRole("ADMIN")
                        .requestMatchers("/api/auth/login","/api/auth/registration", "/api/auth/register").permitAll() // Разрешаем всем
                        .anyRequest().hasAnyRole("USER", "ADMIN") // Все остальные запросы требуют входа
                )
                .formLogin(form -> form
                        .loginPage("/auth/login") // Страница логина
                        .loginProcessingUrl("/process_login") // URL обработки логина
                        .defaultSuccessUrl("/hello", true) // После успешного входа
                        .failureUrl("/auth/login?error") // При ошибке входа
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutUrl("/logout") // URL для выхода
                        .logoutSuccessUrl("/auth/login?logout") // Перенаправление после выхода
                        .invalidateHttpSession(true) // Инвалидация сессии
                        .deleteCookies("JSESSIONID") // Удаление куки
                        .permitAll()
                )
                .csrf(csrf -> csrf.disable());


        return http.build();
    }































//    @Bean
//    public AuthenticationManager authenticationManager(HttpSecurity http) throws Exception {
//        AuthenticationManagerBuilder authenticationManagerBuilder =
//                http.getSharedObject(AuthenticationManagerBuilder.class);
//        authenticationManagerBuilder
//                .userDetailsService(personDetailsService) // Используем кастомный UserDetailsService
//                .passwordEncoder(passwordEncoder()); // Используем BCryptPasswordEncoder
//
//        return authenticationManagerBuilder.build();
//    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(); // Используем BCryptPasswordEncoder для безопасности
    }
}
