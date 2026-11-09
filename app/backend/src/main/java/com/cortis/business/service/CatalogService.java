package com.cortis.business.service;

import com.cortis.business.model.CatalogKind;
import com.cortis.business.model.BusinessException;
import com.cortis.business.model.BusinessDtos.*;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.UUID;

@Service
public class CatalogService {
    private final JdbcTemplate jdbc;
    public CatalogService(JdbcTemplate jdbc) { this.jdbc = jdbc; }

    static int pageSize(int requested) {
        if (requested < 1 || requested > 100)
            throw new BusinessException(400, "INVALID_LIMIT", "Limit must be between 1 and 100");
        return requested;
    }
    static int pageOffset(int requested) {
        if (requested < 0 || requested > 1_000_000)
            throw new BusinessException(400, "INVALID_OFFSET", "Offset must be between 0 and 1000000");
        return requested;
    }
    static String searchText(String q) {
        if (q == null) return "";
        String value = q.trim();
        if (value.length() > 120) throw new BusinessException(400, "QUERY_TOO_LONG", "Search text exceeds 120 characters");
        return value;
    }
    static RowMapper<MasterView> masterMapper() {
        return (rs, n) -> new MasterView(
                rs.getObject("id", UUID.class), rs.getObject("type_id", UUID.class),
                rs.getString("type_name"), rs.getString("manufacturer_code"),
                rs.getString("manufacturer"), rs.getString("description"),
                rs.getString("order_code"), rs.getString("link"), rs.getString("catalog"));
    }
    private RowMapper<TypeView> typeMapper(CatalogKind kind) {
        return (rs, n) -> new TypeView(rs.getObject("id", UUID.class), rs.getString("name"),
                rs.getString("description"), kind == CatalogKind.HOLDERS ? rs.getBoolean("is_interface") : null);
    }
    private String masterSelect(CatalogKind kind) {
        return "SELECT m.id, m.type_id, t.name AS type_name, m.manufacturer_code, " +
                "m.manufacturer, m.description, m.order_code, m.link, m.catalog FROM " +
                kind.masterTable() + " m JOIN " + kind.typeTable() +
                " t ON t.id=m.type_id AND t.deleted_at IS NULL WHERE m.deleted_at IS NULL";
    }
    @Transactional(readOnly = true)
    public List<MasterView> list(CatalogKind kind, String q, int limit, int offset) {
        String text = searchText(q);
        String sql = masterSelect(kind) + " AND (? = '' OR POSITION(LOWER(?) IN " +
                "LOWER(m.manufacturer || ' ' || m.manufacturer_code || ' ' || COALESCE(m.description,''))) > 0) " +
                "ORDER BY m.manufacturer, m.manufacturer_code, m.id LIMIT ? OFFSET ?";
        return jdbc.query(sql, masterMapper(), text, text, pageSize(limit), pageOffset(offset));
    }
    @Transactional(readOnly = true)
    public MasterView get(CatalogKind kind, UUID id) {
        List<MasterView> items = jdbc.query(masterSelect(kind) + " AND m.id = ?", masterMapper(), id);
        if (items.isEmpty()) throw new BusinessException(404, "MASTER_NOT_FOUND", "Catalog item not found");
        return items.get(0);
    }
    @Transactional(readOnly = true)
    public List<TypeView> types(CatalogKind kind) {
        String cols = kind == CatalogKind.HOLDERS ? "id, name, description, is_interface" : "id, name, description";
        return jdbc.query("SELECT " + cols + " FROM " + kind.typeTable() +
                " WHERE deleted_at IS NULL ORDER BY name LIMIT 500", typeMapper(kind));
    }
    @Transactional
    public TypeView createType(CatalogKind kind, TypeCreate request, String actor) {
        String name = request.name().trim();
        UUID id;
        if (kind == CatalogKind.HOLDERS) {
            id = jdbc.queryForObject("INSERT INTO " + kind.typeTable() +
                    " (name, description, is_interface, created_by) VALUES (?, ?, ?, ?) RETURNING id", UUID.class,
                    name, request.description(), request.isInterface() == null || request.isInterface(), actor);
        } else {
            id = jdbc.queryForObject("INSERT INTO " + kind.typeTable() +
                    " (name, description, created_by) VALUES (?, ?, ?) RETURNING id", UUID.class, name, request.description(), actor);
        }
        return new TypeView(id, name, request.description(), kind == CatalogKind.HOLDERS
                ? request.isInterface() == null || request.isInterface() : null);
    }
    @Transactional
    public MasterView create(CatalogKind kind, MasterCreate request, String actor) {
        Integer count = jdbc.queryForObject("SELECT count(*) FROM " + kind.typeTable() +
                " WHERE id = ? AND deleted_at IS NULL", Integer.class, request.typeId());
        if (count == null || count == 0) throw new BusinessException(400, "INVALID_TYPE", "Catalog type does not exist");
        UUID id = jdbc.queryForObject("INSERT INTO " + kind.masterTable() + " (type_id, manufacturer_code, " +
                        "manufacturer, description, order_code, link, catalog, created_by) VALUES (?, ?, ?, ?, ?, ?, ?, ?) RETURNING id",
                UUID.class, request.typeId(), request.manufacturerCode().trim(), request.manufacturer().trim(),
                request.description(), request.orderCode(), request.link(), request.catalog(), actor);
        return get(kind, id);
    }
}
