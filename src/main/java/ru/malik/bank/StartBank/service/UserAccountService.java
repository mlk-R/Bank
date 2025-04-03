package ru.malik.bank.StartBank.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.malik.bank.StartBank.entity.UserAccountView;
import ru.malik.bank.StartBank.repository.UserAccountRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class UserAccountService {

    private final UserAccountRepository userAccountRepository;

    @Autowired
    public UserAccountService(UserAccountRepository userAccountRepository) {
        this.userAccountRepository = userAccountRepository;
    }

    public List<UserAccountView> getAllUserAccounts() {
        return userAccountRepository.getUserAccounts();
    }

    public List<UserAccountView> getUserAccountsByBalance() {
        List<UserAccountView> userAccounts = userAccountRepository.getUserAccounts();
        return userAccounts.stream()
                .filter(account -> account.getBalance() > 1000)
                .collect(Collectors.toList());
    }
}
