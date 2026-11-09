-- CORTIS Phase 4D. Concrete machine stations and human-reviewed mounting chains.
-- Historic migrations and inventory entries are not modified.
CREATE TABLE company.machine_station (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    machine_inventory_id UUID NOT NULL REFERENCES company.machine_inventory(id),
    position_index INTEGER NOT NULL CHECK (position_index BETWEEN 1 AND 999),
    label TEXT NOT NULL CHECK (length(btrim(label)) BETWEEN 1 AND 100),
    station_type core.machine_station_type NOT NULL,
    max_tool_diameter NUMERIC(12,3) CHECK (max_tool_diameter > 0),
    max_tool_length NUMERIC(12,3) CHECK (max_tool_length > 0),
    max_holder_length NUMERIC(12,3) CHECK (max_holder_length > 0),
    active BOOLEAN NOT NULL DEFAULT true,
    created_by UUID NOT NULL REFERENCES personal.users(id),
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    UNIQUE(machine_inventory_id, position_index)
);
CREATE INDEX machine_station_machine_idx ON company.machine_station(machine_inventory_id, position_index);

-- An approved station mount says ONLY that this mounting chain was reviewed for the
-- particular station. Tool/holder compatibility is a separate reviewed fact.
CREATE TABLE company.machine_station_mount (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    station_id UUID NOT NULL REFERENCES company.machine_station(id),
    holder_master_id UUID NOT NULL REFERENCES specification.holder_master(id),
    interface_master_id UUID REFERENCES specification.interface_master(id),
    evidence TEXT NOT NULL CHECK (length(btrim(evidence)) BETWEEN 12 AND 2000),
    source_reference TEXT NOT NULL CHECK (length(btrim(source_reference)) BETWEEN 3 AND 500),
    reviewed_by UUID NOT NULL REFERENCES personal.users(id),
    reviewed_at TIMESTAMPTZ NOT NULL DEFAULT now()
);
CREATE UNIQUE INDEX machine_station_mount_pair_unique ON company.machine_station_mount
    (station_id, holder_master_id, interface_master_id) NULLS NOT DISTINCT;
CREATE INDEX machine_station_mount_station_idx ON company.machine_station_mount(station_id);

INSERT INTO personal.permissions (code, description) VALUES
 ('CORTIS_ENGINEERING_READ', 'Read engineering features, physical machine positions and assembly candidates'),
 ('CORTIS_ENGINEERING_WRITE', 'Maintain engineering master features and machine positions'),
 ('CORTIS_ENGINEERING_REVIEW', 'Approve evidenced holder/interface mounting chains on a machine position')
ON CONFLICT (code) DO NOTHING;

-- Read grants follow existing compatibility readers; writes/reviews only for admin roles.
INSERT INTO personal.role_permissions (role_id,permission_id)
SELECT r.id,p.id FROM personal.roles r
CROSS JOIN personal.permissions p
WHERE r.deleted_at IS NULL AND p.deleted_at IS NULL
  AND (r.is_admin OR (p.code='CORTIS_ENGINEERING_READ' AND EXISTS (
     SELECT 1 FROM personal.role_permissions rp JOIN personal.permissions oldp ON oldp.id=rp.permission_id
     WHERE rp.role_id=r.id AND rp.deleted_at IS NULL AND oldp.deleted_at IS NULL
       AND oldp.code='CORTIS_COMPATIBILITY_READ')))
  AND p.code IN ('CORTIS_ENGINEERING_READ','CORTIS_ENGINEERING_WRITE','CORTIS_ENGINEERING_REVIEW')
ON CONFLICT (role_id, permission_id) DO UPDATE SET deleted_at=NULL;
