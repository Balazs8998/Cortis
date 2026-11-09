package com.cortis.core.security.config;

import com.cortis.auth.controller.AuthController;
import com.cortis.auth.service.AuthService;
import com.cortis.auth.security.ChipCardAuthenticationProvider;
import com.cortis.auth.security.CortisUserDetailsService;
import com.cortis.core.security.audit.SecurityAuditService;
import com.cortis.core.security.ratelimit.RateLimitKeyResolver;
import com.cortis.core.security.ratelimit.RateLimitResult;
import com.cortis.core.security.ratelimit.RateLimitService;
import com.cortis.core.security.ratelimit.RateLimitRule;
import com.cortis.core.security.session.CortisAccessDeniedHandler;
import com.cortis.core.security.session.CortisAuthenticationEntryPoint;
import com.cortis.core.security.session.SessionLoginService;
import com.cortis.core.security.ratelimit.request.RateLimitRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = AuthController.class)
@Import({SecurityConfig.class, CortisAuthenticationEntryPoint.class, CortisAccessDeniedHandler.class})
class SessionSecurityMvcTest {
    @Autowired MockMvc mvc;
    @MockBean AuthService authService;
    @MockBean SessionLoginService sessionLoginService;
    @MockBean SecurityAuditService auditService;
    @MockBean CortisUserDetailsService detailsService;
    @MockBean ChipCardAuthenticationProvider chipProvider;
    @MockBean RateLimitService rateLimitService;
    @MockBean RateLimitKeyResolver keyResolver;

    @BeforeEach void allowRateLimits() {
        RateLimitRule rule = new RateLimitRule();
        rule.setMaxRequests(100);
        rule.setWindowSeconds(60);
        when(keyResolver.resolve(any())).thenReturn(new RateLimitRequest("test", rule));
        when(rateLimitService.checkLimit(anyString(), any())).thenReturn(
                new RateLimitResult(true, 100, 99, Instant.now().plusSeconds(60)));
    }

    @Test void unauthenticatedMeReturns401() throws Exception {
        mvc.perform(get("/api/auth/me")).andExpect(status().isUnauthorized());
    }

    @Test void publicCsrfEndpointReturnsToken() throws Exception {
        mvc.perform(get("/api/auth/csrf"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").exists());
    }

    @Test void loginWithoutCsrfRejectedBeforeController() throws Exception {
        mvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"userName\":\"bad\",\"password\":\"bad\"}"))
                .andExpect(status().isForbidden());
        verifyNoInteractions(authService);
    }
}
