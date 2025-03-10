package ru.malik.bank.StartBank.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import ru.malik.bank.StartBank.entity.Deposit;
import ru.malik.bank.StartBank.entity.enumEntity.Status;
import ru.malik.bank.StartBank.repository.DepositRepository;
import ru.malik.bank.StartBank.service.DepositService;

import java.util.List;

@Component
public class DepositInterestScheduler {

    private final DepositRepository depositRepository;
    private final DepositService depositService;

    @Autowired
    public DepositInterestScheduler(DepositRepository depositRepository, DepositService depositService) {
        this.depositRepository = depositRepository;
        this.depositService = depositService;
    }

    // Cron-выражение запускает задачу в 00:00 первого числа каждого месяца
    @Scheduled(cron = "0 0 0 1 * ?")
    public void processMonthlyDepositInterest() {
        List<Deposit> activeDeposits = depositRepository.findAllByStatus(Status.ACTIVE);
        for (Deposit deposit : activeDeposits) {
            try {
                depositService.accrueDepositInterest(deposit.getId());
                System.out.println("Начислены проценты для депозита ID " + deposit.getId());
            } catch (RuntimeException e) {
                System.err.println("Ошибка при начислении процентов для депозита ID " + deposit.getId() + ": " + e.getMessage());
            }
        }
    }
}