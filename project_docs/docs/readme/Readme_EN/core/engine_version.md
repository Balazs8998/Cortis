# engine_version

## Purpose

Tracks system module versions and release states.

---

## Structure

| Column | Type | Constraints | Description |
|---|---|---|---|
| module | TEXT | NOT NULL, PRIMARY KEY | module name |
| version | TEXT | NOT NULL, PRIMARY KEY | version number |
| release_date | TIMESTAMPTZ | NOT NULL | release timestamp |
| deployed_date | TIMESTAMPTZ | - | deployment timestamp |
| is_stable | BOOLEAN | NOT NULL | stable release indicator |
| description | TEXT | - | version description |

---

## Column Roles

### module

Defines which system module the version belongs to.

For example: core, tool, machine, order.

### version

The version number of the module.

For example: 0.0.1, 1.2.0, 2.0.0.

### release_date

The timestamp when the version was created or released.

### deployed_date

Defines when the version was actually deployed or put into use.

### is_stable

Indicates whether the version is considered a stable release.

### description

Additional information or change description for the version.

---

## Constraints

| Name | Type | Columns | Description |
|---|---|---|---|
| ev_pkey | PRIMARY KEY | module, version | ensures that the same version can only exist once within a module |