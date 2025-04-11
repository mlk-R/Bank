package ru.malik.bank.StartBank.util;

import ru.malik.bank.StartBank.entity.Account;
import ru.malik.bank.StartBank.entity.User;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class AccountBuilder {
    private User user;
    private BigDecimal balance;
    private String accountType;
    private LocalDateTime createdAt;
    private String cardNumber;
    private LocalDate expirationDate;
    private String cvv;

    public AccountBuilder setUser(User user) {
        this.user = user;
        return this;
    }

    public AccountBuilder setBalance(BigDecimal balance) {
        this.balance = balance;
        return this;
    }

    public AccountBuilder setAccountType(String accountType) {
        this.accountType = accountType;
        return this;
    }

    public AccountBuilder setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
        return this;
    }

    public AccountBuilder setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
        return this;
    }

    public AccountBuilder setExpirationDate(LocalDate expirationDate) {
        this.expirationDate = expirationDate;
        return this;
    }

    public AccountBuilder setCvv(String cvv) {
        this.cvv = cvv;
        return this;
    }

    public Account build() {
        Account account = new Account();
        account.setUser(user);
        account.setBalance(balance);
        account.setAccountType(accountType);
        account.setCreatedAt(createdAt);
        account.setCardNumber(cardNumber);
        account.setExpirationDate(expirationDate);
        account.setCvv(cvv);
        return account;
    }
}
