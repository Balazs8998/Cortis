package com.cortis.agent.chat;

import com.cortis.agent.chat.AgentChatDtos.*;
import com.cortis.agent.security.AgentConversationAuthorizationService;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Repository;
import org.springframework.web.server.ResponseStatusException;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

/** Uses the existing V16 agent schema. Ownership is required before every read/write. */
@Repository
public class AgentChatRepository {
    private final JdbcTemplate jdbc;
    private final AgentConversationAuthorizationService ownership;

    public AgentChatRepository(JdbcTemplate jdbc, AgentConversationAuthorizationService ownership) {
        this.jdbc = jdbc;
        this.ownership = ownership;
    }

    public List<Conversation> conversations(UUID userId) {
        return jdbc.query("""
              SELECT id,title,updated_at FROM agent.conversation
              WHERE owner_user_id=? AND deleted_at IS NULL
              ORDER BY updated_at DESC LIMIT 40
              """, (rs, index) -> new Conversation(rs.getObject("id", UUID.class),
                  rs.getString("title"), rs.getTimestamp("updated_at").toInstant()), userId);
    }

    public Conversation create(UUID userId) {
        UUID id = jdbc.queryForObject("""
             INSERT INTO agent.conversation (owner_user_id) VALUES (?) RETURNING id
             """, UUID.class, userId);
        return new Conversation(id, "Új beszélgetés", Instant.now());
    }

    public List<Message> messages(UUID id, Authentication user) {
        ownership.requireOwner(id, user);
        return jdbc.query("""
             SELECT role,content,created_at FROM (
               SELECT role,content,created_at,sequence_no FROM agent.conversation_message
               WHERE conversation_id=? AND role IN ('USER','ASSISTANT') AND content IS NOT NULL
               ORDER BY sequence_no DESC LIMIT 100
             ) latest ORDER BY sequence_no ASC
             """, (rs, index) -> new Message(rs.getString("role").toLowerCase(java.util.Locale.ROOT),
                     rs.getString("content"), rs.getTimestamp("created_at").toInstant()), id);
    }

    public void verify(UUID id, Authentication user) { ownership.requireOwner(id, user); }

    public UUID beginRun(UUID id, UUID userId, String model, int limit, int iterations) {
        // A crashed worker must not indefinitely block its conversation.
        jdbc.update("""
          UPDATE agent.execution_run SET status='TIMED_OUT', termination_reason='STALE_RUN',
          finished_at=now(), duration_ms=GREATEST(0, (extract(epoch FROM (now()-started_at))*1000)::bigint)
          WHERE conversation_id=? AND status='RUNNING' AND started_at < now() - interval '15 minutes'
          """, id);
        try {
            return jdbc.queryForObject("""
                INSERT INTO agent.execution_run (conversation_id,requested_by,model_name,tool_limit,iteration_limit)
                VALUES (?,?,?,?,?) RETURNING id
                """, UUID.class, id, userId, model, limit, iterations);
        } catch (DataIntegrityViolationException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "A beszélgetésben már fut egy agent-válasz.");
        }
    }

    public void append(UUID id, String role, String content, String toolName, String toolCallsJson) {
        // Exactly one active run per conversation; agent writes are sequential in that run.
        jdbc.update("""
            INSERT INTO agent.conversation_message
                (conversation_id,sequence_no,role,content,tool_name,tool_calls)
            VALUES (?,(SELECT COALESCE(MAX(sequence_no),0)+1 FROM agent.conversation_message WHERE conversation_id=?),
                    ?,?,?,?::jsonb)
            """, id, id, role, content, toolName, toolCallsJson);
        jdbc.update("UPDATE agent.conversation SET updated_at=now(),version=version+1 WHERE id=?", id);
    }

    /** Model context includes past native assistant tool calls, not just their display text. */
    public List<StoredMessage> modelHistory(UUID id) {
        return jdbc.query("""
            SELECT role,content,tool_name,tool_calls::text AS tool_calls FROM (
              SELECT role,content,tool_name,tool_calls,sequence_no
              FROM agent.conversation_message WHERE conversation_id=?
              ORDER BY sequence_no DESC LIMIT 35
            ) history ORDER BY sequence_no ASC
            """, (rs, n) -> new StoredMessage(rs.getString("role").toLowerCase(java.util.Locale.ROOT),
                  rs.getString("content"), rs.getString("tool_name"), rs.getString("tool_calls")), id);
    }

    public record StoredMessage(String role, String content, String toolName, String toolCallsJson) {}

    public void titleOnce(UUID id, String firstMessage) {
        String clipped = firstMessage.strip();
        if (clipped.length() > 80) clipped = clipped.substring(0, 80).strip() + "…";
        jdbc.update("UPDATE agent.conversation SET title=? WHERE id=? AND title='Új beszélgetés'", clipped, id);
    }

    public void logTool(UUID runId, int order, String name, String argsJson, String resultJson,
                        String status, String errorCode, long durationMs) {
        jdbc.update("""
            INSERT INTO agent.tool_execution
                (run_id, call_order, tool_name, arguments, result, status, error_code,
                 started_at,finished_at,duration_ms)
            VALUES (?,?,?,?::jsonb,?::jsonb,?,?,now(),now(),?)
            """, runId, order, name, argsJson, resultJson, status, errorCode, Math.max(0L, durationMs));
    }

    public void finish(UUID runId, String status, String reason, int toolCalls, int iterations,
                       long durationMs, String errorCode) {
        jdbc.update("""
          UPDATE agent.execution_run SET status=?,termination_reason=?,tool_calls_used=?,
              iterations_used=?,finished_at=now(),duration_ms=?,error_code=? WHERE id=?
          """, status, reason, toolCalls, iterations, Math.max(0L, durationMs), errorCode, runId);
    }
}
