package ru.malik.bank.StartBank.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.malik.bank.StartBank.entity.Account;
import ru.malik.bank.StartBank.entity.Deposit;
import ru.malik.bank.StartBank.entity.Transaction;
import ru.malik.bank.StartBank.entity.User;
import ru.malik.bank.StartBank.entity.enumEntity.Status;
import ru.malik.bank.StartBank.entity.enumEntity.TransactionType;
import ru.malik.bank.StartBank.repository.AccountRepository;
import ru.malik.bank.StartBank.repository.DepositRepository;
import ru.malik.bank.StartBank.repository.TransactionRepository;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class DepositService {

    private final DepositRepository depositRepository;
    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;

    @Autowired
    public DepositService(DepositRepository depositRepository,
                          AccountRepository accountRepository,
                          TransactionRepository transactionRepository) {
        this.depositRepository = depositRepository;
        this.accountRepository = accountRepository;
        this.transactionRepository = transactionRepository;
    }

    // Метод для открытия нового депозита
    @Transactional
    public Deposit createDeposit(BigDecimal amount, Integer term, Long accountId, User user) {
        // Получаем карту по accountId
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new RuntimeException("Карта не найдена"));

        // Проверяем, принадлежит ли карта пользователю
        if (!account.getUser().equals(user)) {
            throw new RuntimeException("Карта не принадлежит этому пользователю");
        }

        // Проверяем, достаточно ли средств на карте для открытия депозита
        if (account.getBalance().compareTo(amount) < 0) {
            throw new RuntimeException("Недостаточный баланс на карте");
        }

        // Списываем сумму депозита с баланса карты
        account.setBalance(account.getBalance().subtract(amount));
        accountRepository.save(account);

        // Создаём новый депозит
        Deposit deposit = new Deposit();
        deposit.setUser(user);
        deposit.setAmount(amount);
        deposit.setTerm(term);
        deposit.setInterestRate(BigDecimal.valueOf(5)); // процент для депозита
        deposit.setStatus(Status.ACTIVE);
        deposit.setCreatedAt(LocalDateTime.now());

        return depositRepository.save(deposit);
    }

    // Получение депозитов по ID пользователя
    public List<Deposit> getDepositsByUserId(Long userId) {
        if (userId == null) {
            throw new IllegalArgumentException("User ID cannot be null");
        }
        return depositRepository.findByUserId(userId);
    }

    // Метод для снятия средств с депозита (если это предусмотрено логикой)
    @Transactional
    public Deposit withdrawDeposit(Long depositId, Long accountId, BigDecimal withdrawalAmount, User user) {
        Deposit deposit = depositRepository.findById(depositId)
                .orElseThrow(() -> new RuntimeException("Депозит не найден"));

        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new RuntimeException("Карта не найдена"));

        if (!account.getUser().equals(user)) {
            throw new RuntimeException("Карта не принадлежит этому пользователю");
        }

        if (deposit.getAmount().compareTo(withdrawalAmount) < 0) {
            throw new RuntimeException("Недостаточный баланс депозита");
        }

        // Уменьшаем сумму депозита
        deposit.setAmount(deposit.getAmount().subtract(withdrawalAmount));
        if (deposit.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            deposit.setStatus(Status.CLOSED);
        }

        // Перечисляем средства на счет пользователя
        account.setBalance(account.getBalance().add(withdrawalAmount));

        Transaction transaction = new Transaction();
        transaction.setAccount(account);
        transaction.setType(TransactionType.DEPOSIT_WITHDRAWAL);
        transaction.setAmount(withdrawalAmount);
        transaction.setStatus("COMPLETED");
        transaction.setCreatedAt(LocalDateTime.now());

        transactionRepository.save(transaction);
        accountRepository.save(account);
        return depositRepository.save(deposit);
    }

    // Метод для ежемесячного начисления процентов на депозит
    @Transactional
    public Deposit accrueDepositInterest(Long depositId) {
        Deposit deposit = depositRepository.findById(depositId)
                .orElseThrow(() -> new RuntimeException("Депозит не найден"));

        if (deposit.getStatus() != Status.ACTIVE) {
            throw new RuntimeException("Депозит не активен");
        }

        BigDecimal monthlyInterestRate = deposit.getInterestRate()
                .divide(BigDecimal.valueOf(12), 6, RoundingMode.HALF_UP);
        BigDecimal interestAccrued = deposit.getAmount()
                .multiply(monthlyInterestRate).setScale(2, RoundingMode.HALF_UP);

        deposit.setAmount(deposit.getAmount().add(interestAccrued));
        return depositRepository.save(deposit);
    }
}
