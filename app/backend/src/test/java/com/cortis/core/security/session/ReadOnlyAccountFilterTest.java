package com.cortis.core.security.session;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class ReadOnlyAccountFilterTest {
    @AfterEach void clear() { SecurityContextHolder.clearContext(); }

    private static void authenticate(String authority) {
        SecurityContextHolder.getContext().setAuthentication(
                UsernamePasswordAuthenticationToken.authenticated("test-user", null,
                        List.of(new SimpleGrantedAuthority(authority))));
    }

    @Test void readonlyAccountCannotPost() throws Exception {
        authenticate("ROLE_CORTIS_READONLY");
        MockHttpServletRequest req = new MockHttpServletRequest("POST", "/api/v1/inventory/tools");
        req.setServletPath("/api/v1/inventory/tools");
        MockHttpServletResponse res = new MockHttpServletResponse();
        MockFilterChain chain = new MockFilterChain();
        new ReadOnlyAccountFilter().doFilter(req, res, chain);
        assertEquals(403, res.getStatus());
        assertTrue(res.getContentAsString().contains("READ_ONLY_ACCOUNT"));
        assertNull(chain.getRequest());
    }

    @Test void readonlyAccountCanRead() throws Exception {
        authenticate("ROLE_CORTIS_READONLY");
        MockHttpServletRequest req = new MockHttpServletRequest("GET", "/api/v1/inventory/tools");
        req.setServletPath("/api/v1/inventory/tools");
        MockHttpServletResponse res = new MockHttpServletResponse();
        MockFilterChain chain = new MockFilterChain();
        new ReadOnlyAccountFilter().doFilter(req, res, chain);
        assertEquals(200, res.getStatus());
        assertNotNull(chain.getRequest());
    }

    @Test void readonlyAccountMayLogout() throws Exception {
        authenticate("ROLE_CORTIS_READONLY");
        MockHttpServletRequest req = new MockHttpServletRequest("POST", "/api/auth/logout");
        req.setServletPath("/api/auth/logout");
        MockHttpServletResponse res = new MockHttpServletResponse();
        MockFilterChain chain = new MockFilterChain();
        new ReadOnlyAccountFilter().doFilter(req, res, chain);
        assertNotNull(chain.getRequest());
    }

    @Test void adminAccountMayPost() throws Exception {
        authenticate("ROLE_CORTIS_ADMIN");
        MockHttpServletRequest req = new MockHttpServletRequest("POST", "/api/v1/orders");
        req.setServletPath("/api/v1/orders");
        MockHttpServletResponse res = new MockHttpServletResponse();
        MockFilterChain chain = new MockFilterChain();
        new ReadOnlyAccountFilter().doFilter(req, res, chain);
        assertNotNull(chain.getRequest());
    }
}
