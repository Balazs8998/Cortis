package com.cortis.business.service;

import com.cortis.business.model.BusinessException;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class InventoryServiceTest {
    @Test void addingAndIssuingStockIsDeterministic() {
        assertEquals(14, InventoryService.resultingQuantity(10, 4));
        assertEquals(0, InventoryService.resultingQuantity(10, -10));
    }
    @Test void preventsNegativeAndOverflowingStock() {
        assertThrows(BusinessException.class, () -> InventoryService.resultingQuantity(3, -4));
        assertThrows(BusinessException.class, () -> InventoryService.resultingQuantity(Integer.MAX_VALUE, 1));
    }
}
