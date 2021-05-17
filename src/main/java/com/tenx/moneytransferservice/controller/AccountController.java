package com.tenx.moneytransferservice.controller;


import com.tenx.moneytransferservice.exception.CurrencyMismatchException;
import com.tenx.moneytransferservice.exception.InsufficientBalanceException;
import com.tenx.moneytransferservice.exception.TransferBetweenSameAccountException;
import com.tenx.moneytransferservice.model.Account;
import com.tenx.moneytransferservice.model.TransferTransaction;
import com.tenx.moneytransferservice.service.AccountService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.net.URISyntaxException;
import java.time.LocalDateTime;

@RestController
@RequestMapping("/v1/accounts")
public class AccountController {

    @Autowired
    AccountService accountService;

    private static final Logger logger = LoggerFactory.getLogger(AccountController.class);
    /**
     *
     * creates account
     * @param createAccount
     *      {
     *     "currency": "GBP"
     *     }
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity createAccount(@RequestBody AccountDTO accountDTO) throws URISyntaxException {
        Long accountId = accountService.saveAccount(accountDTO);
        return ResponseEntity
                .created(new URI("/v1/accounts/" + accountId)).build();
    }

}
