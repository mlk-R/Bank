package ru.malik.bank.StartBank.security;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import ru.malik.bank.StartBank.entity.User;

import java.util.Collection;
import java.util.List;

public class CustomUserDetails implements UserDetails {

    private final User user;

    public CustomUserDetails(User user) {
        this.user = user;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + user.getRole()));
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getUsername();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true; // Можно реализовать логику, если аккаунт может быть просрочен
    }

    @Override
    public boolean isAccountNonLocked() {
        return true; // Можно реализовать логику для заблокированных пользователей
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true; // Можно реализовать логику для истекших учетных данных
    }

    @Override
    public boolean isEnabled() {
        return true; // Можно добавить логику для отключенных пользователей
    }

    public User getUser() {
        return this.user;
    }
}
