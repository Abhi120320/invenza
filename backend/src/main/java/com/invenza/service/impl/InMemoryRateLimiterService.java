package com.invenza.service.impl;

import com.invenza.exception.RateLimitException;
import com.invenza.service.RateLimiterService;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * In-memory thread-safe rate limiter.
 * Lockout rules:
 * - 3 failures -> 30 sec
 * - 5 failures -> 5 min (300 sec)
 * - 10 failures -> 30 min (1800 sec)
 */
@Service
public class InMemoryRateLimiterService implements RateLimiterService {

    private static class Entry {
        int attempts = 0;
        long lockedUntilEpochSec = 0;
    }

    private final Map<String, Entry> store = new ConcurrentHashMap<>();

    private static final int[][] LOCKOUT_SCHEDULE = {
            {3, 30},
            {5, 300},
            {10, 1800}
    };

    private String buildKey(String identifier, String ip) {
        return (identifier != null ? identifier : "anon") + "::" + (ip != null ? ip : "unknown");
    }

    private int getLockoutDuration(int attempts) {
        int duration = 0;
        for (int[] tier : LOCKOUT_SCHEDULE) {
            if (attempts >= tier[0]) {
                duration = tier[1];
            }
        }
        return duration;
    }

    @Override
    public void checkRateLimit(String identifier, String ip) {
        String key = buildKey(identifier, ip);
        Entry entry = store.get(key);
        if (entry != null) {
            long now = Instant.now().getEpochSecond();
            if (entry.lockedUntilEpochSec > now) {
                int waitSecs = (int) (entry.lockedUntilEpochSec - now);
                throw new RateLimitException(waitSecs);
            }
        }
    }

    @Override
    public int recordFailure(String identifier, String ip) {
        String key = buildKey(identifier, ip);
        Entry entry = store.computeIfAbsent(key, k -> new Entry());
        entry.attempts++;
        int lockSecs = getLockoutDuration(entry.attempts);
        if (lockSecs > 0) {
            entry.lockedUntilEpochSec = Instant.now().getEpochSecond() + lockSecs;
        }
        return entry.attempts;
    }

    @Override
    public void recordSuccess(String identifier, String ip) {
        String key = buildKey(identifier, ip);
        store.remove(key);
    }

    @Override
    public int getAttempts(String identifier, String ip) {
        String key = buildKey(identifier, ip);
        Entry entry = store.get(key);
        return entry != null ? entry.attempts : 0;
    }

    @Override
    public Integer getRemainingAttemptsBeforeLockout(int attempts) {
        for (int[] tier : LOCKOUT_SCHEDULE) {
            if (tier[0] > attempts) {
                return tier[0] - attempts;
            }
        }
        return null;
    }
}

