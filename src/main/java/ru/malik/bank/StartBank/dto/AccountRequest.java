package ru.malik.bank.StartBank.dto;

import java.math.BigDecimal;

public class AccountRequest {
    private BigDecimal initalBalance;
    private String accountType;

    public BigDecimal getInitalBalance() {
        return initalBalance;
    }

    public void setInitalBalance(BigDecimal initalBalance) {
        this.initalBalance = initalBalance;
    }

    public String getAccountType() {
        return accountType;
    }

    public void setAccountType(String accountType) {
        this.accountType = accountType;
    }
}
