-- ToolType definition

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

-- ToolMaster definition

CREATE TABLE IF NOT EXISTS specification.tool_master
(
    id           uuid PRIMARY KEY     DEFAULT gen_random_uuid(),
    code    TEXT        NOT NULL,
    type_id uuid        NOT NULL,
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

CREATE TABLE IF NOT EXISTS specification.entity_feature_value(
    id uuid PRIMARY KEY DEFAULT gen_random_uuid(),

    entity_id uuid NOT NULL ,
    entity_feature_id uuid NOT NULL,

    value_number NUMERIC,
    value_text   TEXT,

    created_by   TEXT                 DEFAULT current_setting('app.current_user', true),
    created_at   TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at   TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_by   TEXT                 DEFAULT current_setting('app.current_user', true),
    deleted_at   TIMESTAMPTZ,
    FOREIGN KEY (entity_feature_id) REFERENCES core.entity_feature (id),

    CHECK ( value_text IS NOT NULL
        OR  value_number IS NOT NULL),

    UNIQUE (entity_id,entity_feature_id)
);


-- InterfaceType definition

CREATE TABLE IF NOT EXISTS specification.interface_type
(
    id          uuid PRIMARY KEY     DEFAULT gen_random_uuid(),
    type        TEXT        NOT NULL UNIQUE,
    description TEXT,

    created_by  TEXT                 DEFAULT current_setting('app.current_user', true),
    created_at  TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at  TIMESTAMPTZ,
    updated_by  TEXT        NOT NULL DEFAULT current_setting('app.current_user', true),
    deleted_at  TIMESTAMPTZ
);

-- InterfaceMaster definition

CREATE TABLE IF NOT EXISTS specification.interface_master
(
    id                uuid PRIMARY KEY     DEFAULT gen_random_uuid(),
    code    TEXT        NOT NULL,
    type_id uuid        NOT NULL,
    manufacturer      TEXT        NOT NULL DEFAULT 'unknown',
    description       TEXT,

    created_by        TEXT                 DEFAULT current_setting('app.current_user', true),
    created_at        TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at        TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_by        TEXT                 DEFAULT current_setting('app.current_user', true),
    deleted_at        TIMESTAMPTZ,
    FOREIGN KEY (type_id) REFERENCES specification.interface_type (id),
    UNIQUE (type_id, code)
);


-- HolderType definition

CREATE TABLE IF NOT EXISTS specification.holder_type
(
    id           uuid PRIMARY KEY     DEFAULT gen_random_uuid(),
    type         TEXT        NOT NULL UNIQUE,
    is_interface BOOLEAN     NOT NULL default true,
    description  TEXT,
    created_by   TEXT                 DEFAULT current_setting('app.current_user', true),
    created_at   TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at   TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_by   TEXT                 DEFAULT current_setting('app.current_user', true),
    deleted_at   TIMESTAMPTZ
);

-- HolderMaster definition

CREATE TABLE IF NOT EXISTS specification.holder_master
(
    id             uuid PRIMARY KEY     DEFAULT gen_random_uuid(),
    code    TEXT        NOT NULL,
    type_id uuid        NOT NULL,
    manufacturer   TEXT        NOT NULL DEFAULT 'unknown',
    description    TEXT,

    created_by     TEXT                 DEFAULT current_setting('app.current_user', true),
    created_at     TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at     TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_by     TEXT                 DEFAULT current_setting('app.current_user', true),
    deleted_at     TIMESTAMPTZ,
    FOREIGN KEY (type_id) REFERENCES specification.holder_type (id),
    UNIQUE (type_id, code)

);

-- MachineType definition

CREATE TABLE IF NOT EXISTS specification.machine_type
(
    id          uuid PRIMARY KEY     DEFAULT gen_random_uuid(),
    type        TEXT        NOT NULL UNIQUE,
    description TEXT,

    created_by  TEXT                 DEFAULT current_setting('app.current_user', true),
    created_at  TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at  TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_by  TEXT                 DEFAULT current_setting('app.current_user', true),
    deleted_at  TIMESTAMPTZ
);

-- MachineMaster definition

CREATE TABLE IF NOT EXISTS specification.machine_master
(
    id              uuid PRIMARY KEY     DEFAULT gen_random_uuid(),
    code    TEXT        NOT NULL,
    type_id uuid        NOT NULL,
    manufacturer    TEXT        NOT NULL DEFAULT 'unknown',
    description     TEXT,

    created_by      TEXT                 DEFAULT current_setting('app.current_user', true),
    created_at      TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at      TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_by      TEXT                 DEFAULT current_setting('app.current_user', true),
    deleted_at      TIMESTAMPTZ,
    FOREIGN KEY (type_id) REFERENCES specification.machine_type (id),
    UNIQUE (type_id, code)
);


CREATE TABLE IF NOT EXISTS specification.machine_tool_station_category
(
    id              uuid PRIMARY KEY     DEFAULT gen_random_uuid(),
    type_id uuid        NOT NULL,
    station_code    TEXT        NOT NULL,
    name    TEXT        NOT NULL,
    is_interface    BOOLEAN     NOT NULL DEFAULT false,
    is_holder       BOOLEAN     NOT NULL DEFAULT false,

    created_by      TEXT                 DEFAULT current_setting('app.current_user', true),
    created_at      TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at      TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_by      TEXT                 DEFAULT current_setting('app.current_user', true),
    deleted_at      TIMESTAMPTZ,
    UNIQUE (type_id, station_code)
);

CREATE TABLE IF NOT EXISTS specification.machine_station_feature_requirement
(
    id                     uuid PRIMARY KEY     DEFAULT gen_random_uuid(),
    machine_type_id        uuid     NOT NULL,
    requirement_feature_id uuid     NOT NULL,
    tool_station_id         uuid    NOT NULL ,
    created_by             TEXT                 DEFAULT current_setting('app.current_user', true),
    created_at             TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at             TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_by             TEXT                 DEFAULT current_setting('app.current_user', true),
    deleted_at             TEXT,
    FOREIGN KEY (machine_type_id) REFERENCES specification.machine_type (id),
    FOREIGN KEY (requirement_feature_id) REFERENCES core.entity_feature (id),
    FOREIGN KEY (tool_station_id) REFERENCES specification.machine_tool_station_category (id),
    UNIQUE (machine_type_id, requirement_feature_id)
);


