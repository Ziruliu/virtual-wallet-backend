package com.ziruliu.backend.exception;

public class NegativeAccountBalanceException extends RuntimeException {

    public NegativeAccountBalanceException(String message) {
        super(message);
    }
}
