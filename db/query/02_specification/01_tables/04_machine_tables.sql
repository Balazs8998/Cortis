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


CREATE TABLE IF NOT EXISTS specification.machine_master
(
    id                uuid PRIMARY KEY     DEFAULT gen_random_uuid(),

    manufacturer_code TEXT        NOT NULL,
    type_id           uuid        NOT NULL,
    manufacturer      TEXT        NOT NULL DEFAULT 'unknown',
    description       TEXT,

    link              TEXT,
    catalog           TEXT,
    order_code        TEXT,

    created_by        TEXT                 DEFAULT current_setting('app.current_user', true),
    created_at        TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at        TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_by        TEXT                 DEFAULT current_setting('app.current_user', true),
    deleted_at        TIMESTAMPTZ,

    FOREIGN KEY (type_id) REFERENCES specification.machine_type (id),
    UNIQUE (type_id, manufacturer_code)
);


CREATE TABLE IF NOT EXISTS specification.machine_tool_station_category
(
    id           uuid PRIMARY KEY                   DEFAULT gen_random_uuid(),

    type_id      uuid                      NOT NULL,
    station_type core.machine_station_type NOT NULL,
    name         TEXT                      NOT NULL,
    is_interface BOOLEAN                   NOT NULL DEFAULT false,
    is_holder    BOOLEAN                   NOT NULL DEFAULT false,

    created_by   TEXT                               DEFAULT current_setting('app.current_user', true),
    created_at   TIMESTAMPTZ               NOT NULL DEFAULT now(),
    updated_at   TIMESTAMPTZ               NOT NULL DEFAULT now(),
    updated_by   TEXT                               DEFAULT current_setting('app.current_user', true),
    deleted_at   TIMESTAMPTZ,
    UNIQUE (type_id, station_type)
);



CREATE TABLE IF NOT EXISTS specification.machine_station_feature_requirement
(
    id                     uuid PRIMARY KEY     DEFAULT gen_random_uuid(),

    machine_type_id        uuid        NOT NULL,
    requirement_feature_id uuid        NOT NULL,
    tool_station_id        uuid        NOT NULL,

    created_by             TEXT                 DEFAULT current_setting('app.current_user', true),
    created_at             TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at             TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_by             TEXT                 DEFAULT current_setting('app.current_user', true),
    deleted_at             TEXT,

    FOREIGN KEY (machine_type_id) REFERENCES specification.machine_type (id),
    FOREIGN KEY (requirement_feature_id) REFERENCES core.feature_definition (id),
    FOREIGN KEY (tool_station_id) REFERENCES specification.machine_tool_station_category (id),
    UNIQUE (machine_type_id, requirement_feature_id)
);
