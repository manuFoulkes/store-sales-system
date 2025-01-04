package com.store.exception.sale;

public class SaleNotFoundException extends RuntimeException {

    public SaleNotFoundException(String message) {
        super(message);
    }
}
