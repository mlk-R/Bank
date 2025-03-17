package ru.malik.bank.StartBank.service;


import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.malik.bank.StartBank.entity.Account;
import ru.malik.bank.StartBank.entity.Transaction;
import ru.malik.bank.StartBank.entity.User;
import ru.malik.bank.StartBank.repository.AccountRepository;
import ru.malik.bank.StartBank.repository.TransactionRepository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;


import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
public class AccountServiceTest {

    @InjectMocks
    private AccountService accountService;

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private TransactionRepository transactionRepository;

    @Test
    public void TestCreateAccount() {
        User user = new User();
        user.setId(1L);
        BigDecimal initialBalance = BigDecimal.valueOf(1000);
        String accountType = "DEBIT";

        Account account = new Account();
        account.setId(1L);
        account.setUser(user);
        account.setAccountType(accountType);
        account.setBalance(initialBalance);
        account.setCardNumber("5280567500000001");
        account.setCreatedAt(LocalDateTime.now());
        account.setExpirationDate(LocalDate.now().plusYears(5));
        account.setCvv("123");

        when(accountRepository.save(any(Account.class))).thenReturn(account);


        Account result = accountService.createAccount(user, initialBalance, accountType);

        assertNotNull(result);
        assertEquals(user, result.getUser());
        assertEquals(accountType, result.getAccountType());
        assertEquals(initialBalance, result.getBalance());

        verify(accountRepository).save(any(Account.class));
    }

    @Test
    public void TestTopUpAccount() {
        Account account = new Account();
        account.setId(1L);
        account.setBalance(BigDecimal.valueOf(500));

        BigDecimal topUpBalance = BigDecimal.valueOf(200);

        when(accountRepository.save(account)).thenReturn(account);

        accountService.topUpAccount(account, topUpBalance);

        assertEquals(BigDecimal.valueOf(700), account.getBalance());
        verify(accountRepository).save(account);
        verify(transactionRepository).save(any(Transaction.class));
    }

    @Test
    public void testWithdrawDebitSuccess() {
        // Arrange: создаем дебетовый счет с балансом 1000
        Account account = new Account();
        account.setId(1L);
        account.setBalance(BigDecimal.valueOf(1000));
        account.setAccountType("DEBIT");

        BigDecimal withdrawAmount = BigDecimal.valueOf(300);
        when(accountRepository.save(account)).thenReturn(account);

        // Act
        accountService.withdraw(account, withdrawAmount);

        // Assert: баланс должен стать 700
        assertEquals(BigDecimal.valueOf(700), account.getBalance());
        verify(accountRepository).save(account);
        verify(transactionRepository).save(any(Transaction.class));
    }

    @Test
    public void testWithdrawDebitInsufficientFunds() {
        // Arrange: создаем дебетовый счет с балансом 100
        Account account = new Account();
        account.setId(1L);
        account.setBalance(BigDecimal.valueOf(100));
        account.setAccountType("DEBIT");

        BigDecimal withdrawAmount = BigDecimal.valueOf(200);

        // Act & Assert: ожидаем выброс RuntimeException с соответствующим сообщением
        Exception exception = assertThrows(RuntimeException.class, () -> {
            accountService.withdraw(account, withdrawAmount);
        });
        assertEquals("Для дебетовой карты баланс не может быть отрицательным", exception.getMessage());
    }

    @Test
    public void testTransferFundsSuccess() {
        // Arrange: исходный счет с балансом 1000, дебетовый
        Account sourceAccount = new Account();
        sourceAccount.setId(1L);
        sourceAccount.setBalance(BigDecimal.valueOf(1000));
        sourceAccount.setAccountType("DEBIT");

        // Целевой счет с балансом 500
        Account targetAccount = new Account();
        targetAccount.setId(2L);
        targetAccount.setBalance(BigDecimal.valueOf(500));
        targetAccount.setAccountType("DEBIT");
        targetAccount.setCardNumber("1234567890123456");

        BigDecimal transferAmount = BigDecimal.valueOf(300);

        // Настраиваем мок поиска счета по номеру карты
        when(accountRepository.findByCardNumber("1234567890123456")).thenReturn(Optional.of(targetAccount));
        when(accountRepository.save(sourceAccount)).thenReturn(sourceAccount);
        when(accountRepository.save(targetAccount)).thenReturn(targetAccount);

        // Act
        accountService.transferFunds(sourceAccount, "1234567890123456", transferAmount);

        // Assert: баланс исходного счета должен уменьшиться, целевого – увеличиться
        assertEquals(BigDecimal.valueOf(700), sourceAccount.getBalance());
        assertEquals(BigDecimal.valueOf(800), targetAccount.getBalance());

        verify(accountRepository).save(sourceAccount);
        verify(accountRepository).save(targetAccount);
        // Должно быть сохранено две транзакции: для исходящего и для входящего счета
        verify(transactionRepository, times(2)).save(any(Transaction.class));
    }

    @Test
    public void testGetAccountById_NotFound() {
        // Arrange: эмулируем ситуацию, когда счет не найден
        when(accountRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert: ожидаем выброс исключения с сообщением "Аккаунт не найден"
        Exception exception = assertThrows(RuntimeException.class, () -> {
            accountService.getAccountById(1L);
        });
        assertEquals("Аккаунт не найден", exception.getMessage());
    }
}
