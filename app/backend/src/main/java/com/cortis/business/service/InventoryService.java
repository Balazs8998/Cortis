package com.cortis.business.service;

import com.cortis.business.model.CatalogKind;
import com.cortis.business.model.BusinessException;
import com.cortis.business.model.BusinessDtos.*;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.UUID;

@Service
public class InventoryService {
    private final JdbcTemplate jdbc;
    public InventoryService(JdbcTemplate jdbc) { this.jdbc = jdbc; }

    private RowMapper<InventoryView> mapper(CatalogKind kind) {
        return (rs, n) -> new InventoryView(rs.getObject("id", UUID.class),
                rs.getObject("master_id", UUID.class), rs.getString("master_code"),
                rs.getString("inventory_code"), rs.getString("name"), rs.getString("location"),
                kind.stocked() ? rs.getInt("quantity") : null);
    }
    private String inventorySelect(CatalogKind kind) {
        return "SELECT i.id, i." + kind.masterFkColumn() + " AS master_id, " +
                "m.manufacturer_code AS master_code, i." + kind.codeColumn() +
                " AS inventory_code, i.name, i.location, " +
                (kind.stocked() ? "i.quantity" : "NULL::INTEGER AS quantity") +
                " FROM " + kind.inventoryTable() + " i JOIN " + kind.masterTable() + " m ON m.id=i." +
                kind.masterFkColumn() + " AND m.deleted_at IS NULL WHERE i.deleted_at IS NULL";
    }
    @Transactional(readOnly = true)
    public List<InventoryView> list(CatalogKind kind, int limit, int offset) {
        return jdbc.query(inventorySelect(kind) + " ORDER BY i.name, i.id LIMIT ? OFFSET ?",
                mapper(kind), CatalogService.pageSize(limit), CatalogService.pageOffset(offset));
    }
    @Transactional(readOnly = true)
    public InventoryView get(CatalogKind kind, UUID id) {
        List<InventoryView> result = jdbc.query(inventorySelect(kind) + " AND i.id=?", mapper(kind), id);
        if (result.isEmpty()) throw new BusinessException(404, "INVENTORY_NOT_FOUND", "Inventory item not found");
        return result.get(0);
    }
    @Transactional
    public InventoryView create(CatalogKind kind, InventoryCreate request, UUID actor) {
        Integer exists = jdbc.queryForObject("SELECT count(*) FROM " + kind.masterTable() +
                " WHERE id=? AND deleted_at IS NULL", Integer.class, request.masterId());
        if (exists == null || exists == 0) throw new BusinessException(400, "INVALID_MASTER", "Catalog master does not exist");
        String location = request.location() == null || request.location().isBlank() ? "pending" : request.location().trim();
        int quantity = request.quantity() == null ? 0 : request.quantity();
        UUID id;
        if (kind.stocked()) {
            id = jdbc.queryForObject("INSERT INTO " + kind.inventoryTable() + " (" +
                    kind.masterFkColumn() +
                    ", name, location, quantity) VALUES (?, ?, ?, ?) RETURNING id", UUID.class,
                    request.masterId(), request.name().trim(), location, quantity);
        } else {
            if (request.quantity() != null)
                throw new BusinessException(400, "MACHINE_HAS_NO_STOCK", "Machines do not have a quantity column");
            id = jdbc.queryForObject("INSERT INTO " + kind.inventoryTable() + " (" +
                    kind.masterFkColumn() +
                    ", name, location) VALUES (?, ?, ?) RETURNING id", UUID.class,
                    request.masterId(), request.name().trim(), location);
        }
        if (kind.stocked() && quantity > 0) {
            jdbc.update("INSERT INTO company.inventory_movement (" + movementFk(kind) +
                            ", previous_quantity, quantity_delta, resulting_quantity, reason, performed_by) " +
                            "VALUES (?, 0, ?, ?, 'Initial inventory quantity', ?)",
                    id, quantity, quantity, actor);
        }
        return get(kind, id);
    }
    private String movementFk(CatalogKind kind) {
        return switch (kind) {
            case TOOLS -> "tool_inventory_id";
            case HOLDERS -> "holder_inventory_id";
            case INTERFACES -> "interface_inventory_id";
            case MATERIALS -> "material_inventory_id";
            case ACCESSORIES -> "accessory_inventory_id";
            case PARTS -> "part_inventory_id";
            case MACHINES -> throw new BusinessException(400, "MACHINE_HAS_NO_STOCK", "Machines do not have a quantity column");
        };
    }
    @Transactional
    public InventoryView adjust(CatalogKind kind, UUID id, StockAdjustment adjustment, UUID actor) {
        String fk = movementFk(kind);
        if (adjustment.delta() == 0)
            throw new BusinessException(400, "ZERO_ADJUSTMENT", "Quantity delta must not be zero");
        List<Integer> quantities = jdbc.query("SELECT quantity FROM " + kind.inventoryTable() +
                " WHERE id=? AND deleted_at IS NULL FOR UPDATE", (rs, n) -> rs.getInt(1), id);
        if (quantities.isEmpty())
            throw new BusinessException(404, "INVENTORY_NOT_FOUND", "Inventory item not found");
        int before = quantities.get(0);
        int after = resultingQuantity(before, adjustment.delta());
        jdbc.update("UPDATE " + kind.inventoryTable() +
                " SET quantity=?, updated_at=now(), updated_by=? WHERE id=?",
                after, actor.toString(), id);
        jdbc.update("INSERT INTO company.inventory_movement (" + fk +
                ", previous_quantity, quantity_delta, resulting_quantity, reason, performed_by) " +
                "VALUES (?, ?, ?, ?, ?, ?)", id, before, adjustment.delta(), after,
                adjustment.reason().trim(), actor);
        return get(kind, id);
    }
    static int resultingQuantity(int before, int delta) {
        long result = (long) before + delta;
        if (before < 0 || result < 0 || result > Integer.MAX_VALUE)
            throw new BusinessException(409, "INVALID_STOCK", "Resulting quantity must be between 0 and 2147483647");
        return (int) result;
    }

    /** Exact full-table DB aggregations, not only the first page of inventory. */
    @Transactional(readOnly = true)
    public List<InventorySummary> summary() {
        return java.util.Arrays.stream(CatalogKind.values()).map(kind -> {
            String sql = kind.stocked()
                    ? "SELECT COUNT(*) AS item_count, COALESCE(SUM(quantity), 0) AS amount FROM " + kind.inventoryTable() + " WHERE deleted_at IS NULL"
                    : "SELECT COUNT(*) AS item_count, NULL::BIGINT AS amount FROM " + kind.inventoryTable() + " WHERE deleted_at IS NULL";
            return jdbc.queryForObject(sql, (rs, index) -> new InventorySummary(
                    kind.slug(), rs.getLong("item_count"), kind.stocked() ? rs.getLong("amount") : null));
        }).toList();
    }

    /** Archive rather than destroy; non-empty stocked items must first be reconciled to zero. */
    @Transactional
    public void archive(CatalogKind kind, UUID id, UUID actor) {
        String sql = kind.stocked()
                ? "SELECT quantity FROM " + kind.inventoryTable() + " WHERE id=? AND deleted_at IS NULL FOR UPDATE"
                : "SELECT 0 AS quantity FROM " + kind.inventoryTable() + " WHERE id=? AND deleted_at IS NULL FOR UPDATE";
        List<Integer> found = jdbc.query(sql, (rs, n) -> rs.getInt(1), id);
        if (found.isEmpty()) throw new BusinessException(404, "INVENTORY_NOT_FOUND", "Inventory item not found");
        if (found.get(0) != 0) throw new BusinessException(409, "STOCK_NOT_EMPTY", "Reduce quantity to zero before archiving");
        jdbc.update("UPDATE " + kind.inventoryTable() +
                        " SET deleted_at=now(), updated_at=now(), updated_by=? WHERE id=? AND deleted_at IS NULL",
                actor.toString(), id);
    }

    @Transactional(readOnly = true)
    public List<MovementView> history(CatalogKind kind, UUID id, int limit) {
        String fk = movementFk(kind);
        get(kind, id); // 404 for unknown inventory records
        return jdbc.query("SELECT id, previous_quantity, quantity_delta, resulting_quantity, reason, " +
                "performed_by, performed_at FROM company.inventory_movement WHERE " + fk +
                "=? ORDER BY performed_at DESC, id DESC LIMIT ?", (rs, n) ->
                new MovementView(rs.getObject("id", UUID.class), rs.getInt("previous_quantity"),
                        rs.getInt("quantity_delta"), rs.getInt("resulting_quantity"), rs.getString("reason"),
                        rs.getObject("performed_by", UUID.class), rs.getTimestamp("performed_at").toInstant()),
                id, CatalogService.pageSize(limit));
    }
}
