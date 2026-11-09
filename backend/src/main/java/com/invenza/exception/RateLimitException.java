package com.invenza.exception;

import org.springframework.http.HttpStatus;

public class RateLimitException extends ApiException {
    private final int retryAfterSeconds;

    public RateLimitException(int retryAfterSeconds) {
        super(String.format("Too many failed attempts. Try again in %d seconds.", retryAfterSeconds), HttpStatus.TOO_MANY_REQUESTS);
        this.retryAfterSeconds = retryAfterSeconds;
    }

    public int getRetryAfterSeconds() {
        return retryAfterSeconds;
    }
}

