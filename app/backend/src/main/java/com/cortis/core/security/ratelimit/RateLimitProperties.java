package com.cortis.core.security.ratelimit;

import org.springframework.boot.context.properties.ConfigurationProperties;


@ConfigurationProperties(prefix = "security.rate-limit")
public class RateLimitProperties {

    private boolean enabled;
    private RateLimitRule defaultLimit;
    private RateLimitRule loginLimit;
    private RateLimitRule strictLimit;

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public RateLimitRule getDefaultLimit() {
        return defaultLimit;
    }

    public void setDefaultLimit(RateLimitRule defaultLimit) {
        this.defaultLimit = defaultLimit;
    }

    public RateLimitRule getLoginLimit() {
        return loginLimit;
    }

    public void setLoginLimit(RateLimitRule loginLimit) {
        this.loginLimit = loginLimit;
    }

    public RateLimitRule getStrictLimit() {
        return strictLimit;
    }

    public void setStrictLimit(RateLimitRule strictLimit) {
        this.strictLimit = strictLimit;
    }
}
