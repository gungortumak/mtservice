package com.tenx.moneytransferservice.repository;

import com.tenx.moneytransferservice.model.Account;
import com.tenx.moneytransferservice.model.Currency;
import com.tenx.moneytransferservice.model.TransferTransaction;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@ExtendWith(SpringExtension.class)
@SpringBootTest
class TransferTransactionRepositoryTest {
    @Autowired
    private TransferTransactionRepository transferTransactionRepository;
    @Autowired
    private AccountRepository accountRepository;

    private static Account savedSourceAccount;
    private static Account savedTargetAccount;

    @BeforeEach
    public void initEach() {
        Account sourceAccount = Account.builder()
                .balance(new BigDecimal("1000"))
                .currency(Currency.GBP)
                .createdAt(LocalDateTime.now()).build();
        Account targetAccount = Account.builder()
                .balance(new BigDecimal("2000"))
                .currency(Currency.GBP)
                .createdAt(LocalDateTime.now()).build();
        TransferTransactionRepositoryTest.savedSourceAccount = accountRepository.save(sourceAccount);
        TransferTransactionRepositoryTest.savedTargetAccount = accountRepository.save(targetAccount);
    }

    @AfterEach
    public void afterEach() {
        transferTransactionRepository.deleteAll();
        accountRepository.deleteAll();
    }

    @Test
    @DisplayName("Persist Transfer Transaction Successfully")
    void testSaveTransferTransactionWithAccounts() {
        BigDecimal transferAmount = new BigDecimal("500");

        TransferTransaction transferTransaction = TransferTransaction.builder()
                .sourceAccount(TransferTransactionRepositoryTest.savedSourceAccount)
                .targetAccount(TransferTransactionRepositoryTest.savedTargetAccount)
                .amount(transferAmount).build();


        TransferTransaction savedTransferTransaction = transferTransactionRepository.save(transferTransaction);

        Assertions.assertNotNull(savedTransferTransaction);
        Assertions.assertNotNull(TransferTransactionRepositoryTest.savedSourceAccount);
        Assertions.assertNotNull(TransferTransactionRepositoryTest.savedTargetAccount);
        Assertions.assertNotNull(savedTransferTransaction.getId());
        Assertions.assertNotNull(savedTransferTransaction.getSourceAccount());
        Assertions.assertNotNull(savedTransferTransaction.getTargetAccount());
        Assertions.assertEquals(TransferTransactionRepositoryTest.savedSourceAccount.getId(), savedTransferTransaction.getSourceAccount().getId());
        Assertions.assertEquals(TransferTransactionRepositoryTest.savedTargetAccount.getId(), savedTransferTransaction.getTargetAccount().getId());

    }
}