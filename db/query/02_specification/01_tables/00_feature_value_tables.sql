CREATE TABLE IF NOT EXISTS specification.entity_feature_value
(
    id                uuid PRIMARY KEY     DEFAULT gen_random_uuid(),

    entity_id         uuid        NOT NULL,
    entity_feature_id uuid        NOT NULL,

    value_number      NUMERIC,
    value_text        TEXT,

    created_by        TEXT                 DEFAULT current_setting('app.current_user', true),
    created_at        TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at        TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_by        TEXT                 DEFAULT current_setting('app.current_user', true),
    deleted_at        TIMESTAMPTZ,

    FOREIGN KEY (entity_feature_id) REFERENCES core.entity_feature (id),

    CHECK ( value_text IS NOT NULL
        OR value_number IS NOT NULL),

    UNIQUE (entity_id, entity_feature_id)
);

