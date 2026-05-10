-- =========================================================
-- CORTIS database commmon_types - Baseline
-- =========================================================



-- =========================================================
-- Global enum types
-- =========================================================

CREATE TYPE core.record_status AS ENUM (
    'active',
    'inactive',
    'archived'
    );

create type core.entity_type as enum(
    'tool',
    'holder',
    'interface',
    'machine',
    'machine_station'
    );

create type core.role_type as enum(
    'geometry',
    'mounting',
    'constraint',
    'cutting'
    );

create type core.machine_station_type as enum(
    'turning_tool_station',
    'cross_driven_tool_station',
    'front_axial_tool_station',
    'backside_axial_tool_station'
    );

create type core.tool_mounting_requirement as enum (
    'geometry',
    'form',
    'form_and_geometry'
    );

create type core.holder_clamping_requirement as enum(
    'round',
    'square'
    );



-- =========================================================
-- Global domain types
-- =========================================================

-- =========================================================
-- Semantic ID types
-- =========================================================
