package com.cortis.business.orders;

import com.cortis.business.model.BusinessException;
import com.cortis.business.orders.OrderDtos.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.ZoneId;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.UUID;

@Service
public class OrdersService {
    private final JdbcTemplate jdbc;
    private final ZoneId zone;

    public OrdersService(JdbcTemplate jdbc, @Value("${cortis.orders.zone:Europe/Berlin}") String businessZone) {
        this.jdbc = jdbc;
        this.zone = ZoneId.of(businessZone);
    }

    private static final RowMapper<Summary> SUMMARY_MAPPER = (rs, ignored) ->
            new Summary(rs.getObject("id", UUID.class), rs.getString("order_code"),
                    rs.getString("customer"), rs.getObject("order_date", LocalDate.class),
                    rs.getObject("delivery_date", LocalDate.class),
                    Status.valueOf(rs.getString("status")), rs.getInt("item_count"),
                    rs.getString("note"));

    private static final String SELECT_SUMMARY = """
            SELECT o.id, o.order_code, o.customer, o.order_date, o.delivery_date,
                   o.status, o.note,
                   (SELECT count(*) FROM company.sales_order_line li WHERE li.sales_order_id = o.id) AS item_count
            FROM company.sales_order o WHERE o.deleted_at IS NULL
            """;

    static MonthRange monthRange(LocalDate date) {
        YearMonth month = YearMonth.from(date);
        return new MonthRange(month.atDay(1), month.plusMonths(1).atDay(1));
    }
    record MonthRange(LocalDate first, LocalDate next) {}

    static int pageSize(int value) {
        if (value < 1 || value > 100)
            throw new BusinessException(400, "INVALID_LIMIT", "limit must be between 1 and 100");
        return value;
    }
    static int pageOffset(int value) {
        if (value < 0 || value > 1_000_000)
            throw new BusinessException(400, "INVALID_OFFSET", "offset must be between 0 and 1000000");
        return value;
    }
    static String normalizeSearch(String q) {
        String value = q == null ? "" : q.trim();
        if (value.length() > 100)
            throw new BusinessException(400, "INVALID_SEARCH", "Order code search must be at most 100 characters");
        return value;
    }
    static boolean allowedTransition(Status from, Status to) {
        if (from == to) return false;
        return switch (from) {
            case NEW -> to == Status.IN_PROGRESS || to == Status.CANCELLED;
            case IN_PROGRESS -> to == Status.READY || to == Status.CANCELLED;
            case READY -> to == Status.SHIPPED || to == Status.IN_PROGRESS || to == Status.CANCELLED;
            case SHIPPED, CANCELLED -> false;
        };
    }

    @Transactional(readOnly = true)
    public List<Summary> list(String q, Scope scope, int limit, int offset) {
        String search = normalizeSearch(q);
        int size = pageSize(limit);
        int pageStart = pageOffset(offset);
        Objects.requireNonNull(scope, "scope");
        LocalDate today = LocalDate.now(zone);
        MonthRange month = monthRange(today);
        String sql = SELECT_SUMMARY + " AND (? = '' OR position(lower(?) in lower(o.order_code)) > 0)";
        // Three hard-coded query variants; no user-defined SQL interpolated.
        return switch (scope) {
            case all -> jdbc.query(sql + " ORDER BY o.delivery_date, o.order_code LIMIT ? OFFSET ?",
                    SUMMARY_MAPPER, search, search, size, pageStart);
            case thisMonth -> jdbc.query(sql + " AND o.delivery_date >= ? AND o.delivery_date < ? " +
                            "AND o.status NOT IN ('SHIPPED', 'CANCELLED') " +
                            "ORDER BY o.delivery_date, o.order_code LIMIT ? OFFSET ?",
                    SUMMARY_MAPPER, search, search, month.first(), month.next(), size, pageStart);
            case overdue -> jdbc.query(sql + " AND o.delivery_date < ? " +
                            "AND o.status NOT IN ('SHIPPED', 'CANCELLED') " +
                            "ORDER BY o.delivery_date, o.order_code LIMIT ? OFFSET ?",
                    SUMMARY_MAPPER, search, search, today, size, pageStart);
        };
    }

    @Transactional(readOnly = true)
    public Overview overview() {
        LocalDate today = LocalDate.now(zone);
        MonthRange month = monthRange(today);
        return jdbc.queryForObject("""
                SELECT
                  count(*) FILTER (WHERE delivery_date >= ? AND delivery_date < ?
                      AND status NOT IN ('SHIPPED','CANCELLED')) AS month_due,
                  count(*) FILTER (WHERE delivery_date < ?
                      AND status NOT IN ('SHIPPED','CANCELLED')) AS overdue,
                  count(*) FILTER (WHERE status = 'IN_PROGRESS') AS in_progress
                FROM company.sales_order WHERE deleted_at IS NULL
                """, (rs, n) -> new Overview(rs.getLong("month_due"),
                rs.getLong("overdue"), rs.getLong("in_progress")),
                month.first(), month.next(), today);
    }

    @Transactional(readOnly = true)
    public Summary get(UUID id) {
        List<Summary> found = jdbc.query(SELECT_SUMMARY + " AND o.id=?", SUMMARY_MAPPER, id);
        if (found.isEmpty()) throw new BusinessException(404, "ORDER_NOT_FOUND", "Order not found");
        return found.get(0);
    }

    @Transactional(readOnly = true)
    public Detail detail(UUID id) {
        Summary summary = get(id);
        List<Line> lines = jdbc.query("""
                SELECT id, position, item_code, description, quantity, unit
                FROM company.sales_order_line WHERE sales_order_id=? ORDER BY position
                """, (rs, index) -> new Line(rs.getObject("id", UUID.class), rs.getInt("position"),
                    rs.getString("item_code"), rs.getString("description"),
                    rs.getBigDecimal("quantity"), rs.getString("unit")), id);
        return new Detail(summary, lines);
    }

    static String businessCode(long next) {
        if (next < 1) throw new IllegalArgumentException("Sequence must be positive");
        return "AU" + String.format(Locale.ROOT, "%06d", next);
    }

    @Transactional
    public Summary create(Create request, UUID actor) {
        if (request.deliveryDate().isBefore(LocalDate.now(zone))) {
            throw new BusinessException(400, "DELIVERY_DATE_IN_PAST", "Delivery date cannot be in the past");
        }
        if (request.lines() == null || request.lines().isEmpty()) {
            throw new BusinessException(400, "MISSING_LINES", "An order must contain at least one line");
        }
        if (request.lines().size() > 100) {
            throw new BusinessException(400, "TOO_MANY_LINES", "Maximum 100 order lines");
        }
        // nextval is atomic under concurrent requests; gaps after rollback are intentional.
        Long next = jdbc.queryForObject("SELECT nextval('company.sales_order_number_seq')", Long.class);
        String code = businessCode(Objects.requireNonNull(next));
        UUID id = jdbc.queryForObject("""
                INSERT INTO company.sales_order
                    (order_code, customer, order_date, delivery_date, status, note, created_by)
                VALUES (?, ?, ?, ?, 'NEW', ?, ?) RETURNING id
                """, UUID.class, code, request.customer().trim(), LocalDate.now(zone),
                request.deliveryDate(), request.note(), actor);
        for (int i = 0; i < request.lines().size(); i++) {
            LineCreate line = request.lines().get(i);
            if (line == null || line.quantity() == null || line.quantity().compareTo(BigDecimal.ZERO) <= 0)
                throw new BusinessException(400, "INVALID_LINE", "Line quantity must be positive");
            jdbc.update("""
                    INSERT INTO company.sales_order_line
                        (sales_order_id, position, item_code, description, quantity, unit)
                    VALUES (?, ?, ?, ?, ?, ?)
                    """, id, i + 1,
                    line.itemCode() == null || line.itemCode().isBlank() ? null : line.itemCode().trim(),
                    line.description().trim(), line.quantity(), line.unit().toUpperCase(Locale.ROOT));
        }
        return get(id);
    }

    @Transactional
    public Summary changeStatus(UUID id, Status requested, UUID actor) {
        List<String> statuses = jdbc.query("""
                SELECT status FROM company.sales_order WHERE id=? AND deleted_at IS NULL FOR UPDATE
                """, (rs, index) -> rs.getString(1), id);
        if (statuses.isEmpty()) throw new BusinessException(404, "ORDER_NOT_FOUND", "Order not found");
        Status current = Status.valueOf(statuses.get(0));
        if (!allowedTransition(current, requested))
            throw new BusinessException(409, "INVALID_STATUS_TRANSITION", "Invalid order status transition");
        jdbc.update("""
                UPDATE company.sales_order SET status=?, updated_by=?, updated_at=now()
                WHERE id=? AND deleted_at IS NULL
                """, requested.name(), actor, id);
        return get(id);
    }
}
