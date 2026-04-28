-- EngineVersion definition

CREATE TABLE IF NOT EXISTS core.engine_version(
                              module TEXT NOT NULL,
                              version TEXT NOT NULL DEFAULT '0.1.0',
                              release_date TEXT NOT NULL DEFAULT (CURRENT_TIMESTAMP),
                              deployed_date TEXT,
                              is_stable INTEGER NOT NULL CHECK(is_stable IN (0,1)),
                              description TEXT,

                              PRIMARY KEY (module, version)
);
