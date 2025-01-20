package com.store.exception.sale;

public class InvalidSaleStateException extends RuntimeException {

    public InvalidSaleStateException(String message) {
        super(message);
    }
}