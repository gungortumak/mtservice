package com.tenx.moneytransferservice.service;

import com.tenx.moneytransferservice.controller.TransferTransactionDTO;
import com.tenx.moneytransferservice.model.Account;
import com.tenx.moneytransferservice.model.Currency;
import com.tenx.moneytransferservice.model.TransferTransaction;
import com.tenx.moneytransferservice.repository.AccountRepository;
import com.tenx.moneytransferservice.repository.TransferTransactionRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest
class TransferTransactionServiceTest {
    @Autowired
    private TransferTransactionService transferTransactionService;

    @MockBean
    private TransferTransactionRepository transferTransactionRepository;

    @MockBean
    private AccountRepository accountRepository;

    private final static Long SOURCE_ACCOUNT_ID = 1L;
    private final static Long TARGET_ACCOUNT_ID = 2L;
    private final static Long SUCCESSFUL_TRANSACTION_ID = 11L;

    @Test
    @DisplayName("Transfer Money Succesfully")
    void testTransferMoney() {
        BigDecimal originalSourceAccountAmount = new BigDecimal("2000");
        BigDecimal originalTargetAccountAmount = new BigDecimal("1000");

        BigDecimal transferAmount = new BigDecimal("500");

        Account sourceAccount = Account.builder().id(SOURCE_ACCOUNT_ID).
                balance(originalSourceAccountAmount).
                currency(Currency.GBP).
                createdAt(LocalDateTime.now()).build();
        Account targetAccount = Account.builder().id(TARGET_ACCOUNT_ID).
                balance(originalTargetAccountAmount).
                currency(Currency.GBP).
                createdAt(LocalDateTime.now()).build();
        TransferTransaction transferTransaction = TransferTransaction.builder()
                .sourceAccount(sourceAccount)
                .targetAccount(targetAccount)
                .amount(transferAmount).build();


        TransferTransaction savedTransferTransaction = TransferTransaction.builder()
                .sourceAccount(sourceAccount)
                .targetAccount(targetAccount)
                .amount(transferAmount)
                .id(SUCCESSFUL_TRANSACTION_ID).build();


        doReturn(savedTransferTransaction).when(transferTransactionRepository).save(transferTransaction);


        TransferTransaction actualTransferTransaction = transferTransactionService.transferMoney(sourceAccount,targetAccount,transferAmount);
        Assertions.assertNotNull(actualTransferTransaction);
        Assertions.assertEquals(originalSourceAccountAmount.subtract(transferAmount),actualTransferTransaction.getSourceAccount().getBalance());
        Assertions.assertEquals(originalTargetAccountAmount.add(transferAmount),actualTransferTransaction.getTargetAccount().getBalance());
        Assertions.assertEquals(transferAmount,actualTransferTransaction.getAmount());

    }
}