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
    id                uuid PRIMARY KEY     DEFAULT gen_random_uuid(),

    manufacturer_code TEXT        NOT NULL,
    type_id           uuid        NOT NULL,
    manufacturer      TEXT        NOT NULL DEFAULT 'unknown',
    description       TEXT,

    order_code        TEXT,
    link              TEXT,
    catalog           TEXT,

    created_by        TEXT                 DEFAULT current_setting('app.current_user', true),
    created_at        TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at        TIMESTAMPTZ,
    updated_by        TEXT,
    deleted_at        TIMESTAMPTZ,

    FOREIGN KEY (type_id) REFERENCES specification.tool_type (id)
);

create table if not exists specification.tool_type_mounting_option
(
    id                uuid primary key                         default gen_random_uuid(),

    type_id           uuid                            not null,
    option_name       text                            not null,
    requirement_basis core.mounting_requirement_basis not null,

    created_by        text                                     default current_setting('app.current_user', true),
    created_at        timestamptz                     not null default now(),
    updated_at        timestamptz,
    updated_by        text,
    deleted_at        timestamptz,

    foreign key (type_id) references specification.tool_type (id),
    unique (type_id, option_name)
);

CREATE TABLE IF NOT EXISTS specification.tool_type_mounting_feature_requirement
(
    id                     uuid PRIMARY KEY     DEFAULT gen_random_uuid(),

    type_id                uuid        NOT NULL,
    requirement_feature_id uuid        NOT NULL,

    created_by             TEXT                 DEFAULT current_setting('app.current_user', true),
    created_at             TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at             TIMESTAMPTZ,
    updated_by             TEXT,
    deleted_at             TEXT,

    FOREIGN KEY (type_id) REFERENCES specification.tool_type (id),
    FOREIGN KEY (requirement_feature_id) REFERENCES core.entity_feature (id),
    UNIQUE (type_id, requirement_feature_id)
);