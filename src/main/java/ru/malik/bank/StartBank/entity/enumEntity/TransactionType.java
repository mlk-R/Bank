package ru.malik.bank.StartBank.entity.enumEntity;

public enum TransactionType {
    DEPOSIT,                // Внесение депозита
    WITHDRAWAL,             // Снятие средств
    TRANSFER,               // Перевод между счетами
    PAYMENT,                // Оплата товаров или услуг
    LOAN,                   // Оформление кредита
    CREDIT_PAYMENT,         // Погашение кредита
    REFUND,                 // Возврат средств
    EXCHANGE,               // Обмен валюты
    INTERBANK_TRANSFER;     // Межбанковский перевод
}
