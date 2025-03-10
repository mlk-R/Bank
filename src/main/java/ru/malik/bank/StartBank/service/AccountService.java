package ru.malik.bank.StartBank.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.malik.bank.StartBank.entity.Account;
import ru.malik.bank.StartBank.entity.Transaction;
import ru.malik.bank.StartBank.entity.User;
import ru.malik.bank.StartBank.entity.enumEntity.TransactionType;
import ru.malik.bank.StartBank.repository.AccountRepository;
import ru.malik.bank.StartBank.repository.TransactionRepository;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;

@Service
@Transactional
public class AccountService {

    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;
    private final TransactionService transactionService;

    @Autowired
    public AccountService(AccountRepository accountRepository, TransactionRepository transactionRepository, TransactionService transactionService) {
        this.accountRepository = accountRepository;
        this.transactionRepository = transactionRepository;
        this.transactionService = transactionService;
    }

    @Transactional(readOnly = true)
    public Account findById(Long id) {
        return accountRepository.findById(id).orElse(null);
    }

    public Account createAccount(User user, BigDecimal initialBalance, String accountType) {
        Account account = new Account();
        account.setUser(user);
        account.setBalance(initialBalance.ZERO);
        account.setAccountType(accountType);
        account.setCreatedAt(LocalDateTime.now());
        account.setCardNumber(generateCardNumber());
        account.setExpirationDate(LocalDate.now().plusYears(5));
        account.setCvv(generateCVV());
        return accountRepository.save(account);
    }

    // Генерация номера карты итерационно
    private String generateCardNumber() {
        String lastCardNumber = accountRepository.findMaxCardNumber();
        if (lastCardNumber == null) {
            lastCardNumber = "5280567500000000";
        }
        BigInteger cardNumber = new BigInteger(lastCardNumber);
        cardNumber = cardNumber.add(BigInteger.ONE);
        return String.format("%016d", cardNumber);
    }

    private String generateCVV() {
        Random random = new Random();
        return String.format("%03d", random.nextInt(1000));
    }

    public void topUpAccount(Account account, BigDecimal amount) {
        account.setBalance(account.getBalance().add(amount));
        accountRepository.save(account);

        //Создаем транзакцию Депозит на пополнение счета
        Transaction transaction = new Transaction();
        transaction.setAccount(account);
        transaction.setType(TransactionType.DEPOSIT);
        transaction.setAmount(amount);
        transaction.setStatus("COMPLETED");
        transaction.setCreatedAt(LocalDateTime.now());
        transactionRepository.save(transaction);
    }

    public void withdraw(Account account, BigDecimal amount) {
        BigDecimal newBalance = account.getBalance().subtract(amount);

        if (account.getAccountType().equalsIgnoreCase("DEBIT")) {
            if (newBalance.compareTo(BigDecimal.ZERO) < 0) {
                throw new RuntimeException("Для дебетовой карты баланс не может быть отрицательным");
            }
        } else if (account.getAccountType().equalsIgnoreCase("CREDIT")) {
            BigDecimal creditLimit = new BigDecimal("-100000"); // кредитный лимит
            if (newBalance.compareTo(creditLimit) < 0) {
                throw new RuntimeException("Превышен кредитный лимит");
            }
            // Здесь можно добавить начисление процентов на отрицательный баланс
        }
        account.setBalance(newBalance);
        accountRepository.save(account);

        // Создаем транзакцию для снятия средств (тип WITHDRAWAL)
        Transaction transaction = new Transaction();
        transaction.setAccount(account);
        transaction.setType(TransactionType.WITHDRAWAL); // Тип транзакции для снятия
        transaction.setAmount(amount);
        transaction.setStatus("COMPLETED");
        transaction.setCreatedAt(LocalDateTime.now());
        transactionRepository.save(transaction);
    }

    // Метод для перевода средств с одного аккаунта на другой по номеру карты
    public void transferFunds(Account sourceAccount, String targetCardNumber, BigDecimal amount) {
        // Ищем целевой аккаунт по номеру карты и извлекаем его из Optional
        Account targetAccount = accountRepository.findByCardNumber(targetCardNumber)
                .orElseThrow(() -> new RuntimeException("Аккаунт получателя не найден для карты: " + targetCardNumber));

        // Проверяем баланс исходящего аккаунта
        BigDecimal newSourceBalance = sourceAccount.getBalance().subtract(amount);
        if (sourceAccount.getAccountType().equalsIgnoreCase("DEBIT")) {
            if (newSourceBalance.compareTo(BigDecimal.ZERO) < 0) {
                throw new RuntimeException("Недостаточно средств для перевода с дебетового аккаунта");
            }
        } else if (sourceAccount.getAccountType().equalsIgnoreCase("CREDIT")) {
            BigDecimal creditLimit = new BigDecimal("-100000");
            if (newSourceBalance.compareTo(creditLimit) < 0) {
                throw new RuntimeException("Превышен кредитный лимит");
            }
        }

        // Обновляем баланс исходящего аккаунта
        sourceAccount.setBalance(newSourceBalance);
        accountRepository.save(sourceAccount);

        // Добавляем средства на целевой аккаунт
        targetAccount.setBalance(targetAccount.getBalance().add(amount));
        accountRepository.save(targetAccount);

        // Создаем транзакцию для исходящего аккаунта (списание средств)
        Transaction sourceTransaction = new Transaction();
        sourceTransaction.setAccount(sourceAccount);
        sourceTransaction.setType(TransactionType.TRANSFER);
        sourceTransaction.setAmount(amount);
        sourceTransaction.setStatus("COMPLETED");
        sourceTransaction.setCreatedAt(LocalDateTime.now());
        transactionRepository.save(sourceTransaction);

        // Создаем транзакцию для целевого аккаунта (пополнение средств)
        Transaction targetTransaction = new Transaction();
        targetTransaction.setAccount(targetAccount);
        targetTransaction.setType(TransactionType.TRANSFER);
        targetTransaction.setAmount(amount);
        targetTransaction.setStatus("COMPLETED");
        targetTransaction.setCreatedAt(LocalDateTime.now());
        transactionRepository.save(targetTransaction);
    }

    @Transactional(readOnly = true)
    public List<Account> getAccountsByUser(User user) {
        return accountRepository.findByUser(user);
    }
    @Transactional(readOnly = true)
    public Account getAccountById(Long accountId) {
        return accountRepository.findById(accountId)
                .orElseThrow(() -> new RuntimeException("Аккаунт не найден"));
    }
}
