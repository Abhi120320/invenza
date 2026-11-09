package com.invenza.exception;

import org.springframework.http.HttpStatus;

public class InsufficientStockException extends ApiException {
    public InsufficientStockException(String message) {
        super(message, HttpStatus.BAD_REQUEST);
    }
}

