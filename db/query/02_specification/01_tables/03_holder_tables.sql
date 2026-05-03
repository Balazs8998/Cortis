CREATE TABLE IF NOT EXISTS specification.holder_type
(
    id           uuid                 DEFAULT gen_random_uuid(),

    name       TEXT        NOT NULL UNIQUE,
    is_interface BOOLEAN     NOT NULL default true,
    description  TEXT,

    created_by   TEXT                 DEFAULT current_setting('app.current_user', true),
    created_at   TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at   TIMESTAMPTZ,
    updated_by   TEXT,
    deleted_at   TIMESTAMPTZ,

    CONSTRAINT ht_name_key UNIQUE (name),
    constraint ht_pkey primary key (id)
);


CREATE TABLE IF NOT EXISTS specification.holder_master
(
    id                uuid                 DEFAULT gen_random_uuid(),

    manufacturer_code TEXT        NOT NULL,
    type_id           uuid        NOT NULL,
    manufacturer      TEXT        NOT NULL,
    description       TEXT,

    link              TEXT,
    catalog           TEXT,
    order_code        TEXT,

    created_by        TEXT                 DEFAULT current_setting('app.current_user', true),
    created_at        TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at        TIMESTAMPTZ,
    updated_by        TEXT,
    deleted_at        TIMESTAMPTZ,

    constraint hm_pkey primary key (id),
    constraint hm_holder_type_id_fkey foreign key (type_id) references specification.holder_type (id)
);

create table if not exists specification.holder_type_mounting_option
(
    id                uuid                                      default gen_random_uuid(),

    type_id           uuid                             not null,
    option_name       text                             not null,
    requirement_basis core.holder_clamping_requirement not null,

    created_by        text                                      default current_setting('app.current_user', true),
    created_at        timestamptz                      not null default now(),
    updated_at        timestamptz,
    updated_by        text,
    deleted_at        timestamptz,

    constraint htmo_pkey primary key (id),
    constraint htmo_type_id_option_name_key unique (type_id, option_name),
    constraint htmo_holder_type_id_fkey foreign key (type_id) references specification.tool_type (id)
);

CREATE TABLE IF NOT EXISTS specification.holder_type_mounting_feature_requirement
(
    id                     uuid     DEFAULT gen_random_uuid(),

    mounting_option_id                uuid        NOT NULL,
    requirement_feature_id uuid        NOT NULL,

    created_by             TEXT                 DEFAULT current_setting('app.current_user', true),
    created_at             TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at             TIMESTAMPTZ,
    updated_by             TEXT,
    deleted_at             TEXT,

    constraint htmfr_pkey primary key (id),
    constraint htmfr_holder_type_id_fkey foreign key (mounting_option_id) references specification.holder_type_mounting_option(id),
    constraint htmfr_requirement_feature_id_fkey foreign key (requirement_feature_id) references core.entity_feature(id)
);
