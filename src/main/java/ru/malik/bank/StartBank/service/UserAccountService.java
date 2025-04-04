package ru.malik.bank.StartBank.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.malik.bank.StartBank.entity.UserAccountView;
import ru.malik.bank.StartBank.repository.UserAccountRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserAccountService {

    private final UserAccountRepository userAccountRepository;

    @Autowired
    public UserAccountService(UserAccountRepository userAccountRepository) {
        this.userAccountRepository = userAccountRepository;
    }

    @Transactional(readOnly = true)
    public Page<UserAccountView> getAllUserAccounts(Pageable pageable) {
        return userAccountRepository.getUserAccounts(pageable);
    }

    @Transactional(readOnly = true)
    public Page<UserAccountView> getUserAccountsByBalance(Pageable pageable) {
        Page<UserAccountView> userAccountsPage = userAccountRepository.getUserAccounts(pageable);
        List<UserAccountView> filteredAccounts = userAccountsPage.getContent().stream()
                .filter(account -> account.getBalance() > 1000)
                .collect(Collectors.toList());

        return new PageImpl<>(filteredAccounts, pageable, userAccountsPage.getTotalElements());
    }
}

