package ru.malik.bank.StartBank.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import ru.malik.bank.StartBank.entity.Account;
import ru.malik.bank.StartBank.entity.Loan;
import ru.malik.bank.StartBank.entity.Transaction;
import ru.malik.bank.StartBank.entity.User;
import ru.malik.bank.StartBank.entity.enumEntity.Status;
import ru.malik.bank.StartBank.repository.AccountRepository;
import ru.malik.bank.StartBank.repository.LoanRepository;
import ru.malik.bank.StartBank.repository.TransactionRepository;


import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class LoanServiceTest {

    @InjectMocks
    private LoanService loanService;

    @Mock
    private LoanRepository loanRepository;

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private TransactionRepository transactionRepository;

    @Test
    public void testCreateLoan() {
        // Arrange
        Loan loan = new Loan(); // пустой объект, который будет заполнен сервисом
        User user = new User();
        user.setId(1L);
        BigDecimal amount = BigDecimal.valueOf(1000);

        Loan savedLoan = new Loan();
        savedLoan.setId(1L);
        savedLoan.setUser(user);
        savedLoan.setAmount(amount);
        savedLoan.setInterestRate(BigDecimal.valueOf(10));
        savedLoan.setStatus(Status.ACTIVE);
        savedLoan.setCreatedAt(LocalDateTime.now());

        when(loanRepository.save(any(Loan.class))).thenReturn(savedLoan);

        // Act
        Loan result = loanService.createLoan(loan, amount, user);

        // Assert
        assertNotNull(result);
        assertEquals(user, result.getUser());
        assertEquals(amount, result.getAmount());
        assertEquals(BigDecimal.valueOf(10), result.getInterestRate());
        assertEquals(Status.ACTIVE, result.getStatus());
        verify(loanRepository).save(any(Loan.class));
    }

    @Test
    public void testAccrueLoanInterest_Success() {
        // Arrange
        Loan loan = new Loan();
        loan.setId(1L);
        loan.setAmount(BigDecimal.valueOf(1000));
        loan.setInterestRate(BigDecimal.valueOf(10)); // годовая ставка 10 (не 10%)
        loan.setStatus(Status.ACTIVE);

        when(loanRepository.findById(1L)).thenReturn(Optional.of(loan));
        // При сохранении возвращаем переданный объект
        when(loanRepository.save(any(Loan.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        Loan result = loanService.accrueLoanInterest(1L);

        // Рассчитываем ожидаемое значение:
        // monthlyInterestRate = 10 / 12 = 0.833333 (scale 6)
        // interestAccrued = 1000 * 0.833333 ≈ 833.33 (scale 2)
        // newAmount = 1000 + 833.33 = 1833.33
        BigDecimal expectedNewAmount = new BigDecimal("1833.33");
        assertEquals(expectedNewAmount, result.getAmount());
        verify(loanRepository).findById(1L);
        verify(loanRepository).save(any(Loan.class));
    }

    @Test
    public void testAccrueLoanInterest_LoanNotFound() {
        // Arrange
        when(loanRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        Exception exception = assertThrows(RuntimeException.class, () -> {
            loanService.accrueLoanInterest(1L);
        });
        assertEquals("Кредит не найден", exception.getMessage());
    }

    @Test
    public void testAccrueLoanInterest_NotActive() {
        // Arrange
        Loan loan = new Loan();
        loan.setId(1L);
        loan.setAmount(BigDecimal.valueOf(1000));
        loan.setInterestRate(BigDecimal.valueOf(10));
        loan.setStatus(Status.PAID); // кредит не активен
        when(loanRepository.findById(1L)).thenReturn(Optional.of(loan));

        // Act & Assert
        Exception exception = assertThrows(RuntimeException.class, () -> {
            loanService.accrueLoanInterest(1L);
        });
        assertEquals("Кредит оплачен", exception.getMessage());
    }

    @Test
    public void testGetAllLoans() {
        // Arrange
        List<Loan> loans = List.of(new Loan(), new Loan());
        when(loanRepository.findAll()).thenReturn(loans);

        // Act
        List<Loan> result = loanService.getAllLoans();

        // Assert
        assertEquals(loans.size(), result.size());
        verify(loanRepository).findAll();
    }

    @Test
    public void testGetLoansByUserId_Success() {
        // Arrange
        Long userId = 1L;
        List<Loan> loans = List.of(new Loan(), new Loan());
        when(loanRepository.findByUserId(userId)).thenReturn(loans);

        // Act
        List<Loan> result = loanService.getLoansByUserId(userId);

        // Assert
        assertEquals(loans.size(), result.size());
        verify(loanRepository).findByUserId(userId);
    }

    @Test
    public void testGetLoansByUserId_NullUserId() {
        // Act & Assert
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            loanService.getLoansByUserId(null);
        });
        assertEquals("User ID cannot be null", exception.getMessage());
    }

    @Test
    public void testPayLoan_Success() {
        // Arrange
        Long loanId = 1L;
        Long accountId = 1L;
        User user = new User();
        user.setId(1L);

        Loan loan = new Loan();
        loan.setId(loanId);
        loan.setAmount(BigDecimal.valueOf(1000));
        loan.setStatus(Status.ACTIVE);

        Account account = new Account();
        account.setId(accountId);
        account.setUser(user);
        account.setBalance(BigDecimal.valueOf(1200));

        BigDecimal paymentAmount = BigDecimal.valueOf(300);

        when(loanRepository.findById(loanId)).thenReturn(Optional.of(loan));
        when(accountRepository.findById(accountId)).thenReturn(Optional.of(account));
        when(transactionRepository.save(any(Transaction.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(accountRepository.save(any(Account.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(loanRepository.save(any(Loan.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        Loan result = loanService.payLoan(loanId, accountId, paymentAmount, user);

        // Assert
        // Проверяем, что баланс карты уменьшился: 1200 - 300 = 900
        assertEquals(BigDecimal.valueOf(900), account.getBalance());
        // Сумма кредита уменьшилась: 1000 - 300 = 700
        assertEquals(BigDecimal.valueOf(700), result.getAmount());
        // Поскольку остаток кредита больше 0, статус остается ACTIVE
        assertEquals(Status.ACTIVE, result.getStatus());
        verify(transactionRepository).save(any(Transaction.class));
        verify(accountRepository).save(any(Account.class));
        verify(loanRepository).save(any(Loan.class));
    }

    @Test
    public void testPayLoan_AccountNotBelongToUser() {
        // Arrange
        Long loanId = 1L;
        Long accountId = 1L;
        User user = new User();
        user.setId(1L);

        User otherUser = new User();
        otherUser.setId(2L);

        Loan loan = new Loan();
        loan.setId(loanId);
        loan.setAmount(BigDecimal.valueOf(1000));
        loan.setStatus(Status.ACTIVE);

        Account account = new Account();
        account.setId(accountId);
        account.setUser(otherUser); // не тот пользователь
        account.setBalance(BigDecimal.valueOf(1200));

        BigDecimal paymentAmount = BigDecimal.valueOf(300);

        when(loanRepository.findById(loanId)).thenReturn(Optional.of(loan));
        when(accountRepository.findById(accountId)).thenReturn(Optional.of(account));

        // Act & Assert
        Exception exception = assertThrows(RuntimeException.class, () -> {
            loanService.payLoan(loanId, accountId, paymentAmount, user);
        });
        assertEquals("Карта не принадлежит этому пользователю", exception.getMessage());
    }

    @Test
    public void testPayLoan_InsufficientBalance() {
        // Arrange
        Long loanId = 1L;
        Long accountId = 1L;
        User user = new User();
        user.setId(1L);

        Loan loan = new Loan();
        loan.setId(loanId);
        loan.setAmount(BigDecimal.valueOf(1000));
        loan.setStatus(Status.ACTIVE);

        Account account = new Account();
        account.setId(accountId);
        account.setUser(user);
        account.setBalance(BigDecimal.valueOf(200)); // недостаточно средств

        BigDecimal paymentAmount = BigDecimal.valueOf(300);

        when(loanRepository.findById(loanId)).thenReturn(Optional.of(loan));
        when(accountRepository.findById(accountId)).thenReturn(Optional.of(account));

        // Act & Assert
        Exception exception = assertThrows(RuntimeException.class, () -> {
            loanService.payLoan(loanId, accountId, paymentAmount, user);
        });
        assertEquals("Недостаточный баланс", exception.getMessage());
    }

    @Test
    public void testPayLoan_LoanNotFound() {
        // Arrange
        Long loanId = 1L;
        Long accountId = 1L;
        User user = new User();
        user.setId(1L);
        BigDecimal paymentAmount = BigDecimal.valueOf(300);

        when(loanRepository.findById(loanId)).thenReturn(Optional.empty());

        // Act & Assert
        Exception exception = assertThrows(RuntimeException.class, () -> {
            loanService.payLoan(loanId, accountId, paymentAmount, user);
        });
        assertEquals("Кредит не найден", exception.getMessage());
    }

    @Test
    public void testPayLoan_AccountNotFound() {
        // Arrange
        Long loanId = 1L;
        Long accountId = 1L;
        User user = new User();
        user.setId(1L);

        Loan loan = new Loan();
        loan.setId(loanId);
        loan.setAmount(BigDecimal.valueOf(1000));
        loan.setStatus(Status.ACTIVE);

        BigDecimal paymentAmount = BigDecimal.valueOf(300);

        when(loanRepository.findById(loanId)).thenReturn(Optional.of(loan));
        when(accountRepository.findById(accountId)).thenReturn(Optional.empty());

        // Act & Assert
        Exception exception = assertThrows(RuntimeException.class, () -> {
            loanService.payLoan(loanId, accountId, paymentAmount, user);
        });
        assertEquals("Карта не найдена", exception.getMessage());
    }
}
