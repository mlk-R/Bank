package ru.malik.bank.StartBank.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.malik.bank.StartBank.entity.Loan;
import ru.malik.bank.StartBank.entity.Transaction;
import ru.malik.bank.StartBank.entity.User;
import ru.malik.bank.StartBank.entity.enumEntity.Status;

import java.util.List;

@Repository
public interface LoanRepository extends JpaRepository<Loan, Long> {

    List<Loan> findAllByStatus(Status status);

    @Query("SELECT l FROM Loan l WHERE l.user.id = :userId")
    List<Loan> findByUserId(@Param("userId") Long userId);
}
