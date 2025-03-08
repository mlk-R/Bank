package ru.malik.bank.StartBank.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.malik.bank.StartBank.entity.Loan;
import ru.malik.bank.StartBank.entity.User;
import ru.malik.bank.StartBank.entity.enumEntity.Status;

import java.util.List;

@Repository
public interface LoanRepository extends JpaRepository<Loan, Long> {

    List<User> findByUser(User user);

    List<Loan> findAllByStatus(Status status);
}
