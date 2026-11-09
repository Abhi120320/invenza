package com.invenza.exception;

import org.springframework.http.HttpStatus;

public class UnauthorizedException extends ApiException {
    private final Integer failedAttempts;

    public UnauthorizedException(String message) {
        super(message, HttpStatus.UNAUTHORIZED);
        this.failedAttempts = null;
    }

    public UnauthorizedException(String message, Integer failedAttempts) {
        super(message, HttpStatus.UNAUTHORIZED);
        this.failedAttempts = failedAttempts;
    }

    public Integer getFailedAttempts() {
        return failedAttempts;
    }
}

