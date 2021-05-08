package com.tenx.moneytransferservice.service;

import com.tenx.moneytransferservice.exception.AccountNotFoundException;
import com.tenx.moneytransferservice.model.Account;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public interface AccountService {

    Account findAccount(Long accountId) throws AccountNotFoundException;
    Account debitAccount(Account sourceAccount, BigDecimal amount);
    Account creditAccount(Account targetAccount,BigDecimal amount);

}
