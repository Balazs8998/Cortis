package com.cortis.auth.controller;

import com.cortis.auth.dto.LoginRequest;
import com.cortis.auth.dto.LoginResponse;
import com.cortis.auth.dto.LoginWithChipRequest;
import com.cortis.auth.service.AuthService;
import com.cortis.core.security.audit.SecurityAuditService;
import com.cortis.core.security.session.SessionLoginService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.*;
import com.cortis.auth.security.CortisUserPrincipal;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final AuthService authService;
    private final SessionLoginService sessionLoginService;
    private final SecurityAuditService auditService;

    public AuthController(AuthService authService, SessionLoginService sessionLoginService,
                          SecurityAuditService auditService) {
        this.authService = authService;
        this.sessionLoginService = sessionLoginService;
        this.auditService = auditService;
    }

    @GetMapping("/csrf")
    public Map<String, String> csrf(CsrfToken csrfToken) {
        // Forces the deferred token to be generated and written as XSRF-TOKEN cookie.
        return Map.of("headerName", csrfToken.getHeaderName(), "token", csrfToken.getToken());
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest body,
                                               HttpServletRequest request, HttpServletResponse response) {
        try {
            Authentication authentication = authService.login(body);
            sessionLoginService.signIn(authentication, request, response);
            auditService.record("LOGIN_PASSWORD", "SUCCESS",
                    ((CortisUserPrincipal) authentication.getPrincipal()).getUserId(), request);
            return ResponseEntity.ok(LoginResponse.from(authentication));
        } catch (AuthenticationException exception) {
            auditService.record("LOGIN_PASSWORD", "REJECTED", null, request);
            throw exception;
        }
    }

    @PostMapping("/loginWithChip")
    public ResponseEntity<LoginResponse> loginWithChip(@Valid @RequestBody LoginWithChipRequest body,
                                                       HttpServletRequest request, HttpServletResponse response) {
        try {
            Authentication authentication = authService.loginWithChip(body);
            sessionLoginService.signIn(authentication, request, response);
            auditService.record("LOGIN_CHIP", "SUCCESS",
                    ((CortisUserPrincipal) authentication.getPrincipal()).getUserId(), request);
            return ResponseEntity.ok(LoginResponse.from(authentication));
        } catch (AuthenticationException exception) {
            auditService.record("LOGIN_CHIP", "REJECTED", null, request);
            throw exception;
        }
    }

    @GetMapping("/me")
    public LoginResponse me(@AuthenticationPrincipal CortisUserPrincipal principal) {
        return LoginResponse.from(principal);
    }
    // POST /api/auth/logout is implemented by Spring Security's LogoutFilter.
}
