
CREATE TABLE IF NOT EXISTS specification.tool_type
(
    id          uuid PRIMARY KEY     DEFAULT gen_random_uuid(),
    "type"      TEXT        NOT NULL,
    description TEXT,

    created_by  TEXT                 DEFAULT current_setting('app.current_user', true),
    created_at  TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at  TIMESTAMPTZ,
    updated_by  TEXT        NOT NULL DEFAULT current_setting('app.current_user', true),
    deleted_at  TIMESTAMPTZ
);


CREATE TABLE IF NOT EXISTS specification.tool_master
(
    id           uuid PRIMARY KEY     DEFAULT gen_random_uuid(),
    code         TEXT        NOT NULL,
    type_id      uuid        NOT NULL,
    substrate    TEXT        NOT NULL,
    manufacturer TEXT        NOT NULL DEFAULT 'unknown',
    description  TEXT,

    created_by   TEXT                 DEFAULT current_setting('app.current_user', true),
    created_at   TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at   TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_by   TEXT                 DEFAULT current_setting('app.current_user', true),
    deleted_at   TIMESTAMPTZ,
    FOREIGN KEY (type_id) REFERENCES specification.tool_type (id),
    UNIQUE (type_id, code)
);

CREATE TABLE IF NOT EXISTS specification.tool_type_mounting_feature_requirement
(
    id                     uuid PRIMARY KEY     DEFAULT gen_random_uuid(),
    type_id           uuid     NOT NULL,
    requirement_feature_id uuid     NOT NULL,
    is_required            INTEGER     NOT NULL,
    context                TEXT        NOT NULL,
    created_by             TEXT                 DEFAULT current_setting('app.current_user', true),
    created_at             TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at             TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_by             TEXT                 DEFAULT current_setting('app.current_user', true),
    deleted_at             TEXT,
    FOREIGN KEY (type_id) REFERENCES specification.tool_type (id),
    FOREIGN KEY (requirement_feature_id) REFERENCES core.feature_definition (id),
    UNIQUE (type_id, requirement_feature_id)
);