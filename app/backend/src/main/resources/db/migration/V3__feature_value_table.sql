CREATE TABLE IF NOT EXISTS specification.entity_feature_value
(
    id                uuid                 DEFAULT gen_random_uuid(),

    entity_id         uuid        NOT NULL,
    entity_type       TEXT        NOT NULL,
    entity_feature_id uuid        NOT NULL,

    value_number      NUMERIC,
    value_text        TEXT,

    created_by        TEXT                 DEFAULT current_setting('app.current_user', true),
    created_at        TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at        TIMESTAMPTZ,
    updated_by        TEXT,
    deleted_at        TIMESTAMPTZ,

    constraint efv_pkey primary key (id),
    constraint efv_text_or_number_check CHECK ( value_text IS NOT NULL OR value_number IS NOT NULL),
    constraint efv_entity_type_check CHECK ( entity_type IN (
                                                             'tool_master',
                                                             'interface_master',
                                                             'holder_master',
                                                             'machine_master',
                                                             'machine_tool_station'
        ) ),
    constraint efv_entity_type_id_feature_id_key UNIQUE (entity_type, entity_id, entity_feature_id),
    constraint efv_entity_feature_id_fkey foreign key (entity_feature_id) references core.entity_feature
);



