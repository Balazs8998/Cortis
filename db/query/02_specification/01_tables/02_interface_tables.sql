CREATE TABLE IF NOT EXISTS specification.interface_type
(
    id          uuid PRIMARY KEY     DEFAULT gen_random_uuid(),

    type        TEXT        NOT NULL UNIQUE,
    description TEXT,

    created_by  TEXT                 DEFAULT current_setting('app.current_user', true),
    created_at  TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at  TIMESTAMPTZ,
    updated_by  TEXT,
    deleted_at  TIMESTAMPTZ
);


CREATE TABLE IF NOT EXISTS specification.interface_master
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

    FOREIGN KEY (type_id) REFERENCES specification.interface_type (id)
);

CREATE TABLE IF NOT EXISTS specification.interface_type_mounting_feature_requirement
(
    id                     uuid PRIMARY KEY     DEFAULT gen_random_uuid(),

    type_id                uuid        NOT NULL,
    requirement_feature_id uuid        NOT NULL,

    created_by             TEXT                 DEFAULT current_setting('app.current_user', true),
    created_at             TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at             TIMESTAMPTZ,
    updated_by             TEXT,
    deleted_at             TEXT,

    FOREIGN KEY (type_id) REFERENCES specification.interface_type (id),
    FOREIGN KEY (requirement_feature_id) REFERENCES core.entity_feature(id),
    UNIQUE (type_id, requirement_feature_id)
);
