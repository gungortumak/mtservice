package com.tenx.moneytransferservice.exception;

import java.util.NoSuchElementException;

public class AccountModifiedException  extends RuntimeException {
    public AccountModifiedException(String message){
        super(message);
    }
}
