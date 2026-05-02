# PostgreSQL pg_catalg + info schema

## TABLES (like sqlite_master)

>Returns all user-defined tables (excludes PostgreSQL system schemas).

```sql
SELECT table_schema, table_name
FROM information_schema.tables
WHERE table_type = 'BASE TABLE'
AND table_schema NOT IN ('pg_catalog', 'information_schema');
```

## INDEXES (equivalent of sqlite_master index)

>Lists indexes for a schema, including their definition.


```sql
SELECT schemaname, tablename, indexname, indexdef
FROM pg_indexes
WHERE schemaname = 'specification';
```

## CONTRAINTS (ALL TYPES)

>Shows all constraints for a table (primary key, unique, foreign key, check).


```sql
SELECT
conname,
contype,
conrelid::regclass AS table_name
FROM pg_constraint
WHERE conrelid = 'specification.machine_master'::regclass;
```
### CONSTRAINT TYPES:

#### p = primary key
#### u = unique
#### f = foreign key
#### c = check

### Usage

##### table -> specification.machine_master
##### type -> p | u | f | c

### ONLY UNIQUE CONSTRAINTS

>Filters constraints to show only UNIQUE constraints for a table.


```sql
SELECT conname
FROM pg_constraint
WHERE contype = 'u'
  AND conrelid = 'specification.machine_master'::regclass;
```

## COLUMNS (like PRAGMA table_info)

>Lists all columns of a table with their data types.

```sql
SELECT column_name, data_type
FROM information_schema.columns
WHERE table_name = 'machine_master'
  AND table_schema = 'specification';
```
## Replace FK column

Used when an old FK column is replaced by a new FK column with different meaning.

Steps:
1. Add new FK column.
2. Add new foreign key constraint.
3. Check if the table contains data.
4. If empty, drop the old FK constraint and old column.
5. If not empty, migrate data first, then drop old FK and old column.

Note: Do not use rename if the new column has different business meaning.

## Foreign key naming

PostgreSQL auto-generates FK names if no constraint name is provided.
For CORTIS, FK constraints should be named manually.

Pattern:

`{table_short}_{column_name}_fkey`

Example:

`tmfr_mounting_option_id_fkey`

Example (CREATE TABLE):

```sql
create table tool_mounting_example
    id integer primary key, -- or uuid
    name text,
    mounting_option_id integer, -- or uuid

    CONSTRAINT tmfr_mounting_option_id_fkey
        FOREIGN KEY (mounting_option_id)
        REFERENCES specification.tool_type_mounting_option(id)
```

Example (ADD FOREIGN KEY):

```sql
ALTER TABLE specification.tool_mounting_feature_requirement
ADD CONSTRAINT tmfr_mounting_option_id_fkey
FOREIGN KEY (mounting_option_id)
REFERENCES specification.tool_type_mounting_option(id);
```

Example (RENAME): 

```sql
alter table specification.tool_mounting_feature_requirement
    rename constraint tool_mounting_feature_requirement_requirement_feature_id_fkey
    to tmfr_requirement_feature_id_fkey;
```