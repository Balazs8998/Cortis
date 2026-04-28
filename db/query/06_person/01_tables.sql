-- users definition
CREATE TABLE IF NOT EXISTS personal.users
(
    id                uuid PRIMARY KEY     DEFAULT gen_random_uuid(),
    username      TEXT UNIQUE NOT NULL,
    password_hash TEXT, -- lehet NULL, ha csak chip használat
    chip_code     TEXT, -- belépőkártya / RFID
    is_active     INTEGER     NOT NULL DEFAULT 1,
    created_at    TEXT        NOT NULL
);


-- roles definition
CREATE TABLE IF NOT EXISTS personal.roles
(
    id                uuid PRIMARY KEY     DEFAULT gen_random_uuid(),
    name     TEXT UNIQUE NOT NULL,          -- pl. admin, operator, viewer
    is_admin INTEGER     NOT NULL DEFAULT 0 -- gyors flag
);

-- permissions definition
CREATE TABLE IF NOT EXISTS personal.permissions
(
    id                uuid PRIMARY KEY     DEFAULT gen_random_uuid(),
    code        TEXT UNIQUE NOT NULL,
    description TEXT
);

-- user_roles definition
CREATE TABLE IF NOT EXISTS personal.user_roles
(
    user_id uuid NOT NULL REFERENCES personal.users (id) ON DELETE CASCADE,
    role_id uuid NOT NULL REFERENCES personal.roles (id) ON DELETE CASCADE,
    PRIMARY KEY (user_id, role_id)
);

-- role_permissions definition
CREATE TABLE IF NOT EXISTS personal.role_permissions
(
    role_id       uuid NOT NULL REFERENCES personal.roles (id) ON DELETE CASCADE,
    permission_id uuid NOT NULL REFERENCES personal.permissions (id) ON DELETE CASCADE,
    PRIMARY KEY (role_id, permission_id)
);

