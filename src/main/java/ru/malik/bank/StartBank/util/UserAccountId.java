package ru.malik.bank.StartBank.util;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

import java.io.Serializable;

@Embeddable
public class UserAccountId implements Serializable {
    @Column(name = "user_id")
    private Long userId;

    @Column(name = "account_id")
    private Long accountId;

    public UserAccountId() {
    }

    public UserAccountId(Long userId, Long accountId) {
        this.userId = userId;
        this.accountId = accountId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getAccountId() {
        return accountId;
    }

    public void setAccountId(Long accountId) {
        this.accountId = accountId;
    }
}