package com.cortis.core.security.ratelimit;

import com.cortis.core.security.ratelimit.request.RateLimitRequest;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

public class RateLimitFilter extends OncePerRequestFilter {
    private final RateLimitService service;
    private final RateLimitKeyResolver resolver;
    public RateLimitFilter(RateLimitService service, RateLimitKeyResolver resolver) {
        this.service = service;
        this.resolver = resolver;
    }

    @Override protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                              FilterChain chain) throws ServletException, IOException {
        RateLimitRequest perClient = resolver.resolve(request);
        RateLimitResult clientResult = service.checkLimit(perClient.key(), perClient.rule());
        if (!clientResult.isAllowed()) {
            reject(response, clientResult);
            return;
        }
        RateLimitRequest globalAgent = resolver.resolveGlobalAgent(request);
        if (globalAgent != null) {
            RateLimitResult globalResult = service.checkLimit(globalAgent.key(), globalAgent.rule());
            if (!globalResult.isAllowed()) {
                reject(response, globalResult);
                return;
            }
        }
        chain.doFilter(request, response);
    }

    private void reject(HttpServletResponse response, RateLimitResult result) throws IOException {
        response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.setHeader("Cache-Control", "no-store");
        response.setHeader("X-RateLimit-Limit", String.valueOf(result.getLimit()));
        response.setHeader("X-RateLimit-Remaining", "0");
        if (result.getResetAt() != null) {
            long seconds = Math.max(1, result.getResetAt().getEpochSecond() - java.time.Instant.now().getEpochSecond());
            response.setHeader("Retry-After", String.valueOf(seconds));
        }
        response.getWriter().write("""
                {"status":429,"errorCode":"RATE_LIMIT_EXCEEDED","messageKey":"error.rate.limit.exceeded"}
                """);
    }
}
