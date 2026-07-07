package com.cortis.core.security.ratelimit;

import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
public class RateLimitService {

    private final RateLimitProperties properties;
    private final InMemoryRateLimitStore store;


    RateLimitService(RateLimitProperties properties, InMemoryRateLimitStore store) {
        this.properties = properties;
        this.store = store;
    }

    public RateLimitResult checkLimit(String key, RateLimitRule rule){

        if(!properties.isEnabled()){

            Instant now = Instant.now();

            return new RateLimitResult(
                    true,
                    rule.getMaxRequests(),
                    rule.getMaxRequests(),
                    now
            );
        }
        return store.check(key, rule);

    }

}
