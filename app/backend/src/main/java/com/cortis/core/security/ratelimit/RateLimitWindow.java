package com.cortis.core.security.ratelimit;

import java.time.Instant;

public class RateLimitWindow {

    private int requestCount;
    private Instant windowStart;

    public RateLimitWindow(int requestCount, Instant windowStart) {
        this.requestCount = requestCount;
        this.windowStart = windowStart;
    }

    public int getRequestCount() {
        return requestCount;
    }

    public void setRequestCount(int requestCount) {
        this.requestCount = requestCount;
    }

    public Instant getWindowStart() {
        return windowStart;
    }

    public void setWindowStart(Instant windowStart) {
        this.windowStart = windowStart;
    }
}
