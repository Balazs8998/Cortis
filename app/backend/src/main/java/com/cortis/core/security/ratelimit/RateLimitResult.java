package com.cortis.core.security.ratelimit;

import java.time.Instant;

public class RateLimitResult {

    private final boolean allowed;
    private final int limit;
    private final int remainingRequests;
    private final Instant resetAt;

    public RateLimitResult(boolean allowed, int limit, int remainingRequests, Instant resetAt) {
        this.allowed = allowed;
        this.limit = limit;
        this.remainingRequests = remainingRequests;
        this.resetAt = resetAt;
    }

    public boolean isAllowed() {
        return allowed;
    }

    public int getLimit() {
        return limit;
    }

    public int getRemainingRequests() {
        return remainingRequests;
    }

    public Instant getResetAt() {
        return resetAt;
    }
}
