package com.tenx.moneytransferservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tenx.moneytransferservice.exception.AccountNotFoundException;
import com.tenx.moneytransferservice.model.Account;
import com.tenx.moneytransferservice.model.Currency;
import com.tenx.moneytransferservice.model.TransferTransaction;
import com.tenx.moneytransferservice.service.AccountService;
import com.tenx.moneytransferservice.service.TransferTransactionService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
class TransferTransactionControllerTest {
    @MockBean
    private AccountService accountService;
    @MockBean
    private TransferTransactionService transactionService;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private final static Long SOURCE_ACCOUNT_ID = 1L;
    private final static Long TARGET_ACCOUNT_ID = 2L;
    private final static Long SUCCESSFUL_TRANSACTION_ID = 11L;


    @Test
    @DisplayName("Transfer Money Success")
    void testSuccessfulTransferMoney() throws Exception {
        Account sourceAccount = Account.builder().id(SOURCE_ACCOUNT_ID).
                balance(new BigDecimal(2000)).
                currency(Currency.GBP).
                createdAt(LocalDateTime.now()).build();
        Account targetAccount = Account.builder().id(TARGET_ACCOUNT_ID).
                balance(new BigDecimal(1000)).
                currency(Currency.GBP).
                createdAt(LocalDateTime.now()).build();
        TransferTransaction transferTransaction = TransferTransaction.builder()
                .sourceAccount(sourceAccount)
                .targetAccount(targetAccount)
                .id(SUCCESSFUL_TRANSACTION_ID).build();

        TransferTransactionDTO transferTransactionDTO = TransferTransactionDTO.builder()
                .targetAccountId(TARGET_ACCOUNT_ID)
                .sourceAccountId(SOURCE_ACCOUNT_ID)
                .amount(new BigDecimal(500)).build();

        doReturn(sourceAccount).when(accountService).findAccount(SOURCE_ACCOUNT_ID);
        doReturn(targetAccount).when(accountService).findAccount(TARGET_ACCOUNT_ID);

        doReturn(transferTransaction).when(transactionService).transferMoney(sourceAccount,targetAccount,transferTransactionDTO.getAmount());



        mockMvc.perform(post("/v1/transfers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(transferTransactionDTO)))
                .andExpect(status().isCreated())
                .andExpect(header().string(HttpHeaders.LOCATION, "/v1/transfers/"+transferTransaction.getId()));
    }

    @Test
    @DisplayName("Source account does not exist")
    void testTransferFromNonExistedSourceAcccount() throws Exception {

        Account targetAccount = Account.builder().id(TARGET_ACCOUNT_ID).
                balance(new BigDecimal(1000)).
                currency(Currency.USD).
                createdAt(LocalDateTime.now()).build();

        doThrow(new AccountNotFoundException("Account does not exist : " + SOURCE_ACCOUNT_ID)).when(accountService).findAccount(SOURCE_ACCOUNT_ID);
        doReturn(targetAccount).when(accountService).findAccount(TARGET_ACCOUNT_ID);

        TransferTransactionDTO transferTransactionDTO = TransferTransactionDTO.builder()
                .targetAccountId(TARGET_ACCOUNT_ID)
                .sourceAccountId(SOURCE_ACCOUNT_ID)
                .amount(new BigDecimal(500)).build();

        mockMvc.perform(post("/v1/transfers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(transferTransactionDTO)))
                .andExpect(status().is4xxClientError())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").isNotEmpty());

    }

    @Test
    @DisplayName("Target account does not exist")
    void testTransferToNonExistedTargetAcccount() throws Exception {

        Account sourceAccount = Account.builder().id(SOURCE_ACCOUNT_ID).
                balance(new BigDecimal(1500)).
                currency(Currency.GBP).
                createdAt(LocalDateTime.now()).build();

        doReturn(sourceAccount).when(accountService).findAccount(SOURCE_ACCOUNT_ID);
        doThrow(new AccountNotFoundException("Account does not exist : " + TARGET_ACCOUNT_ID)).when(accountService).findAccount(TARGET_ACCOUNT_ID);

        TransferTransactionDTO transferTransactionDTO = TransferTransactionDTO.builder()
                .targetAccountId(TARGET_ACCOUNT_ID)
                .sourceAccountId(SOURCE_ACCOUNT_ID)
                .amount(new BigDecimal(500)).build();

        mockMvc.perform(post("/v1/transfers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(transferTransactionDTO)))
                .andExpect(status().is4xxClientError())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").isNotEmpty());

    }

    @Test
    @DisplayName("Source and Target Currencies are different")
    void testTransferBeetweenDifferentCurrencyAccounts() throws Exception {
        Account sourceAccount = Account.builder().id(SOURCE_ACCOUNT_ID).
                balance(new BigDecimal(1500)).
                currency(Currency.GBP).
                createdAt(LocalDateTime.now()).build();
        Account targetAccount = Account.builder().id(TARGET_ACCOUNT_ID).
                balance(new BigDecimal(1000)).
                currency(Currency.USD).
                createdAt(LocalDateTime.now()).build();

        doReturn(sourceAccount).when(accountService).findAccount(SOURCE_ACCOUNT_ID);
        doReturn(targetAccount).when(accountService).findAccount(TARGET_ACCOUNT_ID);

        TransferTransactionDTO transferTransactionDTO = TransferTransactionDTO.builder()
                .targetAccountId(TARGET_ACCOUNT_ID)
                .sourceAccountId(SOURCE_ACCOUNT_ID)
                .amount(new BigDecimal(500)).build();

        mockMvc.perform(post("/v1/transfers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(transferTransactionDTO)))
                .andExpect(status().is4xxClientError())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").isNotEmpty());
    }

    @Test
    @DisplayName("Source account has insufficent balance for transfer")
    void testSourceAccountInsufficentBalance() throws Exception {
        Account sourceAccount = Account.builder().id(SOURCE_ACCOUNT_ID).
                balance(new BigDecimal(300)).
                currency(Currency.GBP).
                createdAt(LocalDateTime.now()).build();
        Account targetAccount = Account.builder().id(TARGET_ACCOUNT_ID).
                balance(new BigDecimal(1000)).
                currency(Currency.GBP).
                createdAt(LocalDateTime.now()).build();
        doReturn(sourceAccount).when(accountService).findAccount(SOURCE_ACCOUNT_ID);
        doReturn(targetAccount).when(accountService).findAccount(TARGET_ACCOUNT_ID);

        TransferTransactionDTO transferTransactionDTO = TransferTransactionDTO.builder()
                .targetAccountId(TARGET_ACCOUNT_ID)
                .sourceAccountId(SOURCE_ACCOUNT_ID)
                .amount(new BigDecimal(500)).build();

        mockMvc.perform(post("/v1/transfers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(transferTransactionDTO)))
                .andExpect(status().is4xxClientError())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").isNotEmpty());
    }

    @Test
    @DisplayName("Transfer money for a modified account")
    void testConcurrentTransfer() throws Exception {
        BigDecimal transferAmount = new BigDecimal("500");
        Account sourceAccount = Account.builder().id(SOURCE_ACCOUNT_ID).
                balance(new BigDecimal(2000)).
                currency(Currency.GBP).
                createdAt(LocalDateTime.now()).build();
        Account targetAccount = Account.builder().id(TARGET_ACCOUNT_ID).
                balance(new BigDecimal(1000)).
                currency(Currency.GBP).
                createdAt(LocalDateTime.now()).build();
        doReturn(sourceAccount).when(accountService).findAccount(SOURCE_ACCOUNT_ID);
        doReturn(targetAccount).when(accountService).findAccount(TARGET_ACCOUNT_ID);
        TransferTransactionDTO transferTransactionDTO = TransferTransactionDTO.builder()
                .targetAccountId(TARGET_ACCOUNT_ID)
                .sourceAccountId(SOURCE_ACCOUNT_ID)
                .amount(transferAmount).build();

        doThrow(ObjectOptimisticLockingFailureException.class).when(transactionService).transferMoney(sourceAccount,targetAccount,transferAmount);

        mockMvc.perform(post("/v1/transfers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(transferTransactionDTO)))
                .andExpect(status().is5xxServerError())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").isNotEmpty());
    }

}