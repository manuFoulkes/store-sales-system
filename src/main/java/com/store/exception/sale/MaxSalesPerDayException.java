package com.store.exception.sale;

public class MaxSalesPerDayException extends RuntimeException {

    public MaxSalesPerDayException(String message) {
        super(message);
    }
}
