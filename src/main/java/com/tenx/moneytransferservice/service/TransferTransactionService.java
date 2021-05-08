package com.tenx.moneytransferservice.service;

import com.tenx.moneytransferservice.model.Account;
import com.tenx.moneytransferservice.model.TransferTransaction;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public interface TransferTransactionService {
    TransferTransaction transferMoney(Account sourceAccount, Account targetAccount, BigDecimal amount);
}
