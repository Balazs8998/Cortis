CREATE TABLE IF NOT EXISTS specification.interface_type
(
    id          uuid                 DEFAULT gen_random_uuid(),

    name        TEXT        NOT NULL UNIQUE,
    description TEXT,

    created_by  TEXT                 DEFAULT current_setting('app.current_user', true),
    created_at  TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at  TIMESTAMPTZ,
    updated_by  TEXT,
    deleted_at  TIMESTAMPTZ,

    CONSTRAINT it_name_key UNIQUE (name),
    constraint it_pkey primary key (id)
);


CREATE TABLE IF NOT EXISTS specification.interface_master
(
    id                uuid                 DEFAULT gen_random_uuid(),

    manufacturer_code TEXT        NOT NULL,
    type_id           uuid        NOT NULL,
    manufacturer      TEXT        NOT NULL,
    description       TEXT,

    order_code        TEXT,
    link              TEXT,
    catalog           TEXT,

    created_by        TEXT                 DEFAULT current_setting('app.current_user', true),
    created_at        TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at        TIMESTAMPTZ,
    updated_by        TEXT,
    deleted_at        TIMESTAMPTZ,

    constraint im_pkey primary key (id),
    constraint im_interface_type_id_fkey foreign key (type_id) references specification.interface_type (id)

);


CREATE TABLE IF NOT EXISTS specification.interface_type_mounting_feature_requirement
(
    id                     uuid     DEFAULT gen_random_uuid(),

    type_id                uuid        NOT NULL,
    requirement_feature_id uuid        NOT NULL,

    created_by             TEXT                 DEFAULT current_setting('app.current_user', true),
    created_at             TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at             TIMESTAMPTZ,
    updated_by             TEXT,
    deleted_at             TEXT,

    constraint itmfr_pkey primary key (id),
    constraint itmfr_interface_type_id_fkey foreign key (type_id) references specification.interface_type(id),
    constraint itmfr_entity_feature_id_fkey foreign key (requirement_feature_id) references core.entity_feature(id),
    constraint itmfr_type_id_requirement_feature_id_key UNIQUE (type_id, requirement_feature_id)

);
