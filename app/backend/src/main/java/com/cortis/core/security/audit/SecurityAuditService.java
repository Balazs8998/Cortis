package com.cortis.core.security.audit;

import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.sql.Types;
import java.util.UUID;

/** Metadata only: never log passwords, chip codes, cookies, tokens or tool arguments. */
@Service
public class SecurityAuditService {
    private static final Logger log = LoggerFactory.getLogger(SecurityAuditService.class);
    private final JdbcTemplate jdbc;
    public SecurityAuditService(JdbcTemplate jdbc) { this.jdbc = jdbc; }

    public void record(String action, String outcome, UUID actor, HttpServletRequest request) {
        // Do not accept these values from the caller/browser. RemoteAddr is proxy-dependent.
        String ip = request.getRemoteAddr();
        if (ip != null && ip.length() > 80) ip = ip.substring(0, 80);
        String path = request.getRequestURI();
        if (path != null && path.length() > 500) path = path.substring(0, 500);
        try {
            jdbc.update("""
                INSERT INTO core.security_audit_event(actor_user_id, event_type, outcome, request_path, remote_address)
                VALUES (?, ?, ?, ?, ?)
                """, new Object[] { actor, action, outcome, path, ip },
                    new int[] { Types.OTHER, Types.VARCHAR, Types.VARCHAR, Types.VARCHAR, Types.VARCHAR });
        } catch (DataAccessException e) {
            // Failures are surfaced to operators; cannot block logout or leak credential data.
            log.error("Failed to persist security audit metadata: type={}, outcome={}", action, outcome);
        }
    }
}
