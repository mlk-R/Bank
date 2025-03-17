package ru.malik.bank.StartBank.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.malik.bank.StartBank.entity.Account;
import ru.malik.bank.StartBank.entity.Transaction;
import ru.malik.bank.StartBank.repository.TransactionRepository;
import org.junit.jupiter.api.Test;


import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.mockito.internal.verification.VerificationModeFactory.times;

@ExtendWith(MockitoExtension.class)
class TransactionServiceTest {

    @Mock
    private TransactionRepository transactionRepository;

    @InjectMocks
    private TransactionService transactionService;

    private Transaction transaction;

    @BeforeEach
    void setUp() {
        transaction = new Transaction();
        transaction.setId(1L);
        transaction.setAmount(BigDecimal.valueOf(100));
        transaction.setStatus("CREATED");
        transaction.setCreatedAt(LocalDateTime.now());
    }

    @Test
    void testCreateTransaction() {
        when(transactionRepository.save(any(Transaction.class))).thenReturn(transaction);

        Transaction savedTransaction = transactionService.createTransaction(transaction, new Account(), BigDecimal.valueOf(100));

        assertNotNull(savedTransaction);
        assertEquals("CREATED", savedTransaction.getStatus());
        verify(transactionRepository, times(1)).save(transaction);
    }

    @Test
    void testGetTransactionsByAccountId() {
        when(transactionRepository.findByAccountId(anyLong())).thenReturn(Arrays.asList(transaction));

        List<Transaction> transactions = transactionService.getTransactionsByAccountId(1L);

        assertFalse(transactions.isEmpty());
        assertEquals(1, transactions.size());
        verify(transactionRepository, times(1)).findByAccountId(1L);
    }

    @Test
    void testDeleteTransaction() {
        when(transactionRepository.findById(1L)).thenReturn(Optional.of(transaction));
        doNothing().when(transactionRepository).delete(transaction);

        transactionService.deleteTransaction(1L);

        verify(transactionRepository, times(1)).findById(1L);
        verify(transactionRepository, times(1)).delete(transaction);
    }
}