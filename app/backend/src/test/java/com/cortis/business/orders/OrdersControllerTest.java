package com.cortis.business.orders;

import com.cortis.business.orders.OrderDtos.Scope;
import com.cortis.business.orders.OrderDtos.Summary;
import org.junit.jupiter.api.Test;
import org.springframework.security.access.prepost.PreAuthorize;

import java.lang.reflect.Method;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/** Contract guard against accidentally exposing order endpoints without method permissions. */
class OrdersControllerTest {
    @Test void allFiveEndpointsExplicitlyRequireTheirPermission() throws Exception {
        List<Method> endpoints = List.of(
                OrdersController.class.getMethod("list", String.class, Scope.class, int.class, int.class),
                OrdersController.class.getMethod("summary"),
                OrdersController.class.getMethod("detail", java.util.UUID.class),
                OrdersController.class.getMethod("create", OrderDtos.Create.class,
                        com.cortis.auth.security.CortisUserPrincipal.class),
                OrdersController.class.getMethod("changeStatus", java.util.UUID.class,
                        OrderDtos.StatusChange.class, com.cortis.auth.security.CortisUserPrincipal.class));
        for (int i = 0; i < endpoints.size(); i++) {
            PreAuthorize guard = endpoints.get(i).getAnnotation(PreAuthorize.class);
            assertNotNull(guard, endpoints.get(i).getName());
            assertEquals("hasAuthority('CORTIS_ORDERS_" + (i < 3 ? "READ" : "WRITE") + "')", guard.value());
        }
    }
}
