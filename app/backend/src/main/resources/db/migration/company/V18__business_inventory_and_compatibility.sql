-- CORTIS V1 Phase 3A. Designed to extend existing V5-V9, V16 and V17 schema.
-- Inventory stock adjustment: immutable ledger, actual stock held in V9 inventory tables.
CREATE TABLE company.inventory_movement (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    tool_inventory_id UUID REFERENCES company.tool_inventory(id),
    holder_inventory_id UUID REFERENCES company.holder_inventory(id),
    interface_inventory_id UUID REFERENCES company.interface_inventory(id),
    previous_quantity INTEGER NOT NULL CHECK (previous_quantity >= 0),
    quantity_delta INTEGER NOT NULL CHECK (quantity_delta <> 0),
    resulting_quantity INTEGER NOT NULL CHECK (resulting_quantity >= 0),
    reason TEXT NOT NULL CHECK (length(btrim(reason)) BETWEEN 3 AND 500),
    performed_by UUID NOT NULL REFERENCES personal.users(id),
    performed_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    CONSTRAINT inventory_movement_single_asset CHECK (
        num_nonnulls(tool_inventory_id, holder_inventory_id, interface_inventory_id) = 1
    ),
    CONSTRAINT inventory_movement_balance CHECK (
        previous_quantity::BIGINT + quantity_delta::BIGINT = resulting_quantity::BIGINT
    )
);
CREATE INDEX inventory_movement_tool_idx ON company.inventory_movement(tool_inventory_id, performed_at DESC);
CREATE INDEX inventory_movement_holder_idx ON company.inventory_movement(holder_inventory_id, performed_at DESC);
CREATE INDEX inventory_movement_interface_idx ON company.inventory_movement(interface_inventory_id, performed_at DESC);

-- A reviewed decision is evidence of compatibility; it is not an inference from a similar name or dimension.
-- Rows are append-only to preserve an auditable history of review decisions.
CREATE TABLE specification.tool_holder_compatibility_decision (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    review_number BIGINT GENERATED ALWAYS AS IDENTITY UNIQUE,
    tool_master_id UUID NOT NULL REFERENCES specification.tool_master(id),
    holder_master_id UUID NOT NULL REFERENCES specification.holder_master(id),
    verdict TEXT NOT NULL CHECK (verdict IN ('COMPATIBLE', 'INCOMPATIBLE')),
    evidence TEXT NOT NULL CHECK (length(btrim(evidence)) BETWEEN 12 AND 2000),
    source_reference TEXT NOT NULL CHECK (length(btrim(source_reference)) BETWEEN 3 AND 500),
    reviewed_by UUID NOT NULL REFERENCES personal.users(id),
    reviewed_at TIMESTAMPTZ NOT NULL DEFAULT now()
);
CREATE INDEX tool_holder_decision_pair_idx
    ON specification.tool_holder_compatibility_decision(tool_master_id, holder_master_id, review_number DESC);

-- Permissions for the new business API; permissions are not granted to arbitrary users.
INSERT INTO personal.permissions(code, description) VALUES
    ('CORTIS_CATALOG_READ', 'Read CNC master catalogs and classification types'),
    ('CORTIS_CATALOG_WRITE', 'Create CNC master catalog records and classification types'),
    ('CORTIS_INVENTORY_READ', 'Read company CNC inventory and stock history'),
    ('CORTIS_INVENTORY_WRITE', 'Add company inventory and adjust quantities'),
    ('CORTIS_COMPATIBILITY_READ', 'Inspect reviewed tool/holder compatibility'),
    ('CORTIS_COMPATIBILITY_REVIEW', 'Record engineering-reviewed tool/holder compatibility')
ON CONFLICT (code) DO NOTHING;

-- Explicitly designate existing active administrator roles as holders of these permissions.
INSERT INTO personal.role_permissions(role_id, permission_id)
SELECT r.id, p.id FROM personal.roles r CROSS JOIN personal.permissions p
WHERE r.is_admin = true AND r.deleted_at IS NULL AND p.deleted_at IS NULL
  AND p.code IN ('CORTIS_CATALOG_READ','CORTIS_CATALOG_WRITE','CORTIS_INVENTORY_READ',
                 'CORTIS_INVENTORY_WRITE','CORTIS_COMPATIBILITY_READ','CORTIS_COMPATIBILITY_REVIEW')
ON CONFLICT (role_id, permission_id) DO NOTHING;
