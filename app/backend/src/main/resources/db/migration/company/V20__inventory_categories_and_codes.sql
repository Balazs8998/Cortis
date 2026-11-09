-- CORTIS V1 Phase 4C. Add materials/accessories/parts and server-generated inventory codes.
-- V1-V19 remain unchanged; existing business codes and ledger rows are preserved.

-- Independent, transaction-safe sequences per business prefix. Sequences are intentionally non-transactional:
-- rollback gaps are acceptable; reuse of issued identifiers is not.
CREATE SEQUENCE company.inventory_tool_code_seq AS BIGINT MINVALUE 1 MAXVALUE 999999 START 1 NO CYCLE;
CREATE SEQUENCE company.inventory_holder_code_seq AS BIGINT MINVALUE 1 MAXVALUE 999999 START 1 NO CYCLE;
CREATE SEQUENCE company.inventory_interface_code_seq AS BIGINT MINVALUE 1 MAXVALUE 999999 START 1 NO CYCLE;
CREATE SEQUENCE company.inventory_machine_code_seq AS BIGINT MINVALUE 1 START 1 NO CYCLE;
CREATE SEQUENCE company.inventory_material_code_seq AS BIGINT MINVALUE 1 MAXVALUE 999999 START 1 NO CYCLE;
CREATE SEQUENCE company.inventory_accessory_code_seq AS BIGINT MINVALUE 1 MAXVALUE 999999 START 1 NO CYCLE;
CREATE SEQUENCE company.inventory_part_code_seq AS BIGINT MINVALUE 1 MAXVALUE 999999 START 1 NO CYCLE;

-- Seed sequences from *all* existing (including archived) V9 codes. No codes are rewritten.
-- false on empty means the first nextval() returns 1; true after an existing maximum means max+1.
SELECT setval('company.inventory_tool_code_seq', COALESCE((SELECT MAX(SUBSTRING(tool_code FROM 2)::BIGINT) FROM company.tool_inventory WHERE tool_code ~ '^W[0-9]{6}$'), 1), EXISTS (SELECT 1 FROM company.tool_inventory WHERE tool_code ~ '^W[0-9]{6}$'));
SELECT setval('company.inventory_holder_code_seq', COALESCE((SELECT MAX(SUBSTRING(holder_code FROM 2)::BIGINT) FROM company.holder_inventory WHERE holder_code ~ '^H[0-9]{6}$'), 1), EXISTS (SELECT 1 FROM company.holder_inventory WHERE holder_code ~ '^H[0-9]{6}$'));
SELECT setval('company.inventory_interface_code_seq', COALESCE((SELECT MAX(SUBSTRING(interface_code FROM 2)::BIGINT) FROM company.interface_inventory WHERE interface_code ~ '^I[0-9]{6}$'), 1), EXISTS (SELECT 1 FROM company.interface_inventory WHERE interface_code ~ '^I[0-9]{6}$'));
SELECT setval('company.inventory_machine_code_seq', COALESCE((SELECT MAX(SUBSTRING(machine_code FROM 2)::BIGINT) FROM company.machine_inventory WHERE machine_code ~ '^M[1-9][0-9]*$' AND length(machine_code) <= 19), 1), EXISTS (SELECT 1 FROM company.machine_inventory WHERE machine_code ~ '^M[1-9][0-9]*$' AND length(machine_code) <= 19));

-- All new inventory inserts go through the database default; callers cannot choose an API code.
ALTER TABLE company.tool_inventory ALTER COLUMN tool_code SET DEFAULT ('W' || lpad(nextval('company.inventory_tool_code_seq')::TEXT, 6, '0'));
ALTER TABLE company.holder_inventory ALTER COLUMN holder_code SET DEFAULT ('H' || lpad(nextval('company.inventory_holder_code_seq')::TEXT, 6, '0'));
ALTER TABLE company.interface_inventory ALTER COLUMN interface_code SET DEFAULT ('I' || lpad(nextval('company.inventory_interface_code_seq')::TEXT, 6, '0'));
ALTER TABLE company.machine_inventory ALTER COLUMN machine_code SET DEFAULT ('M' || nextval('company.inventory_machine_code_seq')::TEXT);

-- Simple type/master catalogs, aligned with the existing specification naming model.
-- No CNC dimensional compatibility is inferred for these families.
CREATE TABLE specification.material_type (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name TEXT NOT NULL UNIQUE,
    description TEXT,
    created_by TEXT DEFAULT current_setting('app.current_user', true),
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at TIMESTAMPTZ,
    updated_by TEXT,
    deleted_at TIMESTAMPTZ
);
CREATE TABLE specification.accessory_type (LIKE specification.material_type INCLUDING ALL);
CREATE TABLE specification.part_type (LIKE specification.material_type INCLUDING ALL);

CREATE TABLE specification.material_master (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    manufacturer_code TEXT NOT NULL,
    type_id UUID NOT NULL REFERENCES specification.material_type(id),
    manufacturer TEXT NOT NULL,
    description TEXT,
    order_code TEXT,
    link TEXT,
    catalog TEXT,
    created_by TEXT DEFAULT current_setting('app.current_user', true),
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at TIMESTAMPTZ,
    updated_by TEXT,
    deleted_at TIMESTAMPTZ,
    CONSTRAINT material_master_manufacturer_unique UNIQUE(manufacturer_code, manufacturer)
);
CREATE TABLE specification.accessory_master (LIKE specification.material_master INCLUDING DEFAULTS INCLUDING CONSTRAINTS);
ALTER TABLE specification.accessory_master ADD CONSTRAINT accessory_master_pk PRIMARY KEY(id);
ALTER TABLE specification.accessory_master ADD CONSTRAINT accessory_master_manufacturer_unique UNIQUE(manufacturer_code, manufacturer);
ALTER TABLE specification.accessory_master ADD CONSTRAINT accessory_master_type_fk FOREIGN KEY(type_id) REFERENCES specification.accessory_type(id);
CREATE TABLE specification.part_master (LIKE specification.material_master INCLUDING DEFAULTS INCLUDING CONSTRAINTS);
ALTER TABLE specification.part_master ADD CONSTRAINT part_master_pk PRIMARY KEY(id);
ALTER TABLE specification.part_master ADD CONSTRAINT part_master_manufacturer_unique UNIQUE(manufacturer_code, manufacturer);
ALTER TABLE specification.part_master ADD CONSTRAINT part_master_type_fk FOREIGN KEY(type_id) REFERENCES specification.part_type(id);

-- For the new families, reuse the established V9 stock record shape.
CREATE TABLE company.material_inventory (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    material_code TEXT NOT NULL UNIQUE DEFAULT ('R' || lpad(nextval('company.inventory_material_code_seq')::TEXT, 6, '0')),
    name TEXT NOT NULL,
    quantity INTEGER NOT NULL DEFAULT 0 CHECK(quantity >= 0),
    location TEXT NOT NULL DEFAULT 'pending',
    material_master_id UUID NOT NULL REFERENCES specification.material_master(id),
    created_by TEXT DEFAULT current_setting('app.current_user', true),
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at TIMESTAMPTZ,
    updated_by TEXT,
    deleted_at TIMESTAMPTZ
);
CREATE TABLE company.accessory_inventory (LIKE company.material_inventory INCLUDING DEFAULTS INCLUDING CONSTRAINTS);
-- LIKE copies the source column names; replace family-specific columns and PK/FK explicitly.
ALTER TABLE company.accessory_inventory RENAME COLUMN material_code TO accessory_code;
ALTER TABLE company.accessory_inventory RENAME COLUMN material_master_id TO accessory_master_id;
ALTER TABLE company.accessory_inventory ALTER COLUMN accessory_code SET DEFAULT ('Z' || lpad(nextval('company.inventory_accessory_code_seq')::TEXT, 6, '0'));
ALTER TABLE company.accessory_inventory ADD PRIMARY KEY (id);
ALTER TABLE company.accessory_inventory ADD CONSTRAINT accessory_inventory_code_unique UNIQUE(accessory_code);
ALTER TABLE company.accessory_inventory ADD CONSTRAINT accessory_inventory_master_fk FOREIGN KEY(accessory_master_id) REFERENCES specification.accessory_master(id);
CREATE TABLE company.part_inventory (LIKE company.material_inventory INCLUDING DEFAULTS INCLUDING CONSTRAINTS);
ALTER TABLE company.part_inventory RENAME COLUMN material_code TO part_code;
ALTER TABLE company.part_inventory RENAME COLUMN material_master_id TO part_master_id;
ALTER TABLE company.part_inventory ALTER COLUMN part_code SET DEFAULT ('E' || lpad(nextval('company.inventory_part_code_seq')::TEXT, 6, '0'));
ALTER TABLE company.part_inventory ADD PRIMARY KEY (id);
ALTER TABLE company.part_inventory ADD CONSTRAINT part_inventory_code_unique UNIQUE(part_code);
ALTER TABLE company.part_inventory ADD CONSTRAINT part_inventory_master_fk FOREIGN KEY(part_master_id) REFERENCES specification.part_master(id);

CREATE INDEX material_inventory_active_idx ON company.material_inventory(name, id) WHERE deleted_at IS NULL;
CREATE INDEX accessory_inventory_active_idx ON company.accessory_inventory(name, id) WHERE deleted_at IS NULL;
CREATE INDEX part_inventory_active_idx ON company.part_inventory(name, id) WHERE deleted_at IS NULL;

-- Extend the append-only movement ledger, keeping every V18 record intact.
ALTER TABLE company.inventory_movement
    ADD COLUMN material_inventory_id UUID REFERENCES company.material_inventory(id),
    ADD COLUMN accessory_inventory_id UUID REFERENCES company.accessory_inventory(id),
    ADD COLUMN part_inventory_id UUID REFERENCES company.part_inventory(id);
ALTER TABLE company.inventory_movement DROP CONSTRAINT inventory_movement_single_asset;
ALTER TABLE company.inventory_movement ADD CONSTRAINT inventory_movement_single_asset CHECK (
    num_nonnulls(tool_inventory_id, holder_inventory_id, interface_inventory_id,
                 material_inventory_id, accessory_inventory_id, part_inventory_id) = 1
);
CREATE INDEX inventory_movement_material_idx ON company.inventory_movement(material_inventory_id, performed_at DESC);
CREATE INDEX inventory_movement_accessory_idx ON company.inventory_movement(accessory_inventory_id, performed_at DESC);
CREATE INDEX inventory_movement_part_idx ON company.inventory_movement(part_inventory_id, performed_at DESC);

INSERT INTO personal.permissions(code, description) VALUES
    ('CORTIS_INVENTORY_DELETE', 'Archive an empty stock item or a registered machine')
ON CONFLICT (code) DO NOTHING;
INSERT INTO personal.role_permissions(role_id, permission_id)
SELECT r.id, p.id FROM personal.roles r CROSS JOIN personal.permissions p
WHERE r.is_admin AND r.deleted_at IS NULL AND p.deleted_at IS NULL
  AND p.code = 'CORTIS_INVENTORY_DELETE'
ON CONFLICT (role_id, permission_id) DO UPDATE SET deleted_at = NULL;
