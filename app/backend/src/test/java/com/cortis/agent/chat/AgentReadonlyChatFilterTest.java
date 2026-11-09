package com.cortis.agent.chat;

import com.cortis.core.security.session.ReadOnlyAccountFilter;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class AgentReadonlyChatFilterTest {
    @AfterEach void clearContext() { SecurityContextHolder.clearContext(); }

    private static boolean allowed(String method, String path) throws Exception {
        SecurityContextHolder.getContext().setAuthentication(
                UsernamePasswordAuthenticationToken.authenticated("cortis", null,
                        List.of(new SimpleGrantedAuthority("ROLE_CORTIS_READONLY"))));
        var request = new MockHttpServletRequest(method, path);
        request.setServletPath(path);
        var response = new MockHttpServletResponse();
        var chain = new MockFilterChain();
        new ReadOnlyAccountFilter().doFilter(request, response, chain);
        return chain.getRequest() != null;
    }

    @Test void readonlyMayChatButNeverWriteBusinessData() throws Exception {
        String id = "a78bb162-6c6f-48d9-a233-1ac5d4e10f49";
        assertTrue(allowed("POST", "/api/agent/conversations"));
        assertTrue(allowed("POST", "/api/agent/conversations/" + id + "/messages"));
        assertFalse(allowed("POST", "/api/v1/inventory/tools"));
        assertFalse(allowed("PATCH", "/api/v1/orders/" + id + "/status"));
        assertFalse(allowed("POST", "/api/agent/conversations/" + id + "/delete"));
        assertFalse(allowed("PUT", "/api/agent/conversations/" + id + "/messages"));
    }
}
