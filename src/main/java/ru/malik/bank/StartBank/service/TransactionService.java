package ru.malik.bank.StartBank.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.malik.bank.StartBank.entity.Transaction;
import ru.malik.bank.StartBank.exception.ResourceNotFoundException;
import ru.malik.bank.StartBank.repository.TransactionRepository;

import java.util.List;

@Service
@Transactional
public class TransactionService {

    private final TransactionRepository transactionRepository;

    @Autowired
    public TransactionService(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    // Создание новой транзакции
    public Transaction createTransaction(Transaction transaction) {
        return transactionRepository.save(transaction);
    }

    // Получение всех транзакций всех пользователей
    public List<Transaction> getAllTransactions() {
        return transactionRepository.findAll();
    }

    // Получение транзакции по id
    public List<Transaction> getTransactionsByAccountId(Long accountId) {
        return transactionRepository.findByAccountId(accountId);
    }

    // Обновление транзакции
    public Transaction updateTransaction(Long id, Transaction transactionDetails) {
        Transaction transaction = transactionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Transaction not found with id: " + id));

        transaction.setType(transactionDetails.getType());
        transaction.setAmount(transactionDetails.getAmount());
        transaction.setStatus(transactionDetails.getStatus());
        // При необходимости можно обновлять и другие поля

        return transactionRepository.save(transaction);
    }

    // Удаление транзакции
    public void deleteTransaction(Long id) {
        Transaction transaction = transactionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Transaction not found with id: " + id));
        transactionRepository.delete(transaction);
    }
}