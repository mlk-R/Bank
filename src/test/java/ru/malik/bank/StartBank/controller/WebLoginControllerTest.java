package ru.malik.bank.StartBank.controller;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.malik.bank.StartBank.controller.web.WebLoginController;
import ru.malik.bank.StartBank.dto.loginRegister.LoginRequest;
import ru.malik.bank.StartBank.service.UserService;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(WebLoginController.class)
@AutoConfigureMockMvc(addFilters = false)
public class WebLoginControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserService userService;

    @TestConfiguration
    static class WebLoginControllerTestConfiguration {
        @Bean
        @Primary
        public UserService userService() {
            return Mockito.mock(UserService.class);
        }
    }

    @Test
    public void testShowLoginForm_WithRegisteredParam() throws Exception {
        mockMvc.perform(get("/login").param("registered", "true"))
                .andExpect(status().isOk())
                .andExpect(view().name("login"))
                .andExpect(model().attributeExists("message"))
                .andExpect(model().attribute("message", "Вы успешно зарегистрировались! Пожалуйста, войдите в систему."));
    }

    @Test
    public void testShowLoginForm_WithoutRegisteredParam() throws Exception {
        mockMvc.perform(get("/login"))
                .andExpect(status().isOk())
                .andExpect(view().name("login"))
                .andExpect(model().attributeDoesNotExist("message"));
    }

    @Test
    void testProcessLogin_Success() throws Exception {
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setUsername("username");
        loginRequest.setPassword("password");

        doNothing().when(userService).loginUser(any(LoginRequest.class));

        mockMvc.perform(post("/login")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("username", loginRequest.getUsername())
                        .param("password", loginRequest.getPassword()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/account"));

        verify(userService, times(2)).loginUser(any(LoginRequest.class));
    }

    @Test
    public void testProcessLogin_Fail() throws Exception {
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setUsername("testuser");
        loginRequest.setPassword("wrongpassword");

        doThrow(new RuntimeException("неверный логин и пароль"))
                .when(userService).loginUser(any(LoginRequest.class));

        mockMvc.perform(post("/login")
                .param("username", loginRequest.getUsername())
                .param("password", loginRequest.getPassword()))
                .andExpect(status().isOk())
                .andExpect(view().name("login"))
                .andExpect(model().attributeExists("error"))
                .andExpect(model().attribute("error", "неверный логин и пароль"));

        verify(userService, times(1)).loginUser(any(LoginRequest.class));
    }
}
