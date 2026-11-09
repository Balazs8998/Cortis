-- CORTIS V1 Phase 4B: sales orders. V19 follows the existing Phase 3A V18.
-- No existing inventory or historical order data is rewritten.
CREATE SEQUENCE company.sales_order_number_seq AS BIGINT START WITH 1 INCREMENT BY 1 NO CYCLE;

CREATE TABLE company.sales_order (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    order_code TEXT NOT NULL UNIQUE CHECK (order_code ~ '^AU[0-9]{6,}$'),
    customer TEXT NOT NULL CHECK (length(btrim(customer)) BETWEEN 1 AND 200),
    order_date DATE NOT NULL,
    delivery_date DATE NOT NULL,
    status TEXT NOT NULL DEFAULT 'NEW' CHECK (status IN ('NEW', 'IN_PROGRESS', 'READY', 'SHIPPED', 'CANCELLED')),
    note TEXT CHECK (note IS NULL OR length(note) <= 2000),
    created_by UUID NOT NULL REFERENCES personal.users(id),
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_by UUID REFERENCES personal.users(id),
    updated_at TIMESTAMPTZ,
    deleted_at TIMESTAMPTZ
);

CREATE INDEX sales_order_delivery_idx ON company.sales_order(delivery_date, order_code)
    WHERE deleted_at IS NULL;
CREATE INDEX sales_order_status_delivery_idx ON company.sales_order(status, delivery_date)
    WHERE deleted_at IS NULL;

CREATE TABLE company.sales_order_line (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    sales_order_id UUID NOT NULL REFERENCES company.sales_order(id) ON DELETE RESTRICT,
    position INTEGER NOT NULL CHECK (position > 0),
    item_code TEXT CHECK (item_code IS NULL OR length(btrim(item_code)) BETWEEN 1 AND 100),
    description TEXT NOT NULL CHECK (length(btrim(description)) BETWEEN 1 AND 500),
    quantity NUMERIC(12, 3) NOT NULL CHECK (quantity > 0),
    unit TEXT NOT NULL DEFAULT 'PCS' CHECK (unit ~ '^[A-Z]{1,8}$'),
    CONSTRAINT sales_order_line_position_unique UNIQUE(sales_order_id, position)
);
CREATE INDEX sales_order_line_order_idx ON company.sales_order_line(sales_order_id, position);

INSERT INTO personal.permissions(code, description) VALUES
    ('CORTIS_ORDERS_READ', 'Read orders, order lines and delivery schedules'),
    ('CORTIS_ORDERS_WRITE', 'Create orders and update order statuses')
ON CONFLICT (code) DO NOTHING;

-- Only existing active admin roles receive the new permissions automatically.
INSERT INTO personal.role_permissions(role_id, permission_id)
SELECT r.id, p.id FROM personal.roles r CROSS JOIN personal.permissions p
WHERE r.is_admin AND r.deleted_at IS NULL AND p.deleted_at IS NULL
  AND p.code IN ('CORTIS_ORDERS_READ', 'CORTIS_ORDERS_WRITE')
ON CONFLICT (role_id, permission_id) DO UPDATE SET deleted_at = NULL;
