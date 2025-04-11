package ru.malik.bank.StartBank.service;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.malik.bank.StartBank.dto.UserAccountDto;
import ru.malik.bank.StartBank.entity.UserAccountView;
import ru.malik.bank.StartBank.repository.UserAccountRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserAccountService {

    private final UserAccountRepository userAccountRepository;
    private final ModelMapper modelMapper;

    @Autowired
    public UserAccountService(UserAccountRepository userAccountRepository,
                              ModelMapper modelMapper) {
        this.userAccountRepository = userAccountRepository;
        this.modelMapper = modelMapper;
    }

    @Transactional(readOnly = true)
    public Page<UserAccountDto> getAllUserAccounts(Pageable pageable) {
        return userAccountRepository.getUserAccounts(pageable)
                .map(entity -> modelMapper.map(entity, UserAccountDto.class));
    }

    @Transactional(readOnly = true)
    public Page<UserAccountDto> getUserAccountsByBalance(Pageable pageable) {
        Page<UserAccountView> userAccountsPage = userAccountRepository.getUserAccounts(pageable);

        List<UserAccountDto> filteredAccounts = userAccountsPage.getContent().stream()
                .filter(account -> account.getBalance() > 1000)
                .map(entity -> modelMapper.map(entity, UserAccountDto.class))
                .collect(Collectors.toList());

        return new PageImpl<>(filteredAccounts, pageable, userAccountsPage.getTotalElements());
    }
}
