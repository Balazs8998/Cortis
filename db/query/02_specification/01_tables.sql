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
    deleted_at  TEXT
);

-- ToolMaster definition

CREATE TABLE IF NOT EXISTS specification.tool_master
(
    id           uuid PRIMARY KEY     DEFAULT gen_random_uuid(),
    tool_code    TEXT        NOT NULL,
    tool_type_id uuid     NOT NULL,
    substrate    TEXT        NOT NULL,
    tool_keyword INTEGER     NOT NULL,
    manufacturer TEXT        NOT NULL DEFAULT 'unknown',
    description  TEXT,
    created_by   TEXT                 DEFAULT current_setting('app.current_user', true),
    created_at   TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at   TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_by   TEXT                 DEFAULT current_setting('app.current_user', true),
    deleted_at   TEXT,
    FOREIGN KEY (tool_type_id) REFERENCES specification.tool_type (id),
    UNIQUE (tool_type_id, tool_code)
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
    deleted_at  TEXT
);

-- InterfaceMaster definition

CREATE TABLE IF NOT EXISTS specification.interface_master
(
    id                uuid PRIMARY KEY     DEFAULT gen_random_uuid(),
    interface_code    TEXT        NOT NULL,
    interface_type_id uuid     NOT NULL,
    interface_keyword INTEGER     NOT NULL,
    manufacturer      TEXT        NOT NULL DEFAULT 'unknown',
    description       TEXT,
    created_by        TEXT                 DEFAULT current_setting('app.current_user', true),
    created_at        TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at        TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_by        TEXT                 DEFAULT current_setting('app.current_user', true),
    deleted_at        TEXT,
    FOREIGN KEY (interface_type_id) REFERENCES specification.interface_type (id),
    UNIQUE (interface_type_id, interface_code)
);

-- HolderType definition

CREATE TABLE IF NOT EXISTS specification.holder_type
(
    id           uuid PRIMARY KEY     DEFAULT gen_random_uuid(),
    type         TEXT        NOT NULL UNIQUE,
    is_interface INTEGER     NOT NULL CHECK (is_interface IN (0, 1)),
    description  TEXT,
    created_by   TEXT                 DEFAULT current_setting('app.current_user', true),
    created_at   TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at   TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_by   TEXT                 DEFAULT current_setting('app.current_user', true),
    deleted_at   TEXT
);

-- HolderMaster definition

CREATE TABLE IF NOT EXISTS specification.holder_master
(
    id             uuid PRIMARY KEY     DEFAULT gen_random_uuid(),
    holder_code    TEXT        NOT NULL,
    holder_type_id uuid     NOT NULL,
    holder_keyword INTEGER     NOT NULL,
    manufacturer   TEXT        NOT NULL DEFAULT 'unknown',
    description    TEXT,
    created_by     TEXT                 DEFAULT current_setting('app.current_user', true),
    created_at     TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at     TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_by     TEXT                 DEFAULT current_setting('app.current_user', true),
    deleted_at     TEXT,
    FOREIGN KEY (holder_type_id) REFERENCES specification.holder_type (id),
    UNIQUE (holder_type_id, holder_code)

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
    deleted_at  TEXT
);

-- MachineMaster definition

CREATE TABLE IF NOT EXISTS specification.machine_master
(
    id              uuid PRIMARY KEY     DEFAULT gen_random_uuid(),
    machine_code    TEXT        NOT NULL,
    machine_type_id uuid     NOT NULL,
    machine_keyword INTEGER     NOT NULL,
    manufacturer    TEXT        NOT NULL DEFAULT 'unknown',
    description     TEXT,
    created_by      TEXT                 DEFAULT current_setting('app.current_user', true),
    created_at      TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at      TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_by      TEXT                 DEFAULT current_setting('app.current_user', true),
    deleted_at      TEXT,
    FOREIGN KEY (machine_type_id) REFERENCES specification.machine_type (id),
    UNIQUE (machine_type_id, machine_code)
);

