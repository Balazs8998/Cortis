CREATE TABLE IF NOT EXISTS specification.tool_type
(
    id          uuid                 DEFAULT gen_random_uuid(),

    name        TEXT        NOT NULL,
    description TEXT,

    created_by  TEXT                 DEFAULT current_setting('app.current_user', true),
    created_at  TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at  TIMESTAMPTZ,
    updated_by  TEXT,
    deleted_at  TIMESTAMPTZ,

    CONSTRAINT tt_name_key UNIQUE (name),

    constraint tt_pkey primary key (id)
);


create table if not exists specification.tool_type_mounting_option
(
    id                uuid                                    default gen_random_uuid(),

    type_id           uuid                           not null,
    option_name       text                           not null,
    requirement_basis core.tool_mounting_requirement not null,

    created_by        text                                    default current_setting('app.current_user', true),
    created_at        timestamptz                    not null default now(),
    updated_at        timestamptz,
    updated_by        text,
    deleted_at        timestamptz,

    constraint ttmo_pkey primary key (id),

    constraint ttmo_type_id_requirement_basis_key unique (type_id, requirement_basis),

    constraint ttmo_type_id_id_key unique (type_id, id),

    constraint ttmo_tool_type_id_fkey foreign key (type_id) references specification.tool_type (id)
);


CREATE TABLE IF NOT EXISTS specification.tool_master
(
    id                 uuid                 DEFAULT gen_random_uuid(),

    manufacturer_code  TEXT        NOT NULL,
    type_id            uuid        NOT NULL,
    mounting_option_id uuid        not null,
    manufacturer       TEXT        NOT NULL,
    description        TEXT,

    order_code         TEXT,
    link               TEXT,
    catalog            TEXT,

    created_by         TEXT                 DEFAULT current_setting('app.current_user', true),
    created_at         TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at         TIMESTAMPTZ,
    updated_by         TEXT,
    deleted_at         TIMESTAMPTZ,

    constraint tm_pkey primary key (id),

    constraint tm_mounting_option_id_fkey foreign key (type_id, mounting_option_id)
        references specification.tool_type_mounting_option (type_id, id),

    constraint tm_tool_type_id_fkey foreign key (type_id) references specification.tool_type (id)
);


CREATE TABLE IF NOT EXISTS specification.tool_mounting_feature_requirement
(
    id                     uuid                 DEFAULT gen_random_uuid(),

    mounting_option_id     uuid        not null,
    requirement_feature_id uuid        NOT NULL,

    created_by             TEXT                 DEFAULT current_setting('app.current_user', true),
    created_at             TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at             TIMESTAMPTZ,
    updated_by             TEXT,
    deleted_at             TIMESTAMPTZ,

    constraint tmfr_pkey primary key (id),

    constraint tmfr_mounting_option_id_requirement_feature_id_key unique (mounting_option_id,requirement_feature_id),

    constraint tmfr_tool_mounting_feature_requirement_id_fkey foreign key (mounting_option_id)
        references specification.tool_type_mounting_option (id),

    constraint tmfr_entity_feature_id_fkey foreign key (requirement_feature_id)
        references core.entity_feature (id)

);

