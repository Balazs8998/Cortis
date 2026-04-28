-- users definition
CREATE TABLE IF NOT EXISTS personal.users
(
    id            uuid PRIMARY KEY     DEFAULT gen_random_uuid(),
    username      TEXT UNIQUE NOT NULL,
    password_hash TEXT,
    chip_code     TEXT UNIQUE,
    is_active     BOOLEAN     NOT NULL DEFAULT true,

    created_by    TEXT                 DEFAULT COALESCE(current_setting('app.current_user', true), 'system'),
    created_at    TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at    TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_by    TEXT                 DEFAULT COALESCE(current_setting('app.current_user', true), 'system'),
    deleted_at    TIMESTAMPTZ
);

-- roles definition
CREATE TABLE IF NOT EXISTS personal.roles
(
    id         uuid PRIMARY KEY     DEFAULT gen_random_uuid(),
    name       TEXT UNIQUE NOT NULL,
    is_admin   BOOLEAN     NOT NULL DEFAULT false,
    created_by TEXT                 DEFAULT current_setting('app.current_user', true),
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at TIMESTAMPTZ,
    updated_by TEXT        NOT NULL DEFAULT current_setting('app.current_user', true),
    deleted_at TIMESTAMPTZ
);

-- permissions definition
CREATE TABLE IF NOT EXISTS personal.permissions
(
    id          uuid PRIMARY KEY     DEFAULT gen_random_uuid(),
    code        TEXT UNIQUE NOT NULL,
    description TEXT,

    created_by  TEXT                 DEFAULT current_setting('app.current_user', true),
    created_at  TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at  TIMESTAMPTZ,
    updated_by  TEXT        NOT NULL DEFAULT current_setting('app.current_user', true),
    deleted_at  TIMESTAMPTZ
);

-- user_roles definition
CREATE TABLE IF NOT EXISTS personal.user_roles
(
    user_id    uuid        NOT NULL REFERENCES personal.users (id) ON DELETE CASCADE,
    role_id    uuid        NOT NULL REFERENCES personal.roles (id) ON DELETE CASCADE,

    created_by TEXT                 DEFAULT current_setting('app.current_user', true),
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at TIMESTAMPTZ,
    updated_by TEXT        NOT NULL DEFAULT current_setting('app.current_user', true),
    deleted_at TIMESTAMPTZ,
    PRIMARY KEY (user_id, role_id)
);

-- role_permissions definition
CREATE TABLE IF NOT EXISTS personal.role_permissions
(
    role_id       uuid        NOT NULL REFERENCES personal.roles (id) ON DELETE CASCADE,
    permission_id uuid        NOT NULL REFERENCES personal.permissions (id) ON DELETE CASCADE,

    created_by    TEXT                 DEFAULT current_setting('app.current_user', true),
    created_at    TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at    TIMESTAMPTZ,
    updated_by    TEXT        NOT NULL DEFAULT current_setting('app.current_user', true),
    deleted_at    TIMESTAMPTZ,
    PRIMARY KEY (role_id, permission_id)
);

