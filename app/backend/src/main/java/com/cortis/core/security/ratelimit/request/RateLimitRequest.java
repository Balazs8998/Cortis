package com.cortis.core.security.ratelimit.request;

import com.cortis.core.security.ratelimit.RateLimitRule;

public record RateLimitRequest(
        String key,
        RateLimitRule rule
) {
}
