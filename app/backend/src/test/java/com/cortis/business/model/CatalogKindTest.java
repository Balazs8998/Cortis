package com.cortis.business.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class CatalogKindTest {
    @Test void mapsOnlyKnownTableNames() {
        assertEquals("specification.tool_master", CatalogKind.parse("tools").masterTable());
        assertEquals("company.holder_inventory", CatalogKind.parse("holders").inventoryTable());
        assertEquals("specification.interface_type", CatalogKind.parse("interfaces").typeTable());
        assertEquals("company.machine_inventory", CatalogKind.parse("machines").inventoryTable());
        assertEquals("company.material_inventory", CatalogKind.parse("materials").inventoryTable());
        assertEquals("specification.accessory_master", CatalogKind.parse("accessories").masterTable());
        assertEquals("specification.part_type", CatalogKind.parse("parts").typeTable());
    }
    @Test void rejectsSqlInjectionAndUnknownKinds() {
        assertThrows(BusinessException.class, () -> CatalogKind.parse("tools;DROP TABLE personal.users"));
        assertThrows(BusinessException.class, () -> CatalogKind.parse("spindles"));
    }
    @Test void machineHasNoQuantity() {
        assertFalse(CatalogKind.MACHINES.stocked());
        assertTrue(CatalogKind.TOOLS.stocked());
        assertTrue(CatalogKind.MATERIALS.stocked());
        assertTrue(CatalogKind.ACCESSORIES.stocked());
        assertTrue(CatalogKind.PARTS.stocked());
    }
}
