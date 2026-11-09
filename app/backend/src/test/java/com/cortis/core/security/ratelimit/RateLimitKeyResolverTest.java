package com.cortis.core.security.ratelimit;

import com.cortis.auth.entity.UserEntity;
import com.cortis.auth.security.CortisUserPrincipal;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.UUID;
import static org.junit.jupiter.api.Assertions.*;

class RateLimitKeyResolverTest {
    @AfterEach void cleanContext() { SecurityContextHolder.clearContext(); }

    @Test void agentAuthenticatedRequestsAreKeyedByUserIdNotIp() {
        RateLimitProperties props = new RateLimitProperties();
        RateLimitRule perUser = new RateLimitRule();
        props.setAgentLimit(perUser);
        RateLimitKeyResolver resolver = new RateLimitKeyResolver(props);
        UserEntity user = new UserEntity();
        UUID id = UUID.randomUUID();
        user.setId(id);
        user.setUsername("demo");
        CortisUserPrincipal principal = new CortisUserPrincipal(user);
        SecurityContextHolder.getContext().setAuthentication(
                UsernamePasswordAuthenticationToken.authenticated(principal, null, principal.getAuthorities()));
        MockHttpServletRequest request = new MockHttpServletRequest("POST", "/api/agent/chat");
        request.setRemoteAddr("192.0.2.3");
        assertEquals("AGENT:USER:" + id, resolver.resolve(request).key());
    }
}
