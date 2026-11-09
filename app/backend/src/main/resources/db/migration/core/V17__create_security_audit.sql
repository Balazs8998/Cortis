-- CORTIS V1 Phase 2: minimal security audit, appended after already-applied V16.
CREATE TABLE core.security_audit_event (
    id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
    actor_user_id uuid REFERENCES personal.users(id) ON DELETE SET NULL,
    event_type text NOT NULL,
    outcome text NOT NULL,
    request_path text NOT NULL,
    remote_address text,
    occurred_at timestamptz NOT NULL DEFAULT now(),
    CONSTRAINT security_audit_type_ck CHECK (event_type IN
      ('LOGIN_PASSWORD','LOGIN_CHIP','LOGOUT','ACCESS_DENIED','SESSION_REVOKED')),
    CONSTRAINT security_audit_outcome_ck CHECK (outcome IN ('SUCCESS','REJECTED','FAILED'))
);
CREATE INDEX idx_security_audit_actor_time ON core.security_audit_event(actor_user_id, occurred_at DESC);
CREATE INDEX idx_security_audit_time ON core.security_audit_event(occurred_at DESC);
COMMENT ON TABLE core.security_audit_event IS 'Security metadata only: no passwords, chip identifiers, cookies or tokens.';
