-- EngineVersion definition

CREATE TABLE IF NOT EXISTS core.engine_version
(
    module        TEXT    NOT NULL,
    version       TEXT    NOT NULL DEFAULT '0.0.1',
    release_date  TEXT    NOT NULL DEFAULT (CURRENT_TIMESTAMP),
    deployed_date TEXT,
    is_stable     BOOLEAN NOT NULL,
    description   TEXT,

    PRIMARY KEY (module, version)
);

CREATE TABLE IF NOT EXISTS core.feature_definition
(
    id         uuid PRIMARY KEY     DEFAULT gen_random_uuid(),

    name       TEXT        NOT NULL UNIQUE,
    unit       TEXT        NOT NULL,
    data_type  TEXT        NOT NULL,

    created_by TEXT                 DEFAULT current_setting('app.current_user', true),
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at TIMESTAMPTZ,
    updated_by TEXT                 DEFAULT current_setting('app.current_user', true),
    deleted_at TIMESTAMPTZ
);

CREATE TABLE IF NOT EXISTS core.entity_feature
(
    id          uuid PRIMARY KEY          DEFAULT gen_random_uuid(),

    entity_type core.entity_type NOT NULL,
    feature_id  uuid             NOT NULL,
    role        core.role_type   NOT NULL,

    created_by  TEXT                      DEFAULT current_setting('app.current_user', true),
    created_at  TIMESTAMPTZ      NOT NULL DEFAULT now(),
    updated_at  TIMESTAMPTZ,
    updated_by  TEXT                      DEFAULT current_setting('app.current_user', true),
    deleted_at  TIMESTAMPTZ,
    FOREIGN KEY (feature_id) REFERENCES core.feature_definition (id),
    UNIQUE (feature_id, entity_type, role)

);