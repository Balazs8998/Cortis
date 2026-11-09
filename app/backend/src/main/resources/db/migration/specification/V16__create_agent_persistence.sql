-- CORTIS V1: durable agent state and audit foundations.
-- Append after V15; do not edit an already-applied Flyway migration.
-- Requires personal.users(id uuid) from V10 and PostgreSQL gen_random_uuid().

CREATE SCHEMA IF NOT EXISTS agent;

CREATE TABLE agent.conversation (
    id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
    owner_user_id uuid NOT NULL REFERENCES personal.users(id) ON DELETE RESTRICT,
    title text NOT NULL DEFAULT 'Új beszélgetés',
    status text NOT NULL DEFAULT 'ACTIVE',
    created_at timestamptz NOT NULL DEFAULT now(),
    updated_at timestamptz NOT NULL DEFAULT now(),
    deleted_at timestamptz,
    version bigint NOT NULL DEFAULT 0,
    CONSTRAINT agent_conversation_status_ck CHECK (status IN ('ACTIVE','ARCHIVED')),
    CONSTRAINT agent_conversation_title_ck CHECK (char_length(trim(title)) BETWEEN 1 AND 200),
    CONSTRAINT agent_conversation_version_ck CHECK (version >= 0)
);
CREATE INDEX idx_agent_conversation_owner_updated
    ON agent.conversation(owner_user_id, updated_at DESC) WHERE deleted_at IS NULL;

CREATE TABLE agent.conversation_message (
    id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
    conversation_id uuid NOT NULL REFERENCES agent.conversation(id) ON DELETE CASCADE,
    sequence_no bigint NOT NULL,
    role text NOT NULL,
    content text,
    tool_name text,
    tool_calls jsonb,
    tool_call_id text,
    created_at timestamptz NOT NULL DEFAULT now(),
    CONSTRAINT agent_message_order_uq UNIQUE(conversation_id, sequence_no),
    CONSTRAINT agent_message_sequence_ck CHECK (sequence_no > 0),
    CONSTRAINT agent_message_role_ck CHECK (role IN ('SYSTEM','USER','ASSISTANT','TOOL')),
    CONSTRAINT agent_message_tool_calls_ck CHECK (tool_calls IS NULL OR jsonb_typeof(tool_calls) = 'array')
);
CREATE INDEX idx_agent_messages_chronological
    ON agent.conversation_message(conversation_id, sequence_no);

CREATE TABLE agent.execution_run (
    id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
    conversation_id uuid NOT NULL REFERENCES agent.conversation(id) ON DELETE CASCADE,
    requested_by uuid NOT NULL REFERENCES personal.users(id) ON DELETE RESTRICT,
    model_name text NOT NULL,
    status text NOT NULL DEFAULT 'RUNNING',
    termination_reason text,
    tool_limit integer NOT NULL,
    iteration_limit integer NOT NULL,
    tool_calls_used integer NOT NULL DEFAULT 0,
    iterations_used integer NOT NULL DEFAULT 0,
    started_at timestamptz NOT NULL DEFAULT now(),
    finished_at timestamptz,
    duration_ms bigint,
    error_code text,
    CONSTRAINT agent_run_status_ck CHECK(status IN
        ('RUNNING','WAITING_APPROVAL','COMPLETED','PARTIAL','FAILED','CANCELLED','TIMED_OUT')),
    CONSTRAINT agent_run_counts_ck CHECK (
        tool_limit >= 0 AND iteration_limit >= 1 AND
        tool_calls_used >= 0 AND tool_calls_used <= tool_limit AND
        iterations_used >= 0 AND iterations_used <= iteration_limit),
    CONSTRAINT agent_run_duration_ck CHECK (duration_ms IS NULL OR duration_ms >= 0),
    CONSTRAINT agent_run_time_ck CHECK (finished_at IS NULL OR finished_at >= started_at)
);
-- Exactly one ongoing agent run per conversation, including while awaiting approval.
CREATE UNIQUE INDEX idx_agent_run_one_active_per_conversation
    ON agent.execution_run(conversation_id)
    WHERE status IN ('RUNNING','WAITING_APPROVAL');
CREATE INDEX idx_agent_runs_conversation
    ON agent.execution_run(conversation_id, started_at DESC);

CREATE TABLE agent.tool_execution (
    id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
    run_id uuid NOT NULL REFERENCES agent.execution_run(id) ON DELETE CASCADE,
    call_order integer NOT NULL,
    tool_name text NOT NULL,
    arguments jsonb NOT NULL DEFAULT '{}'::jsonb,
    result jsonb,
    status text NOT NULL,
    error_code text,
    safe_error_message text,
    started_at timestamptz,
    finished_at timestamptz,
    duration_ms bigint,
    CONSTRAINT agent_tool_call_order_uq UNIQUE(run_id, call_order),
    CONSTRAINT agent_tool_order_ck CHECK (call_order > 0),
    CONSTRAINT agent_tool_arguments_ck CHECK (jsonb_typeof(arguments) = 'object'),
    CONSTRAINT agent_tool_status_ck CHECK (status IN
        ('PENDING','RUNNING','SUCCESS','FAILED','REJECTED','SKIPPED','UNKNOWN','WAITING_APPROVAL')),
    CONSTRAINT agent_tool_duration_ck CHECK (duration_ms IS NULL OR duration_ms >= 0),
    CONSTRAINT agent_tool_time_ck CHECK (finished_at IS NULL OR started_at IS NULL OR finished_at >= started_at)
);
CREATE INDEX idx_agent_tools_by_run ON agent.tool_execution(run_id, call_order);

CREATE TABLE agent.tool_approval (
    id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
    tool_execution_id uuid NOT NULL UNIQUE REFERENCES agent.tool_execution(id) ON DELETE CASCADE,
    requested_by uuid NOT NULL REFERENCES personal.users(id) ON DELETE RESTRICT,
    decided_by uuid REFERENCES personal.users(id) ON DELETE RESTRICT,
    -- Canonical server-side operation snapshot and its SHA-256 digest.
    operation_snapshot jsonb NOT NULL,
    operation_sha256 char(64) NOT NULL,
    status text NOT NULL DEFAULT 'PENDING',
    requested_at timestamptz NOT NULL DEFAULT now(),
    expires_at timestamptz NOT NULL,
    decided_at timestamptz,
    CONSTRAINT agent_approval_snapshot_ck CHECK (jsonb_typeof(operation_snapshot) = 'object'),
    CONSTRAINT agent_approval_hash_ck CHECK (operation_sha256 ~ '^[0-9a-f]{64}$'),
    CONSTRAINT agent_approval_status_ck CHECK (status IN ('PENDING','APPROVED','REJECTED','EXPIRED','CANCELLED')),
    CONSTRAINT agent_approval_expiry_ck CHECK (expires_at > requested_at),
    CONSTRAINT agent_approval_decision_ck CHECK (
        (status = 'PENDING' AND decided_at IS NULL AND decided_by IS NULL) OR
        (status IN ('EXPIRED','CANCELLED') AND decided_at IS NOT NULL) OR
        (status IN ('APPROVED','REJECTED') AND decided_at IS NOT NULL AND decided_by IS NOT NULL))
);
CREATE INDEX idx_agent_approvals_pending
    ON agent.tool_approval(requested_by, expires_at) WHERE status = 'PENDING';

-- Idempotency keys must be reused on retries of the same mutating request.
CREATE TABLE agent.write_operation (
    id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
    approval_id uuid NOT NULL UNIQUE REFERENCES agent.tool_approval(id) ON DELETE RESTRICT,
    idempotency_key uuid NOT NULL UNIQUE,
    operation_sha256 char(64) NOT NULL,
    status text NOT NULL DEFAULT 'PENDING',
    result jsonb,
    created_at timestamptz NOT NULL DEFAULT now(),
    executed_at timestamptz,
    CONSTRAINT agent_write_hash_ck CHECK (operation_sha256 ~ '^[0-9a-f]{64}$'),
    CONSTRAINT agent_write_status_ck CHECK (status IN ('PENDING','EXECUTING','SUCCEEDED','FAILED','UNKNOWN'))
);

-- Events are append-only by application policy; DB role privileges must enforce it in production.
CREATE TABLE agent.execution_event (
    id bigint GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    run_id uuid NOT NULL REFERENCES agent.execution_run(id) ON DELETE CASCADE,
    event_type text NOT NULL,
    payload jsonb NOT NULL DEFAULT '{}'::jsonb,
    occurred_at timestamptz NOT NULL DEFAULT now(),
    CONSTRAINT agent_event_payload_ck CHECK (jsonb_typeof(payload) = 'object')
);
CREATE INDEX idx_agent_events_run_order
    ON agent.execution_event(run_id, id);

COMMENT ON TABLE agent.conversation IS 'Persistent conversations, independent of login sessions.';
COMMENT ON TABLE agent.write_operation IS 'Server-controlled idempotency and uncertain-outcome tracking for approved writes.';
COMMENT ON COLUMN agent.tool_approval.operation_snapshot IS 'Immutable-by-service canonical operation snapshot; never trust re-supplied model arguments after approval.';
