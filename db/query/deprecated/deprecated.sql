
CREATE TABLE IF NOT EXISTS specification.tool_feature_definition
(
    id                           uuid PRIMARY KEY     DEFAULT gen_random_uuid(),
    feature_definition_name TEXT        NOT NULL,
    scope                   TEXT        NOT NULL,
    value_type              TEXT        NOT NULL,
    unit_default            TEXT        NOT NULL,
    category                     TEXT,
    description                  TEXT,
    created_by                   TEXT                 DEFAULT current_setting('app.current_user', true),
    created_at                   TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at                   TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_by                   TEXT                 DEFAULT current_setting('app.current_user', true),
    deleted_at                   TEXT
    );


CREATE TABLE IF NOT EXISTS specification.tool_type_mounting_feature_requirement
(
    id                     uuid PRIMARY KEY     DEFAULT gen_random_uuid(),
    type_id           uuid     NOT NULL,
    requirement_feature_id uuid     NOT NULL,
    is_required            INTEGER     NOT NULL,
    context                TEXT        NOT NULL,
    created_by             TEXT                 DEFAULT current_setting('app.current_user', true),
    created_at             TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at             TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_by             TEXT                 DEFAULT current_setting('app.current_user', true),
    deleted_at             TEXT,
    FOREIGN KEY (type_id) REFERENCES specification.tool_type (id),
    FOREIGN KEY (requirement_feature_id) REFERENCES specification.tool_feature_definition (id),
    UNIQUE (type_id, requirement_feature_id)
    );


CREATE TABLE IF NOT EXISTS specification.interface_feature_definition
(
    id                                uuid PRIMARY KEY     DEFAULT gen_random_uuid(),
    feature_definition_name TEXT        NOT NULL,
    scope                   TEXT        NOT NULL,
    value_type              TEXT        NOT NULL,
    unit_default            TEXT        NOT NULL,
    category                          TEXT,
    description                       TEXT,
    created_by                        TEXT                 DEFAULT current_setting('app.current_user', true),
    created_at                        TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at                        TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_by                        TEXT                 DEFAULT current_setting('app.current_user', true),
    deleted_at                        TEXT
    );

CREATE TABLE IF NOT EXISTS specification.interface_type_mounting_feature_requirement
(
    id                     uuid PRIMARY KEY     DEFAULT gen_random_uuid(),
    type_id      uuid        NOT NULL,
    requirement_feature_id uuid        NOT NULL,
    created_by             TEXT                 DEFAULT current_setting('app.current_user', true),
    created_at             TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at             TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_by             TEXT                 DEFAULT current_setting('app.current_user', true),
    deleted_at             TEXT,
    FOREIGN KEY (type_id) REFERENCES specification.interface_type (id),
    FOREIGN KEY (requirement_feature_id) REFERENCES specification.interface_feature_definition (id),
    UNIQUE (type_id, requirement_feature_id)
    );



CREATE TABLE IF NOT EXISTS specification.holder_feature_definition
(
    id                             uuid PRIMARY KEY     DEFAULT gen_random_uuid(),
    feature_definition_name TEXT        NOT NULL,
    scope                   TEXT        NOT NULL,
    value_type              TEXT        NOT NULL,
    unit_default            TEXT        NOT NULL,
    category                       TEXT,
    description                    TEXT,
    created_by                     TEXT                 DEFAULT current_setting('app.current_user', true),
    created_at                     TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at                     TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_by                     TEXT                 DEFAULT current_setting('app.current_user', true),
    deleted_at                     TEXT
);


CREATE TABLE IF NOT EXISTS specification.holder_type_mounting_feature_requirement
(
    id                     uuid PRIMARY KEY     DEFAULT gen_random_uuid(),
    type_id         uuid     NOT NULL,
    requirement_feature_id uuid     NOT NULL,
    created_by             TEXT                 DEFAULT current_setting('app.current_user', true),
    created_at             TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at             TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_by             TEXT                 DEFAULT current_setting('app.current_user', true),
    deleted_at             TEXT,
    FOREIGN KEY (type_id) REFERENCES specification.holder_type (id),
    FOREIGN KEY (requirement_feature_id) REFERENCES specification.holder_feature_definition (id),
    UNIQUE (type_id, requirement_feature_id)
);



CREATE TABLE IF NOT EXISTS specification.machine_feature_definition
(
    id                              uuid PRIMARY KEY     DEFAULT gen_random_uuid(),
    feature_definition_name TEXT        NOT NULL,
    scope                   TEXT        NOT NULL,
    value_type              TEXT        NOT NULL,
    unit_default            TEXT        NOT NULL,
    category                        TEXT,
    description                     TEXT,
    created_by                      TEXT                 DEFAULT current_setting('app.current_user', true),
    created_at                      TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at                      TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_by                      TEXT                 DEFAULT current_setting('app.current_user', true),
    deleted_at                      TEXT
);


CREATE TABLE IF NOT EXISTS specification.machine_station_feature_requirement
(
    id                     uuid PRIMARY KEY     DEFAULT gen_random_uuid(),
    machine_type_id        uuid     NOT NULL,
    requirement_feature_id uuid     NOT NULL,
    tool_station_id         uuid    NOT NULL ,
    created_by             TEXT                 DEFAULT current_setting('app.current_user', true),
    created_at             TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at             TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_by             TEXT                 DEFAULT current_setting('app.current_user', true),
    deleted_at             TEXT,
    FOREIGN KEY (machine_type_id) REFERENCES specification.machine_type (id),
    FOREIGN KEY (requirement_feature_id) REFERENCES specification.machine_feature_definition (id),
    FOREIGN KEY (tool_station_id) REFERENCES specification.machine_tool_station_category (id),
    UNIQUE (machine_type_id, requirement_feature_id)
);
