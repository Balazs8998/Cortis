-- CompanyTool definition

CREATE TABLE IF NOT EXISTS company.tool_inventory
(
    id             uuid                 DEFAULT gen_random_uuid(),

    tool_code      TEXT        NOT NULL,
    name           TEXT        NOT NULL,
    quantity       INTEGER     NOT NULL DEFAULT 0,
    location       TEXT        NOT NULL DEFAULT 'pending',
    tool_master_id uuid        NOT NULL,


    created_by     TEXT                 DEFAULT current_setting('app.current_user', true),
    created_at     TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at     TIMESTAMPTZ,
    updated_by     TEXT,
    deleted_at     TIMESTAMPTZ,

    constraint ti_pkey primary key (id),
    constraint ti_tool_master_id_fkey foreign key (tool_master_id) references specification.tool_master (id),
    CONSTRAINT ti_quantity_check CHECK (quantity >= 0),
    constraint ti_tool_code_key unique (tool_code)
);

CREATE TABLE IF NOT EXISTS company.interface_inventory
(
    id                  uuid                 DEFAULT gen_random_uuid(),

    interface_code      TEXT        NOT NULL ,
    name                TEXT        NOT NULL,
    quantity            INTEGER     NOT NULL DEFAULT 0,
    location            TEXT        NOT NULL DEFAULT 'pending',
    interface_master_id uuid        NOT NULL,


    created_by          TEXT                 DEFAULT current_setting('app.current_user', true),
    created_at          TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at          TIMESTAMPTZ,
    updated_by          TEXT,
    deleted_at          TIMESTAMPTZ,

    constraint ii_pkey primary key (id),
    constraint ii_interface_master_id_fkey foreign key (interface_master_id) references specification.interface_master (id),
    CONSTRAINT ii_quantity_check CHECK (quantity >= 0),
    constraint ii_interface_code_key unique (interface_code)
);

CREATE TABLE IF NOT EXISTS company.holder_inventory
(
    id               uuid     DEFAULT gen_random_uuid(),

    holder_code      TEXT        NOT NULL ,
    name             TEXT        NOT NULL,
    quantity         INTEGER     NOT NULL DEFAULT 0,
    location         TEXT        NOT NULL DEFAULT 'pending',
    holder_master_id uuid        NOT NULL,


    created_by       TEXT                 DEFAULT current_setting('app.current_user', true),
    created_at       TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at       TIMESTAMPTZ,
    updated_by       TEXT,
    deleted_at       TIMESTAMPTZ,

    constraint hi_pkey primary key (id),
    constraint hi_holder_master_id_fkey foreign key (holder_master_id) references specification.holder_master (id),
    CONSTRAINT hi_quantity_check CHECK (quantity >= 0),
    constraint hi_holder_code_key unique (holder_code)
);


CREATE TABLE IF NOT EXISTS company.machine_inventory
(
    id                uuid      DEFAULT gen_random_uuid(),

    machine_code      TEXT        NOT NULL ,
    name              TEXT        NOT NULL,
    location          TEXT        NOT NULL DEFAULT 'pending',
    machine_master_id uuid        NOT NULL,


    created_by        TEXT                 DEFAULT current_setting('app.current_user', true),
    created_at        TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at        TIMESTAMPTZ,
    updated_by        TEXT,
    deleted_at        TIMESTAMPTZ,

    constraint mi_pkey primary key (id),
    constraint mi_machine_master_id_fkey foreign key (machine_master_id) references specification.machine_master (id),
    constraint mi_machine_code_key unique (machine_code)
);



