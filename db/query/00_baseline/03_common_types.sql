-- =========================================================
-- CORTIS database commmon_types - Baseline
-- =========================================================



-- =========================================================
-- Global enum types
-- =========================================================

CREATE TYPE record_status AS ENUM (
    'active',
    'inactive',
    'archived'
    );

create type entity_type as enum(
    'tool',
    'holder',
    'interface',
    'machine',
    'machine_station'
    );

create type feature_role_type as enum(
    'geometry',
    'mounting',
    'constraint',
    'cutting'
    )


-- =========================================================
-- Global domain types
-- =========================================================

-- =========================================================
-- Semantic ID types
-- =========================================================
