package com.invenza.exception;

import org.springframework.http.HttpStatus;

/**
 * Base abstract exception for application domain errors.
 * Demonstrates OOP Exception Hierarchy.
 */
public abstract class ApiException extends RuntimeException {

    private final HttpStatus status;

    public ApiException(String message, HttpStatus status) {
        super(message);
        this.status = status;
    }

    public ApiException(String message, HttpStatus status, Throwable cause) {
        super(message, cause);
        this.status = status;
    }

    public HttpStatus getStatus() {
        return status;
    }
}

