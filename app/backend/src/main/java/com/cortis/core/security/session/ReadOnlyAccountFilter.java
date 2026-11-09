package com.cortis.core.security.session;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Set;

/**
 * Safeguard for explicit read-only application roles. This is needed because
 * some older endpoints only require authentication, not a method permission.
 * GET/HEAD/OPTIONS are allowed; POST logout is the only permitted state-changing request.
 */
public final class ReadOnlyAccountFilter extends OncePerRequestFilter {
    private static final Set<String> SAFE_METHODS = Set.of("GET", "HEAD", "OPTIONS");
    private static final String READONLY_ROLE = "ROLE_CORTIS_READONLY";

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain chain) throws ServletException, IOException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        boolean readonly = authentication != null && authentication.isAuthenticated()
                && authentication.getAuthorities().stream()
                    .anyMatch(authority -> READONLY_ROLE.equals(authority.getAuthority()));
        boolean logout = "POST".equals(request.getMethod())
                && "/api/auth/logout".equals(request.getServletPath());
        if (readonly && !SAFE_METHODS.contains(request.getMethod()) && !logout) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.setCharacterEncoding(StandardCharsets.UTF_8.name());
            response.setContentType("application/json");
            response.setHeader("Cache-Control", "no-store");
            response.getWriter().write("{\"errorCode\":\"READ_ONLY_ACCOUNT\","
                    + "\"message\":\"Read-only account cannot modify data.\"}");
            return;
        }
        chain.doFilter(request, response);
    }
}
