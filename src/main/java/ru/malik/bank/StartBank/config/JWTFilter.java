package ru.malik.bank.StartBank.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import ru.malik.bank.StartBank.security.JWTUtil;


import java.io.IOException;
import java.util.ArrayList;

@Component
public class JWTFilter extends OncePerRequestFilter {

    private final JWTUtil jwtUtil;

    @Autowired
    public JWTFilter(JWTUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String token = request.getHeader("Authorization");

        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7); // Убираем префикс "Bearer "

            try {
                // Проверка токена (например, с помощью JWT)
                Claims claims = Jwts.parser()
                        .setSigningKey(jwtUtil.getSigningKey())  // Используем ключ из JWTUtil
                        .parseClaimsJws(token)
                        .getBody();

                // Настроить SecurityContext, если токен валиден
                Authentication authentication = new UsernamePasswordAuthenticationToken(claims.getSubject(), null, new ArrayList<>());
                SecurityContextHolder.getContext().setAuthentication(authentication);
            } catch (Exception e) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.getWriter().write("Ошибка аутентификации");
                return;
            }
        }

        filterChain.doFilter(request, response);
    }
}
