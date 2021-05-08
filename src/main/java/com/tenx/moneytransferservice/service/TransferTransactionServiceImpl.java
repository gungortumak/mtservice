package com.tenx.moneytransferservice.service;

import com.tenx.moneytransferservice.model.Account;
import com.tenx.moneytransferservice.model.TransferTransaction;
import com.tenx.moneytransferservice.repository.TransferTransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.math.BigDecimal;

@Service
public class TransferTransactionServiceImpl implements TransferTransactionService {

    @Autowired
    TransferTransactionRepository transferTransactionRepository;
    @Autowired
    AccountService accountService;

    @Transactional
    @Override
    public TransferTransaction transferMoney(Account sourceAccount, Account targetAccount, BigDecimal amount) {
        accountService.debitAccount(sourceAccount,amount);
        accountService.creditAccount(targetAccount,amount);
        return transferTransactionRepository.save(TransferTransaction.builder().sourceAccount(sourceAccount)
                                                                               .targetAccount(targetAccount)
                                                                               .amount(amount)
                                                                               .build());

    }
}
