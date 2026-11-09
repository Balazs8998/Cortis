package com.cortis.core.security.session;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.session.SessionAuthenticationStrategy;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.stereotype.Service;

@Service
public class SessionLoginService {
    private final SecurityContextRepository repository;
    private final SessionAuthenticationStrategy strategy;

    public SessionLoginService(SecurityContextRepository repository, SessionAuthenticationStrategy strategy) {
        this.repository = repository;
        this.strategy = strategy;
    }

    public void signIn(Authentication authentication, HttpServletRequest request, HttpServletResponse response) {
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new IllegalArgumentException("Only authenticated principals may be saved");
        }
        // Do not simply call setAuthentication: controllers must explicitly persist the context.
        strategy.onAuthentication(authentication, request, response);
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(authentication);
        SecurityContextHolder.setContext(context);
        repository.saveContext(context, request, response);
    }
}
