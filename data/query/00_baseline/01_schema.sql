-- =========================================================
-- CORTIS database schema build - CORE
-- =========================================================

-- less noise, more signal
SET client_min_messages TO WARNING;

-- timezone / locale (ha van)
SET timezone TO 'UTC';

-- =========================================================
-- schemas
-- =========================================================
CREATE SCHEMA IF NOT EXISTS core;
CREATE SCHEMA IF NOT EXISTS translation;
CREATE SCHEMA IF NOT EXISTS specification;
CREATE SCHEMA IF NOT EXISTS company;
CREATE SCHEMA IF NOT EXISTS map;
CREATE SCHEMA IF NOT EXISTS personal;
CREATE SCHEMA IF NOT EXISTS archive;
CREATE SCHEMA IF NOT EXISTS data_debug;
CREATE SCHEMA IF NOT EXISTS metadata_debug;