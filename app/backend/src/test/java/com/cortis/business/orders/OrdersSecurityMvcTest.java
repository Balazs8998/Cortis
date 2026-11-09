package com.cortis.business.orders;

import com.cortis.auth.security.ChipCardAuthenticationProvider;
import com.cortis.auth.security.CortisUserDetailsService;
import com.cortis.core.security.audit.SecurityAuditService;
import com.cortis.core.security.config.SecurityConfig;
import com.cortis.core.security.ratelimit.RateLimitKeyResolver;
import com.cortis.core.security.ratelimit.RateLimitResult;
import com.cortis.core.security.ratelimit.RateLimitRule;
import com.cortis.core.security.ratelimit.RateLimitService;
import com.cortis.core.security.ratelimit.request.RateLimitRequest;
import com.cortis.core.security.session.CortisAccessDeniedHandler;
import com.cortis.core.security.session.CortisAuthenticationEntryPoint;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = OrdersController.class)
@Import({SecurityConfig.class, CortisAuthenticationEntryPoint.class, CortisAccessDeniedHandler.class})
class OrdersSecurityMvcTest {
    @Autowired MockMvc mvc;
    @MockBean OrdersService orders;
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

    @Test void unauthenticatedOrdersRequestReturns401() throws Exception {
        mvc.perform(get("/api/v1/orders")).andExpect(status().isUnauthorized());
    }

    @Test @WithMockUser(authorities = "CORTIS_INVENTORY_READ")
    void unrelatedInventoryPermissionCannotReadOrders() throws Exception {
        mvc.perform(get("/api/v1/orders")).andExpect(status().isForbidden());
        verifyNoInteractions(orders);
    }

    @Test @WithMockUser(authorities = "CORTIS_ORDERS_READ")
    void orderReadPermissionWorksWithoutWritePermission() throws Exception {
        when(orders.list(eq(""), eq(OrderDtos.Scope.thisMonth), eq(50), eq(0))).thenReturn(List.of());
        mvc.perform(get("/api/v1/orders")).andExpect(status().isOk()).andExpect(content().json("[]"));
        mvc.perform(get("/api/v1/orders/summary")).andExpect(status().isOk());
        mvc.perform(post("/api/v1/orders").with(csrf()).contentType("application/json")
                .content("{}"))
                .andExpect(status().isForbidden());
    }

    @Test @WithMockUser(authorities = "CORTIS_ORDERS_WRITE")
    void writingWithoutCsrfIsForbidden() throws Exception {
        mvc.perform(post("/api/v1/orders").contentType("application/json").content("{}"))
                .andExpect(status().isForbidden());
        verifyNoInteractions(orders);
    }
}
