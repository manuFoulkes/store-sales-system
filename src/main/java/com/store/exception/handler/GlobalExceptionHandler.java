package com.store.exception.handler;

import com.store.exception.customer.CustomerAlreadyExistsException;
import com.store.exception.customer.CustomerNotFoundException;
import com.store.exception.model.ApiError;
import com.store.exception.product.InsufficientStockException;
import com.store.exception.product.ProductAlreadyExistsException;
import com.store.exception.product.ProductNotFoundException;
import com.store.exception.sale.InvalidSaleStateException;
import com.store.exception.sale.MaxSalesPerDayException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(CustomerAlreadyExistsException.class)
    public ResponseEntity<ApiError> handleCustomerAlreadyExist(CustomerAlreadyExistsException ex) {
        ApiError error = new ApiError(
                HttpStatus.CONFLICT.value(),
                "CUSTOMER_ALREADY_EXIST",
                ex.getMessage(),
                Instant.now()
        );

        return new ResponseEntity<>(error, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(CustomerNotFoundException.class)
    public ResponseEntity<ApiError> handleCustomerNotFound(CustomerNotFoundException ex) {
        ApiError error = new ApiError(
                HttpStatus.NOT_FOUND.value(),
                "CUSTOMER_NOT_FOUND",
                ex.getMessage(),
                Instant.now()
        );

        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(InsufficientStockException.class)
    public ResponseEntity<ApiError> handleInsufficientStock(InsufficientStockException ex) {
        ApiError error = new ApiError(
                HttpStatus.CONFLICT.value(),
                "INSUFFICIENT_STOCK",
                ex.getMessage(),
                Instant.now()
        );

        return new ResponseEntity<>(error, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(ProductAlreadyExistsException.class)
    public ResponseEntity<ApiError> handleProductAlreadyExist(ProductAlreadyExistsException ex) {
        ApiError error = new ApiError(
                HttpStatus.CONFLICT.value(),
                "PRODUCT_ALREADY_EXIST",
                ex.getMessage(),
                Instant.now()
        );

        return new ResponseEntity<>(error, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(ProductNotFoundException.class)
    public ResponseEntity<ApiError> handleProductNotFound(ProductNotFoundException ex) {
        ApiError error = new ApiError(
                HttpStatus.NOT_FOUND.value(),
                "PRODUCT_NOT_FOUND",
                ex.getMessage(),
                Instant.now()
        );

        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(InvalidSaleStateException.class)
    public ResponseEntity<ApiError> handleInvalidSaleState(InvalidSaleStateException ex) {
        ApiError error= new ApiError(
                HttpStatus.CONFLICT.value(),
                "INVALID_SALE_STATE",
                ex.getMessage(),
                Instant.now()
        );

        return new ResponseEntity<>(error, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(MaxSalesPerDayException.class)
    public ResponseEntity<ApiError> handleMaxSalesPerDay(MaxSalesPerDayException ex) {
        ApiError error = new ApiError(
                HttpStatus.UNPROCESSABLE_ENTITY.value(),
                "MAX_SALES_EXCEEDED",
                ex.getMessage(),
                Instant.now()
        );

        return new ResponseEntity<>(error, HttpStatus.UNPROCESSABLE_ENTITY);
    }
}
