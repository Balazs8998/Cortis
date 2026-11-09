package com.cortis.business.orders;

import com.cortis.business.model.BusinessException;
import com.cortis.business.orders.OrderDtos.Status;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class OrdersServiceTest {
    @Test void businessCodeUsesGermanAuftragPrefixAndDatabaseSequenceValue() {
        assertEquals("AU000001", OrdersService.businessCode(1));
        assertEquals("AU001234", OrdersService.businessCode(1234));
        assertEquals("AU1000000", OrdersService.businessCode(1_000_000));
        assertThrows(IllegalArgumentException.class, () -> OrdersService.businessCode(0));
    }
    @Test void monthRangeCorrectlyHandlesDecemberAndLeapYear() {
        assertEquals(LocalDate.of(2027,1,1),
                OrdersService.monthRange(LocalDate.of(2026,12,18)).next());
        assertEquals(LocalDate.of(2024,2,1),
                OrdersService.monthRange(LocalDate.of(2024,2,29)).first());
        assertEquals(LocalDate.of(2024,3,1),
                OrdersService.monthRange(LocalDate.of(2024,2,29)).next());
    }
    @Test void pagingAndSearchAreBounded() {
        assertEquals(50, OrdersService.pageSize(50));
        assertEquals(0, OrdersService.pageOffset(0));
        assertThrows(BusinessException.class, () -> OrdersService.pageSize(101));
        assertThrows(BusinessException.class, () -> OrdersService.pageSize(0));
        assertThrows(BusinessException.class, () -> OrdersService.pageOffset(-1));
        assertEquals("AU00001", OrdersService.normalizeSearch("  AU00001  "));
        assertThrows(BusinessException.class, () -> OrdersService.normalizeSearch("x".repeat(101)));
    }
    @Test void statusMachineDoesNotReopenCompletedOrCancelledOrders() {
        assertTrue(OrdersService.allowedTransition(Status.NEW, Status.IN_PROGRESS));
        assertTrue(OrdersService.allowedTransition(Status.IN_PROGRESS, Status.READY));
        assertTrue(OrdersService.allowedTransition(Status.READY, Status.SHIPPED));
        assertTrue(OrdersService.allowedTransition(Status.READY, Status.IN_PROGRESS));
        assertTrue(OrdersService.allowedTransition(Status.NEW, Status.CANCELLED));
        assertFalse(OrdersService.allowedTransition(Status.NEW, Status.SHIPPED));
        assertFalse(OrdersService.allowedTransition(Status.SHIPPED, Status.NEW));
        assertFalse(OrdersService.allowedTransition(Status.CANCELLED, Status.NEW));
        assertFalse(OrdersService.allowedTransition(Status.READY, Status.READY));
    }
}
