package com.cortis.core.security.ratelimit;

import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

/** Atomic single-instance fixed-window limiter. Replace with shared store before scaling horizontally. */
@Component
public class InMemoryRateLimitStore {
    private record Window(int count, Instant resetAt) {}
    private final ConcurrentHashMap<String, Window> windows = new ConcurrentHashMap<>();
    private final AtomicInteger operations = new AtomicInteger();

    public RateLimitResult check(String key, RateLimitRule rule) {
        if (rule == null || rule.getMaxRequests() < 1 || rule.getWindowSeconds() < 1) {
            throw new IllegalArgumentException("Rate limit must specify positive maxRequests and windowSeconds");
        }
        Instant now = Instant.now();
        AtomicReference<RateLimitResult> result = new AtomicReference<>();
        windows.compute(key, (ignored, old) -> {
            if (old == null || !now.isBefore(old.resetAt())) {
                Instant reset = now.plusSeconds(rule.getWindowSeconds());
                result.set(new RateLimitResult(true, rule.getMaxRequests(), rule.getMaxRequests() - 1, reset));
                return new Window(1, reset);
            }
            if (old.count() >= rule.getMaxRequests()) {
                result.set(new RateLimitResult(false, rule.getMaxRequests(), 0, old.resetAt()));
                return old;
            }
            int next = old.count() + 1;
            result.set(new RateLimitResult(true, rule.getMaxRequests(), rule.getMaxRequests() - next, old.resetAt()));
            return new Window(next, old.resetAt());
        });
        if (operations.incrementAndGet() % 2048 == 0) {
            windows.forEach((k, window) -> {
                if (!now.isBefore(window.resetAt())) windows.remove(k, window);
            });
        }
        return result.get();
    }
}
