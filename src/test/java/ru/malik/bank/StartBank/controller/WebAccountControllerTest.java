package ru.malik.bank.StartBank.controller;


import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.test.web.servlet.MockMvc;
import ru.malik.bank.StartBank.controller.web.WebAccountController;
import ru.malik.bank.StartBank.entity.Account;
import ru.malik.bank.StartBank.entity.User;
import ru.malik.bank.StartBank.security.JWTUtil;
import ru.malik.bank.StartBank.service.AccountService;
import ru.malik.bank.StartBank.service.UserService;
import java.math.BigDecimal;
import java.security.Principal;
import java.util.Arrays;
import java.util.List;


import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(WebAccountController.class)
@AutoConfigureMockMvc(addFilters = false) // Отключаем Spring Security для тестов
public class WebAccountControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserService userService;

    @Autowired
    private AccountService accountService;

    // Определяем тестовую конфигурацию для регистрации mock-бинов вместо @MockBean
    @TestConfiguration
    static class ControllerTestConfig {

        @Bean
        @Primary
        public UserService userService() {
            return Mockito.mock(UserService.class);
        }

        @Bean
        @Primary
        public AccountService accountService() {
            return Mockito.mock(AccountService.class);
        }

        // Если фильтр требует JWTUtil, определяем его mock
        @Bean
        @Primary
        public JWTUtil jwtUtil() {
            return Mockito.mock(JWTUtil.class);
        }
    }

    // Вспомогательный метод для создания фиктивного Principal
    private Principal getTestPrincipal() {
        return () -> "testuser";
    }

    @Test
    public void testAccountPage() throws Exception {
        // Подготавливаем фиктивного пользователя с аккаунтами
        User user = new User();
        user.setUsername("testuser");
        List<Account> accounts = Arrays.asList(new Account(), new Account());
        user.setAccounts(accounts);

        when(userService.findByUsernameWithAccounts("testuser")).thenReturn(user);

        mockMvc.perform(get("/account").principal(getTestPrincipal()))
                .andExpect(status().isOk())
                .andExpect(model().attribute("user", user))
                .andExpect(model().attribute("accounts", accounts))
                .andExpect(view().name("account"));
    }

    @Test
    public void testCreateAccount_Success() throws Exception {
        User user = new User();
        user.setUsername("testuser");

        when(userService.findByUsername("testuser")).thenReturn(user);
        // Предполагаем, что метод createAccount возвращает созданный аккаунт (или null, если значение не используется)
        Account newAccount = new Account();
        when(accountService.createAccount(eq(user), eq(new BigDecimal("1000")), eq("SAVINGS")))
                .thenReturn(newAccount);

        mockMvc.perform(post("/account/open")
                        .param("accountType", "SAVINGS")
                        .param("initialBalance", "1000")
                        .principal(getTestPrincipal()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/account"))
                .andExpect(flash().attribute("message", "Аккаунт успешно создан"));
    }

    @Test
    public void testCreateAccount_Failure() throws Exception {
        User user = new User();
        user.setUsername("testuser");

        when(userService.findByUsername("testuser")).thenReturn(user);
        // Для метода, возвращающего значение, используем thenThrow()
        when(accountService.createAccount(eq(user), eq(new BigDecimal("1000")), eq("SAVINGS")))
                .thenThrow(new RuntimeException("Ошибка создания аккаунта"));

        mockMvc.perform(post("/account/open")
                        .param("accountType", "SAVINGS")
                        .param("initialBalance", "1000")
                        .principal(getTestPrincipal()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/account"))
                .andExpect(flash().attribute("error", "Ошибка при создании аккаунта: Ошибка создания аккаунта"));
    }

    @Test
    public void testTopUpAccount_Success() throws Exception {
        Account account = new Account();
        account.setId(1L);

        // getAccountById возвращает значение, поэтому используем when(...).thenReturn(...)
        when(accountService.getAccountById(1L)).thenReturn(account);
        // Предполагаем, что topUpAccount – void-метод, поэтому можно оставить doNothing()
        doNothing().when(accountService).topUpAccount(eq(account), eq(new BigDecimal("500")));

        mockMvc.perform(post("/account/top-up/1")
                        .param("amount", "500"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/account"))
                .andExpect(flash().attribute("message", "Счет успешно пополнен"));
    }

    @Test
    public void testWithdrawFunds_Success() throws Exception {
        Account account = new Account();
        account.setId(1L);

        when(accountService.getAccountById(1L)).thenReturn(account);
        // Предполагаем, что withdraw – void-метод
        doNothing().when(accountService).withdraw(eq(account), eq(new BigDecimal("300")));

        mockMvc.perform(post("/account/withdraw/1")
                        .param("amount", "300"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/account"))
                .andExpect(flash().attribute("message", "Средства успешно выведены"));
    }

    @Test
    public void testWithdrawFunds_Failure() throws Exception {
        Account account = new Account();
        account.setId(1L);

        when(accountService.getAccountById(1L)).thenReturn(account);
        // Если withdraw – void-метод, можно оставить doThrow(). Если метод возвращает значение, замените на when(...).thenThrow()
        doThrow(new RuntimeException("Недостаточно средств"))
                .when(accountService).withdraw(eq(account), eq(new BigDecimal("300")));

        mockMvc.perform(post("/account/withdraw/1")
                        .param("amount", "300"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/account"))
                .andExpect(flash().attribute("error", "Ошибка при снятии средств: Недостаточно средств"));
    }
}