# Database Inspection – Constraints Overview

Lists all constraints in the specification schema with definitions.

## Query
```sql
SELECT
n.nspname AS schema_name,
c.relname AS table_name,
con.conname AS constraint_name,
pg_get_constraintdef(con.oid) AS definition
FROM pg_constraint con
JOIN pg_class c ON c.oid = con.conrelid
JOIN pg_namespace n ON n.oid = c.relnamespace
WHERE n.nspname = 'specification'
ORDER BY c.relname, con.conname;
```

## Columns

>schema_name → schema name (e.g. specification)

>table_name → table name

>constraint_name → constraint name

>definition → SQL definition

## When to use

- before renaming constraints
- after schema changes
- debugging insert/update errors
- quick schema inspection

## Notes

Uses PostgreSQL system catalogs:

pg_constraint
pg_class
pg_namespace

Not standard SQL → PostgreSQL only