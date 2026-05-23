SET search_path TO map;

CREATE TABLE IF NOT EXISTS tool_mounting_option_map
(
    id             uuid                 DEFAULT gen_random_uuid(),

    tool_master_id uuid        NOT NULL,
    option_id      uuid        NOT NULL,

    created_by     TEXT                 DEFAULT current_setting('app.current_user', true),
    created_at     TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at     TIMESTAMPTZ,
    updated_by     TEXT,
    deleted_at     TIMESTAMPTZ,

    CONSTRAINT tmom_pkey PRIMARY KEY (id),

    CONSTRAINT tmom_tool_master_id_option_id_key UNIQUE (tool_master_id, option_id),

    CONSTRAINT tmom_tool_master_id_fkey FOREIGN KEY (tool_master_id)
        REFERENCES specification.tool_master (id),

    CONSTRAINT tmom_mounting_option_id_fkey FOREIGN KEY (option_id)
        REFERENCES specification.tool_type_mounting_option (id)
);

CREATE TABLE IF NOT EXISTS holder_mounting_option_map
(
    id               uuid                 DEFAULT gen_random_uuid(),

    holder_master_id uuid        NOT NULL,
    option_id        uuid        NOT NULL,

    created_by       TEXT                 DEFAULT current_setting('app.current_user', true),
    created_at       TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at       TIMESTAMPTZ,
    updated_by       TEXT,
    deleted_at       TIMESTAMPTZ,

    CONSTRAINT hmom_pkey PRIMARY KEY (id),

    CONSTRAINT hmom_holder_master_id_option_id_key UNIQUE (holder_master_id, option_id),

        CONSTRAINT hmom_holder_master_id_fkey FOREIGN KEY (holder_master_id)
        REFERENCES specification.holder_master (id),

    CONSTRAINT hmom_mounting_option_id_fkey FOREIGN KEY (option_id)
        REFERENCES specification.holder_type_mounting_option (id)
);