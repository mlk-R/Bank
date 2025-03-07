package ru.malik.bank.StartBank.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.malik.bank.StartBank.entity.Account;
import ru.malik.bank.StartBank.entity.User;

import java.util.List;
import java.util.Optional;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {

    @Query("SELECT MAX(a.cardNumber) FROM Account a")
    String findMaxCardNumber();

    Optional<Account> findByCardNumber(String cardNumber);

    Optional<Account> findById(Long id);

    List<Account> findByUser(User user);
}
