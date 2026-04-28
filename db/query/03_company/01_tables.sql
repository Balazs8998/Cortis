-- CompanyTool definition

CREATE TABLE IF NOT EXISTS company.tool_inventory
(
    id                uuid PRIMARY KEY     DEFAULT gen_random_uuid(),
    company_tool_code TEXT        NOT NULL UNIQUE,
    name              TEXT        NOT NULL,
    tool_code         TEXT        NOT NULL,
    quantity          INTEGER     NOT NULL DEFAULT 0 CHECK (quantity >= 0),
    location          TEXT        NOT NULL DEFAULT 'pending',
    tool_master_id    uuid        NOT NULL,
    order_code        TEXT        NOT NULL,
    link              TEXT,
    catalog           TEXT,
    created_by        TEXT                 DEFAULT current_setting('app.current_user', true),
    created_at        TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at        TIMESTAMPTZ,
    updated_by        TEXT        NOT NULL DEFAULT current_setting('app.current_user', true),
    deleted_at        TEXT,
    FOREIGN KEY (tool_master_id) REFERENCES specification.tool_master (id)

);

CREATE TABLE IF NOT EXISTS company.interface_inventory
(
    id                     uuid PRIMARY KEY     DEFAULT gen_random_uuid(),
    company_interface_code TEXT        NOT NULL UNIQUE,
    interface_name         TEXT        NOT NULL,
    interface_code         TEXT        NOT NULL,
    quantity               INTEGER     NOT NULL DEFAULT 0 CHECK (quantity >= 0),
    location               TEXT        NOT NULL DEFAULT 'pending',
    interface_master_id    uuid        NOT NULL,
    order_code             TEXT        NOT NULL,
    link                   TEXT,
    catalog                TEXT,
    created_by             TEXT                 DEFAULT current_setting('app.current_user', true),
    created_at             TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at             TIMESTAMPTZ,
    updated_by             TEXT        NOT NULL DEFAULT current_setting('app.current_user', true),
    deleted_at             TEXT,
    FOREIGN KEY (interface_master_id) REFERENCES specification.interface_master (id)
);

CREATE TABLE company.holder_inventory
(
    id                  uuid PRIMARY KEY     DEFAULT gen_random_uuid(),
    company_holder_code TEXT        NOT NULL UNIQUE,
    holder_name         TEXT        NOT NULL,
    holder_code         TEXT        NOT NULL,
    quantity            INTEGER     NOT NULL DEFAULT 0 CHECK (quantity >= 0),
    location            TEXT        NOT NULL DEFAULT 'pending',
    holder_master_id    uuid     NOT NULL,
    link                TEXT,
    catalog             TEXT,
    order_code          TEXT        NOT NULL,
    created_by          TEXT                 DEFAULT current_setting('app.current_user', true),
    created_at          TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at          TIMESTAMPTZ,
    updated_by          TEXT        NOT NULL DEFAULT current_setting('app.current_user', true),
    deleted_at          TEXT,
    FOREIGN KEY (holder_master_id) REFERENCES specification.holder_master (id)
);


CREATE TABLE company.machine_inventory
(
    id                   uuid PRIMARY KEY     DEFAULT gen_random_uuid(),
    company_machine_code TEXT        NOT NULL UNIQUE,
    machine_name         TEXT        NOT NULL,
    machine_code         TEXT        NOT NULL,
    location             TEXT        NOT NULL DEFAULT 'pending',
    machine_master_id    uuid     NOT NULL,
    link                 TEXT,
    catalog              TEXT,
    order_code           TEXT        NOT NULL,
    created_by           TEXT                 DEFAULT current_setting('app.current_user', true),
    created_at           TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at           TIMESTAMPTZ,
    updated_by           TEXT        NOT NULL DEFAULT current_setting('app.current_user', true),
    deleted_at           TEXT,
    FOREIGN KEY (machine_master_id) REFERENCES specification.machine_master (id)
);

/*
CREATE TABLE company.material
(
    id                    uuid PRIMARY KEY     DEFAULT gen_random_uuid(),
    company_material_code TEXT        NOT NULL DEFAULT 'pending' UNIQUE,
    material_name         TEXT,
    material_code         TEXT        NOT NULL,
    quantity              INTEGER     NOT NULL DEFAULT 0 CHECK (quantity >= 0),
    location              TEXT        NOT NULL DEFAULT 'pending',
    diameter              REAL        NOT NULL CHECK (diameter > 0),
    comment               TEXT,
    link                  TEXT,
    catalog               TEXT,
    material_master_id    INTEGER     NOT NULL,
    order_code            TEXT        NOT NULL,
    created_by            TEXT                 DEFAULT current_setting('app.current_user', true),
    created_at            TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at            TIMESTAMPTZ,
    updated_by            TEXT        NOT NULL DEFAULT current_setting('app.current_user', true),
    deleted_at            TEXT
);
 */


