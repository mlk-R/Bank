package ru.malik.bank.StartBank.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.malik.bank.StartBank.entity.UserAccountView;

import java.util.List;

@Repository
public interface UserAccountRepository extends JpaRepository<UserAccountView, Long> {

    @Query(value = "SELECT u.id AS user_id, u.username, u.email, u.created_at, " +
            "a.id AS account_id, a.balance, a.account_type, a.card_number " +
            "from users u JOIN accounts a ON u.id = a.user_id",
            nativeQuery = true)
    List<UserAccountView> getUserAccounts();

}
