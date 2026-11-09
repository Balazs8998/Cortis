package com.cortis.business.service;

import com.cortis.business.model.BusinessException;
import com.cortis.business.model.BusinessDtos.*;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.jdbc.core.RowMapper;
import java.util.List;
import java.util.UUID;

/**
 * Engineering-safe V1 behavior: an explicit, sourced, human-reviewed decision
 * is the only way to return COMPATIBLE or INCOMPATIBLE. No dimensional guesses.
 * Future V2 rule engine can be added without corrupting reviewed decisions.
 */
@Service
public class CompatibilityService {
    private final JdbcTemplate jdbc;
    public CompatibilityService(JdbcTemplate jdbc) { this.jdbc = jdbc; }

    private void requireActiveMasters(UUID toolId, UUID holderId) {
        Integer tools = jdbc.queryForObject("SELECT count(*) FROM specification.tool_master " +
                "WHERE id=? AND deleted_at IS NULL", Integer.class, toolId);
        Integer holders = jdbc.queryForObject("SELECT count(*) FROM specification.holder_master " +
                "WHERE id=? AND deleted_at IS NULL", Integer.class, holderId);
        if (tools == null || tools == 0 || holders == null || holders == 0)
            throw new BusinessException(404, "MASTER_NOT_FOUND", "Tool or holder master not found");
    }
    private RowMapper<CompatibilityView> reviewMapper(UUID toolId, UUID holderId) {
        return (rs, row) -> new CompatibilityView(toolId, holderId, rs.getString("verdict"),
                rs.getObject("id", UUID.class), rs.getString("evidence"), rs.getString("source_reference"),
                rs.getObject("reviewed_by", UUID.class), rs.getTimestamp("reviewed_at").toInstant());
    }
    @Transactional(readOnly = true)
    public CompatibilityView check(UUID toolId, UUID holderId) {
        requireActiveMasters(toolId, holderId);
        List<CompatibilityView> reviewed = jdbc.query("""
                SELECT id, verdict, evidence, source_reference, reviewed_by, reviewed_at
                FROM specification.tool_holder_compatibility_decision
                WHERE tool_master_id=? AND holder_master_id=?
                ORDER BY review_number DESC LIMIT 1
                """, reviewMapper(toolId, holderId), toolId, holderId);
        return reviewed.isEmpty() ? new CompatibilityView(toolId, holderId, "UNKNOWN", null,
                "No verified engineering decision exists for this exact tool/holder pair.",
                null, null, null) : reviewed.get(0);
    }
    @Transactional
    public CompatibilityView review(CompatibilityReview review, UUID actorId) {
        requireActiveMasters(review.toolId(), review.holderId());
        UUID id = jdbc.queryForObject("""
                INSERT INTO specification.tool_holder_compatibility_decision
                (tool_master_id, holder_master_id, verdict, evidence, source_reference, reviewed_by)
                VALUES (?, ?, ?, ?, ?, ?) RETURNING id
                """, UUID.class, review.toolId(), review.holderId(), review.verdict().name(),
                review.evidence().trim(), review.reference().trim(), actorId);
        return jdbc.queryForObject("""
                SELECT id, verdict, evidence, source_reference, reviewed_by, reviewed_at
                FROM specification.tool_holder_compatibility_decision WHERE id=?
                """, reviewMapper(review.toolId(), review.holderId()), id);
    }
}
