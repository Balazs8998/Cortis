package com.cortis.core.security.ratelimit;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "security.rate-limit")
public class RateLimitProperties {
    private boolean enabled = true;
    private RateLimitRule defaultLimit;
    private RateLimitRule loginLimit;
    private RateLimitRule strictLimit;
    private RateLimitRule agentLimit;
    private RateLimitRule agentGlobalLimit;
    public boolean isEnabled() { return enabled; }
    public void setEnabled(boolean enabled) { this.enabled = enabled; }
    public RateLimitRule getDefaultLimit() { return defaultLimit; }
    public void setDefaultLimit(RateLimitRule value) { defaultLimit = value; }
    public RateLimitRule getLoginLimit() { return loginLimit; }
    public void setLoginLimit(RateLimitRule value) { loginLimit = value; }
    public RateLimitRule getStrictLimit() { return strictLimit; }
    public void setStrictLimit(RateLimitRule value) { strictLimit = value; }
    public RateLimitRule getAgentLimit() { return agentLimit; }
    public void setAgentLimit(RateLimitRule value) { agentLimit = value; }
    public RateLimitRule getAgentGlobalLimit() { return agentGlobalLimit; }
    public void setAgentGlobalLimit(RateLimitRule value) { agentGlobalLimit = value; }
}
