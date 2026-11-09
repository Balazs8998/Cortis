package com.cortis.core.security.ratelimit;

import com.cortis.auth.security.CortisUserPrincipal;
import com.cortis.core.security.ratelimit.request.RateLimitRequest;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class RateLimitKeyResolver {
    private final RateLimitProperties properties;
    public RateLimitKeyResolver(RateLimitProperties properties) { this.properties = properties; }

    public RateLimitRequest resolve(HttpServletRequest request) {
        String path = request.getRequestURI();
        String clientIp = request.getRemoteAddr(); // do not trust unvalidated X-Forwarded-For
        if (path.equals("/api/auth/login") || path.equals("/api/auth/loginWithChip")) {
            return new RateLimitRequest("LOGIN:IP:" + clientIp, properties.getLoginLimit());
        }
        if (path.startsWith("/api/agent/") || path.equals("/api/agent")) {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication != null && authentication.getPrincipal() instanceof CortisUserPrincipal principal) {
                return new RateLimitRequest("AGENT:USER:" + principal.getUserId(), properties.getAgentLimit());
            }
            return new RateLimitRequest("AGENT:IP:" + clientIp, properties.getAgentLimit());
        }
        return new RateLimitRequest("DEFAULT:IP:" + clientIp, properties.getDefaultLimit());
    }

    public RateLimitRequest resolveGlobalAgent(HttpServletRequest request) {
        String path = request.getRequestURI();
        if (path.startsWith("/api/agent/") || path.equals("/api/agent")) {
            return new RateLimitRequest("AGENT:GLOBAL", properties.getAgentGlobalLimit());
        }
        return null;
    }
}
