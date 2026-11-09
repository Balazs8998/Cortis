package com.cortis.core.security.session;

import com.cortis.auth.security.CortisUserDetailsService;
import com.cortis.auth.security.CortisUserPrincipal;
import com.cortis.core.security.audit.SecurityAuditService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Deny existing sessions if account was disabled/deleted or authorities were revoked.
 * Loads the current role/permission snapshot on each authenticated request (simple V1 policy).
 */
public class SessionAccountValidationFilter extends OncePerRequestFilter {
    private final CortisUserDetailsService detailsService;
    private final CortisAuthenticationEntryPoint entryPoint;
    private final SecurityAuditService auditService;

    public SessionAccountValidationFilter(CortisUserDetailsService detailsService,
                                          CortisAuthenticationEntryPoint entryPoint,
                                          SecurityAuditService auditService) {
        this.detailsService = detailsService;
        this.entryPoint = entryPoint;
        this.auditService = auditService;
    }

    @Override protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                              FilterChain chain) throws ServletException, IOException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof CortisUserPrincipal saved)) {
            chain.doFilter(request, response);
            return;
        }
        boolean valid = false;
        try {
            CortisUserPrincipal current = (CortisUserPrincipal) detailsService.loadUserByUsername(saved.getUsername());
            valid = current.isEnabled() && current.getUserId().equals(saved.getUserId())
                    && current.getAuthorities().equals(saved.getAuthorities());
        } catch (UsernameNotFoundException ignored) {
            // Deliberately do not reveal whether the user account was deleted or disabled.
        }
        if (!valid) {
            auditService.record("SESSION_REVOKED", "REJECTED", saved.getUserId(), request);
            new SecurityContextLogoutHandler().logout(request, response, authentication);
            entryPoint.commence(request, response,
                    new InsufficientAuthenticationException("Session no longer valid"));
            return;
        }
        chain.doFilter(request, response);
    }
}
