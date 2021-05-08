package com.tenx.moneytransferservice.repository;

import com.tenx.moneytransferservice.exception.AccountNotFoundException;
import com.tenx.moneytransferservice.model.Account;
import com.tenx.moneytransferservice.model.Currency;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith({SpringExtension.class})
@SpringBootTest
class AccountRepositoryTest {

    @Autowired
    private AccountRepository accountRepository;


    private static Account savedAccount;

    @BeforeEach
    public void initEach(){
        Account account = Account.builder()
                .balance(new BigDecimal("1000"))
                .currency(Currency.GBP)
                .createdAt(LocalDateTime.now()).build();
        AccountRepositoryTest.savedAccount = accountRepository.save(account);
    }

    @AfterEach
    public void afterEach(){
         accountRepository.deleteAll();
    }
    @Test
    @DisplayName("Find a saved account")
    void testFindSavedAccount(){
        Account foundAccount = accountRepository.findById(savedAccount.getId()).orElseThrow(() -> new AccountNotFoundException("Account does not exist : " + savedAccount.getId()));

        Assertions.assertEquals(savedAccount.getId(),foundAccount.getId());
        Assertions.assertEquals(0, AccountRepositoryTest.savedAccount.getBalance().compareTo(foundAccount.getBalance()));
        Assertions.assertEquals(AccountRepositoryTest.savedAccount.getCurrency(),foundAccount.getCurrency());
    }


    @Test
    @DisplayName("Concurrent changes are not allowed in same account")
    void testConcurrentChangesOnAccount(){

        Account accountInProgress = accountRepository.findById(AccountRepositoryTest.savedAccount.getId()).orElseThrow(() -> new AccountNotFoundException("Account does not exist : " + savedAccount.getId()));
        accountInProgress.setBalance(new BigDecimal("10"));
        Account account = accountRepository.findById(AccountRepositoryTest.savedAccount.getId()).orElseThrow(() -> new AccountNotFoundException("Account does not exist : " + savedAccount.getId()));
        account.setBalance(new BigDecimal("20"));

        Account savedAccount = accountRepository.save(accountInProgress);

        assertThrows(
                ObjectOptimisticLockingFailureException.class,
                () -> accountRepository.save(account)
        );

        Assertions.assertEquals(accountInProgress.getId(),savedAccount.getId());
        Assertions.assertEquals(new BigDecimal("10"),savedAccount.getBalance());

    }


}