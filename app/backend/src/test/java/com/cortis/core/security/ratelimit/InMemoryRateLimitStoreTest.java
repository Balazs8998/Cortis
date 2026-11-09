package com.cortis.core.security.ratelimit;

import org.junit.jupiter.api.Test;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import static org.junit.jupiter.api.Assertions.*;

class InMemoryRateLimitStoreTest {
    private static RateLimitRule rule(int max, int seconds) {
        RateLimitRule rule = new RateLimitRule();
        rule.setMaxRequests(max);
        rule.setWindowSeconds(seconds);
        return rule;
    }

    @Test void counterIsAtomicUnderHeavyConcurrency() throws Exception {
        InMemoryRateLimitStore store = new InMemoryRateLimitStore();
        RateLimitRule limit = rule(20, 60);
        int totalRequests = 120;
        ExecutorService pool = Executors.newFixedThreadPool(16);
        CountDownLatch begin = new CountDownLatch(1);
        AtomicInteger accepted = new AtomicInteger();
        try {
            java.util.List<Future<?>> futures = new java.util.ArrayList<>();
            for (int i = 0; i < totalRequests; i++) {
                futures.add(pool.submit(() -> {
                    begin.await();
                    if (store.check("shared-key", limit).isAllowed()) accepted.incrementAndGet();
                    return null;
                }));
            }
            begin.countDown();
            for (Future<?> task : futures) task.get(5, TimeUnit.SECONDS);
            assertEquals(20, accepted.get());
            assertFalse(store.check("shared-key", limit).isAllowed());
            assertTrue(store.check("different-key", limit).isAllowed());
        } finally {
            pool.shutdownNow();
        }
    }

    @Test void rejectsInvalidConfiguration() {
        assertThrows(IllegalArgumentException.class, () ->
                new InMemoryRateLimitStore().check("key", rule(0, 60)));
    }
}
