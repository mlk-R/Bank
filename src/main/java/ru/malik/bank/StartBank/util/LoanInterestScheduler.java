package ru.malik.bank.StartBank.util;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import ru.malik.bank.StartBank.entity.Loan;
import ru.malik.bank.StartBank.entity.enumEntity.Status;
import ru.malik.bank.StartBank.repository.LoanRepository;
import ru.malik.bank.StartBank.service.LoanService;

import java.util.List;

@Component
public class LoanInterestScheduler {

    private final LoanRepository loanRepository;
    private final LoanService loanService;

    public LoanInterestScheduler(LoanRepository loanRepository, LoanService loanService) {
        this.loanRepository = loanRepository;
        this.loanService = loanService;
    }

    @Scheduled(cron = "0 0 0 1 * ?")
    public void processMonthlyLoanInterest() {
        List<Loan> activeLoans = loanRepository.findAllByStatus(Status.ACTIVE);
        for (Loan loan : activeLoans) {
            try {
                loanService.accrueLoanInterest(loan.getId());
                System.out.println("Начислены проценты для кредита ID " + loan.getId());
            } catch (RuntimeException e) {
                System.err.println("Ошибка при начислении процентов для кредита ID " + loan.getId() + ": " + e.getMessage());
            }
        }
    }
}