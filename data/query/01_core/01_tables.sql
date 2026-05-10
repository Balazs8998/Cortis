-- EngineVersion definition

CREATE TABLE IF NOT EXISTS core.engine_version
(
    module        TEXT        NOT NULL,
    version       TEXT        NOT NULL DEFAULT '0.0.1',
    release_date  TIMESTAMPTZ NOT NULL DEFAULT now(),
    deployed_date TIMESTAMPTZ,
    is_stable     BOOLEAN     NOT NULL,
    description   TEXT,

    CONSTRAINT ev_pkey PRIMARY KEY (module, version)
);

CREATE TABLE IF NOT EXISTS core.feature_definition
(
    id         uuid                 DEFAULT gen_random_uuid(),

    name       TEXT        NOT NULL UNIQUE,
    unit       TEXT        NOT NULL,
    data_type  TEXT        NOT NULL,

    created_by TEXT                 DEFAULT current_setting('app.current_user', true),
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at TIMESTAMPTZ,
    updated_by TEXT,
    deleted_at TIMESTAMPTZ,

    CONSTRAINT fd_pkey primary key (id)
);

CREATE TABLE IF NOT EXISTS core.entity_feature
(
    id          uuid                      DEFAULT gen_random_uuid(),

    entity_type core.entity_type NOT NULL,
    feature_id  uuid             NOT NULL,
    role        core.role_type   NOT NULL,

    created_by  TEXT                      DEFAULT current_setting('app.current_user', true),
    created_at  TIMESTAMPTZ      NOT NULL DEFAULT now(),
    updated_at  TIMESTAMPTZ,
    updated_by  TEXT,
    FOREIGN KEY (feature_id) REFERENCES core.feature_definition (id),

    constraint ef_pkey primary key (id),
    constraint ef_feature_id_entity_type_role_key unique (feature_id, entity_type, role),
    constraint ef_feature_definition_id_fkey foreign key (feature_id) references core.feature_definition (id)


);