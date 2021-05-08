package com.tenx.moneytransferservice.service;

import com.tenx.moneytransferservice.exception.AccountNotFoundException;
import com.tenx.moneytransferservice.model.Account;
import com.tenx.moneytransferservice.model.Currency;
import com.tenx.moneytransferservice.repository.AccountRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;

@ExtendWith(SpringExtension.class)
@SpringBootTest
class AccountServiceTest {
    @Autowired
    private AccountService accountService;
    @MockBean
    private AccountRepository accountRepository;
    private final static Long ACCOUNT_ID = 1L;

    @Test
    @DisplayName("Find existed account successfully")
    void testFindByExistedAccount(){
        Account expectedAccount = Account.builder().id(ACCOUNT_ID).
                    balance(new BigDecimal("1000")).
                currency(Currency.GBP).
                createdAt(LocalDateTime.now()).build();
        doReturn(Optional.of(expectedAccount)).when(accountRepository).findById(ACCOUNT_ID);

        Account actualAccount = accountService.findAccount(ACCOUNT_ID);

        Assertions.assertEquals(expectedAccount,actualAccount);
    }
    @Test
    @DisplayName("Find notexisted account id")
    void testFindByNonExistedAccount(){
        doReturn(Optional.empty()).when(accountRepository).findById(ACCOUNT_ID);

        assertThrows(
                AccountNotFoundException.class,
                () -> accountService.findAccount(ACCOUNT_ID)
        );
    }

    @Test
    @DisplayName("Save modified source account")
    void testConcurrentAccountUpdateOnSource(){
        Account account = Account.builder().id(ACCOUNT_ID).
                balance(new BigDecimal("1000")).
                currency(Currency.GBP).
                createdAt(LocalDateTime.now()).build();
        doThrow(ObjectOptimisticLockingFailureException.class).when(accountRepository).save(account);
        BigDecimal amount = new BigDecimal("500");
        assertThrows(
                ObjectOptimisticLockingFailureException.class,
                () -> accountService.debitAccount(account,amount)
        );
    }
    @Test
    @DisplayName("Save modified target account")
    void testConcurrentAccountUpdateOnTarget(){
        Account account = Account.builder().id(ACCOUNT_ID).
                balance(new BigDecimal("1000")).
                currency(Currency.GBP).
                createdAt(LocalDateTime.now()).build();
        doThrow(ObjectOptimisticLockingFailureException.class).when(accountRepository).save(account);
        BigDecimal amount = new BigDecimal("500");
        assertThrows(
                ObjectOptimisticLockingFailureException.class,
                () -> accountService.creditAccount(account,amount)
        );

    }

    @Test
    @DisplayName("account debit")
    void testDebitAccount(){
        BigDecimal originalAmount = new BigDecimal("1000");
        BigDecimal debitAmount = new BigDecimal("500");
        Account account = Account.builder().id(ACCOUNT_ID).
                balance(originalAmount).
                currency(Currency.GBP).
                createdAt(LocalDateTime.now()).build();


        Account savedAccount = Account.builder().id(ACCOUNT_ID).
                balance(originalAmount.subtract(debitAmount)).
                currency(Currency.GBP).
                createdAt(LocalDateTime.now()).build();

        doReturn(savedAccount).when(accountRepository).save(any());

        accountService.debitAccount(account,debitAmount);

        Assertions.assertEquals(savedAccount.getBalance(),account.getBalance());
    }

    @Test
    @DisplayName("account credit")
    void testCreditAccount(){
        BigDecimal originalAmount = new BigDecimal("1000");
        BigDecimal creditAmount = new BigDecimal("500");
        Account account = Account.builder().id(ACCOUNT_ID).
                balance(originalAmount).
                currency(Currency.GBP).
                createdAt(LocalDateTime.now()).build();

        Account savedAccount = Account.builder().id(ACCOUNT_ID).
                balance(originalAmount.add(creditAmount)).
                currency(Currency.GBP).
                createdAt(LocalDateTime.now()).build();

        doReturn(savedAccount).when(accountRepository).save(any());

        accountService.creditAccount(account,creditAmount);
        Assertions.assertEquals(savedAccount.getBalance(),account.getBalance());
    }


}