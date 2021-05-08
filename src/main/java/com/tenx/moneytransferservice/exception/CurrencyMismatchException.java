package com.tenx.moneytransferservice.exception;

public class CurrencyMismatchException extends RuntimeException {
    public CurrencyMismatchException(String message){
        super(message);
    }
}
