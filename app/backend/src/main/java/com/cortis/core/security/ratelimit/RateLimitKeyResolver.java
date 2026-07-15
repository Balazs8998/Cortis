package com.cortis.core.security.ratelimit;

import com.cortis.core.security.ratelimit.request.RateLimitRequest;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;

@Component
public class RateLimitKeyResolver {

    private static final String LOGIN_GROUP = "LOGIN";
 //   private static final String STRICT_GROUP = "STRICT";
    private static final String DEFAULT_GROUP = "DEFAULT";

    private final RateLimitProperties properties;

    public RateLimitKeyResolver(RateLimitProperties properties) {
        this.properties = properties;
    }

    public RateLimitRequest resolve(HttpServletRequest request) {
        String clientIp = resolveClientIp(request);
        String path = request.getRequestURI();

        if (path.equals("/api/auth/login") || path.equals("/api/auth/loginWithChip")) {
            return new RateLimitRequest(
                    clientIp + ":" + LOGIN_GROUP,
                    properties.getLoginLimit()
            );
        }


        return new RateLimitRequest(
                clientIp + ":" + DEFAULT_GROUP,
                properties.getDefaultLimit()
        );
    }

    private String resolveClientIp(HttpServletRequest request) {
        return request.getRemoteAddr();

        /*
         * Proxy mögött később ez nem biztos, hogy a valódi kliens ip lesz !. ott majd ilyenek jöhetnek :
         * x-forwarded-for
         * x-real-ip
         * forwarder
         * */
    }

}
