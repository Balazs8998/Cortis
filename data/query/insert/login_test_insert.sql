-- =========================================================
-- CORTIS TEST DATA
-- =========================================================

-- Permissions
INSERT INTO personal.permissions (code)
VALUES
    ('create'),
    ('remove'),
    ('READ')
    ON CONFLICT (code) DO NOTHING;


-- Roles
INSERT INTO personal.roles (name, is_admin)
VALUES
    ('szerszam', false),
    ('iroda', true)
    ON CONFLICT (name) DO NOTHING;


-- =========================================================
-- Test user
-- =========================================================

-- password: test1234
-- chipCode: 04A8F29C112233

-- Authentication can be tested using the test.http file
-- located in the repository root.

INSERT INTO personal.users (
    username,
    password_hash,
    chip_code_hash,
    is_active
)
VALUES (
           'Béla',
           '$2a$10$XABBZMGuXqfROR0tVMPclOw7dQj9FOS2wZfo0HBN39cCVpSaqfjeu',
           'b2b3d2413b8683ac9a9a2abaecdd9ac0f263461bbe0b0b02d3e03f5591bf7dd7',
           true
       )
    ON CONFLICT (username) DO NOTHING;

-- Béla -> szerszam role
INSERT INTO personal.user_roles (
    user_id,
    role_id
)
SELECT
    u.id,
    r.id
FROM personal.users u
         JOIN personal.roles r
              ON r.name = 'szerszam'
WHERE u.username = 'Béla'
    ON CONFLICT DO NOTHING;


-- szerszam -> create
INSERT INTO personal.role_permissions (
    role_id,
    permission_id
)
SELECT
    r.id,
    p.id
FROM personal.roles r
         JOIN personal.permissions p
              ON p.code = 'create'
WHERE r.name = 'szerszam'
    ON CONFLICT DO NOTHING;


-- szerszam -> READ
INSERT INTO personal.role_permissions (
    role_id,
    permission_id
)
SELECT
    r.id,
    p.id
FROM personal.roles r
         JOIN personal.permissions p
              ON p.code = 'READ'
WHERE r.name = 'szerszam'
    ON CONFLICT DO NOTHING;