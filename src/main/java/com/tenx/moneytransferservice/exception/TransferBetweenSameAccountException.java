package com.tenx.moneytransferservice.exception;

public class TransferBetweenSameAccountException extends RuntimeException{
    public TransferBetweenSameAccountException(String message){
        super(message);
    }
}
