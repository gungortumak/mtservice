package com.tenx.moneytransferservice.exception;

import java.util.NoSuchElementException;

public class AccountNotFoundException extends NoSuchElementException {
    public AccountNotFoundException(String message){
        super(message);
    }
}
