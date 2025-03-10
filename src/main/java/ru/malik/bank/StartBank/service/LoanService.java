package ru.malik.bank.StartBank.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.malik.bank.StartBank.entity.Account;
import ru.malik.bank.StartBank.entity.Loan;
import ru.malik.bank.StartBank.entity.Transaction;
import ru.malik.bank.StartBank.entity.User;
import ru.malik.bank.StartBank.entity.enumEntity.Status;
import ru.malik.bank.StartBank.entity.enumEntity.TransactionType;
import ru.malik.bank.StartBank.repository.AccountRepository;
import ru.malik.bank.StartBank.repository.LoanRepository;
import ru.malik.bank.StartBank.repository.TransactionRepository;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class LoanService {

    private final LoanRepository loanRepository;
    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;

    @Autowired
    public LoanService(LoanRepository loanRepository, AccountRepository accountRepository, TransactionRepository transactionRepository) {
        this.loanRepository = loanRepository;
        this.accountRepository = accountRepository;
        this.transactionRepository = transactionRepository;
    }
    @Transactional
    public Loan createLoan(Loan loan, BigDecimal amount, User user) {
        loan.setUser(user);
        loan.setAmount(amount);
        loan.setInterestRate(BigDecimal.valueOf(10));
        loan.setStatus(Status.ACTIVE);
        loan.setCreatedAt(LocalDateTime.now());
        return loanRepository.save(loan);

    }
    @Transactional
    public Loan accrueLoanInterest(Long loanId) {
        Loan loan = loanRepository.findById(loanId)
                .orElseThrow(() -> new RuntimeException("Кредит не найден"));

        if(loan.getStatus() != Status.ACTIVE) {
            throw new RuntimeException("Кредит оплачен");
        }
        BigDecimal montlyInterestRate = loan.getInterestRate()
                .divide(BigDecimal.valueOf(12), 6, RoundingMode.HALF_UP);

        BigDecimal interestAccrued = loan.getAmount()
                .multiply(montlyInterestRate).setScale(2, RoundingMode.HALF_UP);

        loan.setAmount(loan.getAmount().add(interestAccrued));

        return loanRepository.save(loan);
    }

    public List<Loan> getAllLoans() {
        return loanRepository.findAll();
    }

    // Получение кредитов пользователя
    public List<Loan> getLoansByUserId(Long userId) {
        if (userId == null) {
            throw new IllegalArgumentException("User ID cannot be null");
        }
        return loanRepository.findByUserId(userId);
    }

    @Transactional
    public Loan payLoan(Long loanId, Long accountId, BigDecimal paymentAmount, User user) {

        Loan loan = loanRepository.findById(loanId).orElseThrow(() -> new RuntimeException("Кредит не найден"));

        Account account = accountRepository.findById(accountId).orElseThrow(() -> new RuntimeException("Карта не найдена"));

        if (!account.getUser().equals(user)) {
            throw new RuntimeException("Карта не принадлежит этому пользователю");
        };

        if (account.getBalance().compareTo(paymentAmount) < 0) {
            throw new RuntimeException("Недостаточный баланс");
        }

        account.setBalance(account.getBalance().subtract(paymentAmount));

        loan.setAmount(loan.getAmount().subtract(paymentAmount));

        if (loan.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            loan.setStatus(Status.PAID);
        }

        Transaction transaction = new Transaction();
        transaction.setAccount(account);
        transaction.setType(TransactionType.CREDIT_PAYMENT);
        transaction.setAmount(paymentAmount);
        transaction.setStatus("COMPLETED");
        transaction.setCreatedAt(LocalDateTime.now());

        transactionRepository.save(transaction);
        accountRepository.save(account);
        return loanRepository.save(loan);
    }
}
