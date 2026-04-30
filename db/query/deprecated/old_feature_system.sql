
CREATE TABLE IF NOT EXISTS specification.tool_feature_definition
(
    id                           uuid PRIMARY KEY     DEFAULT gen_random_uuid(),
    feature_definition_name TEXT        NOT NULL,
    scope                   TEXT        NOT NULL,
    value_type              TEXT        NOT NULL,
    unit_default            TEXT        NOT NULL,
    category                     TEXT,
    description                  TEXT,
    created_by                   TEXT                 DEFAULT current_setting('app.current_user', true),
    created_at                   TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at                   TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_by                   TEXT                 DEFAULT current_setting('app.current_user', true),
    deleted_at                   TEXT
    );





CREATE TABLE IF NOT EXISTS specification.interface_feature_definition
(
    id                                uuid PRIMARY KEY     DEFAULT gen_random_uuid(),
    feature_definition_name TEXT        NOT NULL,
    scope                   TEXT        NOT NULL,
    value_type              TEXT        NOT NULL,
    unit_default            TEXT        NOT NULL,
    category                          TEXT,
    description                       TEXT,
    created_by                        TEXT                 DEFAULT current_setting('app.current_user', true),
    created_at                        TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at                        TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_by                        TEXT                 DEFAULT current_setting('app.current_user', true),
    deleted_at                        TEXT
    );





CREATE TABLE IF NOT EXISTS specification.holder_feature_definition
(
    id                             uuid PRIMARY KEY     DEFAULT gen_random_uuid(),
    feature_definition_name TEXT        NOT NULL,
    scope                   TEXT        NOT NULL,
    value_type              TEXT        NOT NULL,
    unit_default            TEXT        NOT NULL,
    category                       TEXT,
    description                    TEXT,
    created_by                     TEXT                 DEFAULT current_setting('app.current_user', true),
    created_at                     TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at                     TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_by                     TEXT                 DEFAULT current_setting('app.current_user', true),
    deleted_at                     TEXT
);





CREATE TABLE IF NOT EXISTS specification.machine_feature_definition
(
    id                              uuid PRIMARY KEY     DEFAULT gen_random_uuid(),
    feature_definition_name TEXT        NOT NULL,
    scope                   TEXT        NOT NULL,
    value_type              TEXT        NOT NULL,
    unit_default            TEXT        NOT NULL,
    category                        TEXT,
    description                     TEXT,
    created_by                      TEXT                 DEFAULT current_setting('app.current_user', true),
    created_at                      TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at                      TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_by                      TEXT                 DEFAULT current_setting('app.current_user', true),
    deleted_at                      TEXT
);

