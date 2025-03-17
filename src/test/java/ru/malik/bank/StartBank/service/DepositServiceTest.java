package ru.malik.bank.StartBank.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.malik.bank.StartBank.entity.Account;
import ru.malik.bank.StartBank.entity.Deposit;
import ru.malik.bank.StartBank.entity.Transaction;
import ru.malik.bank.StartBank.entity.User;
import ru.malik.bank.StartBank.entity.enumEntity.Status;
import ru.malik.bank.StartBank.repository.AccountRepository;
import ru.malik.bank.StartBank.repository.DepositRepository;
import ru.malik.bank.StartBank.repository.TransactionRepository;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class DepositServiceTest {

    @Mock
    private DepositRepository depositRepository;
    @Mock
    private AccountRepository accountRepository;
    @Mock
    private TransactionRepository transactionRepository;

    @InjectMocks
    private DepositService depositService;

    private Account account;
    private User user;
    private Deposit deposit;

    @BeforeEach
    void setUp() {
        // Инициализация пользователя
        user = new User();
        user.setId(1L);

        // Инициализация карты (Account)
        account = new Account();
        account.setId(1L);
        account.setUser(user);
        account.setBalance(BigDecimal.valueOf(1000)); // Исходный баланс 1000

        // Инициализация депозита
        deposit = new Deposit();
        deposit.setId(1L);
        deposit.setUser(user);
        deposit.setAmount(BigDecimal.valueOf(500));
        deposit.setTerm(12);
        deposit.setInterestRate(BigDecimal.valueOf(5)); // процент для депозита
        deposit.setStatus(Status.ACTIVE);
        deposit.setCreatedAt(LocalDateTime.now());
    }

    @Test
    void testCreateDeposit() {
        BigDecimal depositAmount = BigDecimal.valueOf(200);
        Integer term = 12;
        Long accountId = account.getId();

        // Мокаем поиск карты
        when(accountRepository.findById(accountId)).thenReturn(Optional.of(account));
        // Мокаем сохранение депозита
        when(depositRepository.save(any(Deposit.class))).thenAnswer(invocation -> {
            Deposit savedDeposit = invocation.getArgument(0);
            savedDeposit.setId(1L);
            return savedDeposit;
        });

        Deposit createdDeposit = depositService.createDeposit(depositAmount, term, accountId, user);

        assertNotNull(createdDeposit);
        assertEquals(user, createdDeposit.getUser());
        assertEquals(depositAmount, createdDeposit.getAmount());
        assertEquals(term, createdDeposit.getTerm());
        // Проверяем, что баланс карты уменьшился
        assertEquals(BigDecimal.valueOf(800), account.getBalance());
        verify(accountRepository, times(1)).save(account);
        verify(depositRepository, times(1)).save(any(Deposit.class));
    }

    @Test
    void testGetDepositsByUserId() {
        Long userId = user.getId();
        when(depositRepository.findByUserId(userId)).thenReturn(Arrays.asList(deposit));

        List<Deposit> deposits = depositService.getDepositsByUserId(userId);

        assertNotNull(deposits);
        assertFalse(deposits.isEmpty());
        assertEquals(1, deposits.size());
        verify(depositRepository, times(1)).findByUserId(userId);
    }

    @Test
    void testWithdrawDeposit() {
        // Сценарий: сумма депозита равна сумме снятия, поэтому депозит закрывается
        Long depositId = deposit.getId();
        Long accountId = account.getId();
        BigDecimal withdrawalAmount = BigDecimal.valueOf(500);

        when(depositRepository.findById(depositId)).thenReturn(Optional.of(deposit));
        when(accountRepository.findById(accountId)).thenReturn(Optional.of(account));
        // Мокаем сохранение транзакции
        when(transactionRepository.save(any(Transaction.class))).thenAnswer(invocation -> {
            Transaction t = invocation.getArgument(0);
            t.setId(1L);
            return t;
        });
        when(depositRepository.save(any(Deposit.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(accountRepository.save(any(Account.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Deposit updatedDeposit = depositService.withdrawDeposit(depositId, accountId, withdrawalAmount, user);

        // После списания депозита его сумма должна стать 0, а статус — CLOSED
        assertEquals(BigDecimal.ZERO, updatedDeposit.getAmount());
        assertEquals(Status.CLOSED, updatedDeposit.getStatus());
        // Баланс карты должен увеличиться на сумму снятия
        assertEquals(BigDecimal.valueOf(1500), account.getBalance());

        verify(transactionRepository, times(1)).save(any(Transaction.class));
        verify(accountRepository, times(1)).save(account);
        verify(depositRepository, times(1)).save(deposit);
    }

    @Test
    void testAccrueDepositInterest() {
        // Настраиваем депозит для начисления процентов
        deposit.setAmount(BigDecimal.valueOf(1000));
        deposit.setInterestRate(BigDecimal.valueOf(12)); // 12% годовых, что дает месячную ставку 1 (без деления на 100, согласно логике)
        when(depositRepository.findById(deposit.getId())).thenReturn(Optional.of(deposit));
        when(depositRepository.save(any(Deposit.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Deposit updatedDeposit = depositService.accrueDepositInterest(deposit.getId());

        // Расчет по логике: monthlyInterestRate = 12 / 12 = 1; interestAccrued = 1000 * 1 = 1000
        // Ожидаемый итоговый остаток = 1000 + 1000 = 2000
        BigDecimal expectedAmount = BigDecimal.valueOf(2000).setScale(2, RoundingMode.HALF_UP);
        assertEquals(expectedAmount, updatedDeposit.getAmount());
        verify(depositRepository, times(1)).save(deposit);
    }
}
