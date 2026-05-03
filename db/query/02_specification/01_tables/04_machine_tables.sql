CREATE TABLE IF NOT EXISTS specification.machine_type
(
    id          uuid                 DEFAULT gen_random_uuid(),

    type        TEXT        NOT NULL UNIQUE,
    description TEXT,

    created_by  TEXT                 DEFAULT current_setting('app.current_user', true),
    created_at  TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at  TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_by  TEXT                 DEFAULT current_setting('app.current_user', true),
    deleted_at  TIMESTAMPTZ,

    constraint mt_pkey primary key (id)
);


CREATE TABLE IF NOT EXISTS specification.machine_master
(
    id                uuid                 DEFAULT gen_random_uuid(),

    manufacturer_code TEXT        NOT NULL,
    type_id           uuid        NOT NULL,
    manufacturer      TEXT        NOT NULL DEFAULT 'unknown',
    description       TEXT,

    link              TEXT,
    catalog           TEXT,
    order_code        TEXT,

    created_by        TEXT                 DEFAULT current_setting('app.current_user', true),
    created_at        TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at        TIMESTAMPTZ,
    updated_by        TEXT,
    deleted_at        TIMESTAMPTZ,

    constraint mm_pkey primary key (id),
    constraint mm_machine_type_id_fkey foreign key (type_id) references specification.machine_type (id)
);


CREATE TABLE IF NOT EXISTS specification.machine_tool_station_category
(
    id           uuid                               DEFAULT gen_random_uuid(),

    type_id      uuid                      NOT NULL,
    station_type core.machine_station_type NOT NULL,
    name         TEXT                      NOT NULL,
    is_interface BOOLEAN                   NOT NULL DEFAULT false,
    is_holder    BOOLEAN                   NOT NULL DEFAULT false,

    created_by   TEXT                               DEFAULT current_setting('app.current_user', true),
    created_at   TIMESTAMPTZ               NOT NULL DEFAULT now(),
    updated_at   TIMESTAMPTZ,
    updated_by   TEXT,
    deleted_at   TIMESTAMPTZ,

    constraint mtsc_pkey primary key (id),
    constraint mtsc_machine_type_id_fkey foreign key (type_id) references machine_type (id),
    constraint mtsc_type_id_station_type_key UNIQUE (type_id, station_type)
);



CREATE TABLE IF NOT EXISTS specification.machine_station_feature_requirement
(
    id                     uuid                 DEFAULT gen_random_uuid(),

    requirement_feature_id uuid        NOT NULL,
    tool_station_id        uuid        NOT NULL,

    created_by             TEXT                 DEFAULT current_setting('app.current_user', true),
    created_at             TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at             TIMESTAMPTZ,
    updated_by             TEXT,
    deleted_at             TEXT,

    constraint msfr_pkey primary key (id),
    constraint msfr_requirement_feature_id_fkey foreign key (requirement_feature_id) references core.entity_feature (id),
    constraint msfr_tool_station_id_fkey foreign key (tool_station_id) references specification.machine_tool_station_category (id)
);
