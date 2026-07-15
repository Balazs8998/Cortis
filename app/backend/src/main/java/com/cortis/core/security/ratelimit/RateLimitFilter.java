package com.cortis.core.security.ratelimit;

import com.cortis.core.security.ratelimit.request.RateLimitRequest;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import lombok.extern.slf4j.Slf4j;

// TODO: Review exception/logging integration for RateLimitFilter response.

@Slf4j
public class RateLimitFilter extends OncePerRequestFilter{

    private final RateLimitService rateLimitService;
    private final RateLimitKeyResolver keyResolver;

    public RateLimitFilter(RateLimitService rateLimitService, RateLimitKeyResolver keyResolver) {
        this.rateLimitService = rateLimitService;
        this.keyResolver = keyResolver;

    }


    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException{

        RateLimitRequest rateLimitRequest = keyResolver.resolve(request);

        RateLimitResult result = rateLimitService.checkLimit(
                rateLimitRequest.key(),
                rateLimitRequest.rule()
        );

        if (result.isAllowed()) {
            filterChain.doFilter(request, response);
            return;
        }

        writeRateLimitExceededResponse(response, result);
    }


    private void writeRateLimitExceededResponse(HttpServletResponse response,
                                                RateLimitResult result) throws IOException {
        response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        // igen hasznos headerek. dev yml futásánál login-limit 20
        response.setHeader("X-RateLimit-Limit", String.valueOf(result.getLimit()));
        response.setHeader("X-RateLimit-Remaining", String.valueOf(result.getRemainingRequests()));

        if (result.getResetAt() != null) {
            response.setHeader("X-RateLimit-Reset", result.getResetAt().toString());
        }

        response.getWriter().write("""
                {
                  "status": 429,
                  "errorCode": "RATE_LIMIT_EXCEEDED",
                  "messageKey": "error.rate.limit.exceeded"
                }
                """);
    }

}
