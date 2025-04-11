package ru.malik.bank.StartBank.entity;


import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "accounts")
@JsonIgnoreProperties({"transactions"})
@Data
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    @JsonBackReference
    private User user;

    @Column(name = "balance", nullable = false)
    private BigDecimal balance;

    @Column(name = "account_type", nullable = false)
    private String accountType; // "DEBIT" или "CREDIT"

    @Size(min = 16, max = 16, message = "Номер карты должен содержать 16 цифр")
    @Column(name = "card_number", nullable = false, unique = true)
    private String cardNumber;


    @Column(name = "expiration_date", nullable = false)
    private LocalDate expirationDate;

    @Size(min = 3, max = 3, message = "CVV должен содержать 3 цифры")
    @Column(name = "cvv", nullable = false)
    private String cvv;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();


    @OneToMany(mappedBy = "account", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Transaction> transactions;
}
