package ru.malik.bank.StartBank.entity;


import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.*;
import ru.malik.bank.StartBank.entity.enumEntity.Status;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "deposits")
@Data
public class Deposit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Positive(message = "Сумма депозита должна быть больше 0")
    @Column(name = "amount", nullable = false)
    private BigDecimal amount;

    @Min(value = 1, message = "Срок депозита должен быть больше 0")
    @Column(name = "term", nullable = false)
    private Integer term;

    @PositiveOrZero(message = "Процентная ставка не может быть отрицательной")
    @Column(name = "interest_rate", nullable = false)
    private BigDecimal interestRate;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    // Дополнительное поле для статуса депозита (например, ACTIVE, CLOSED)
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private Status status;

}