package ru.malik.bank.StartBank.entity;

import jakarta.persistence.*;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Immutable;
import ru.malik.bank.StartBank.util.UserAccountId;

import java.time.LocalDateTime;

@Entity
@Immutable
@Getter
@Setter
public class UserAccountView {

    @EmbeddedId
    private UserAccountId id;

    @Column(name = "username")
    private String username;

    @Column(name = "email")
    private String email;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "balance")
    private Double balance;

    @Column(name = "account_type")
    private String accountType;

    @Column(name = "card_number")
    private String cardNumber;

}
