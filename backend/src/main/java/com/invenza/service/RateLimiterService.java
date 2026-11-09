package com.invenza.service;

public interface RateLimiterService {
    void checkRateLimit(String identifier, String ip);
    int recordFailure(String identifier, String ip);
    void recordSuccess(String identifier, String ip);
    int getAttempts(String identifier, String ip);
    Integer getRemainingAttemptsBeforeLockout(int attempts);
}

