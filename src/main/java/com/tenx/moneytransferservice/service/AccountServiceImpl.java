package com.tenx.moneytransferservice.service;

import com.tenx.moneytransferservice.exception.AccountNotFoundException;
import com.tenx.moneytransferservice.model.Account;
import com.tenx.moneytransferservice.repository.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class AccountServiceImpl implements  AccountService{

    @Autowired
    AccountRepository accountRepository;


    @Override
    public Account findAccount(Long accountId)  {
        return accountRepository.findById(accountId).orElseThrow(() -> new AccountNotFoundException("Account does not exist : " + accountId));
    }

    @Override
    public Account debitAccount(Account sourceAccount, BigDecimal amount) {
        sourceAccount.setBalance(sourceAccount.getBalance().subtract(amount));
        return accountRepository.save(sourceAccount);
    }

    @Override
    public Account creditAccount(Account targetAccount, BigDecimal amount) {
        targetAccount.setBalance(targetAccount.getBalance().add(amount));
        return accountRepository.save(targetAccount);
    }
}
