# machine_tool_station_category

## Purpose

Stores machine tool station categories.

Defines which station types belong to a given machine type and whether the station is intended for handling interfaces or holders.

## Structure

| Column | Type | Constraints | Description |
|---|---|---|---|
| id | UUID | PRIMARY KEY | unique row identifier |
| type_id | UUID | NOT NULL, FOREIGN KEY | referenced machine type |
| station_type | core.machine_station_type | NOT NULL | station type |
| name | TEXT | NOT NULL | station category name |
| is_interface | BOOLEAN | NOT NULL | interface support indicator |
| is_holder | BOOLEAN | NOT NULL | holder support indicator |

## Column Roles

### id

Unique technical identifier for every record.  
Used for handling relationships and references.

### type_id

Reference to the machine type identifier.

Provides relationship to the `specification.machine_type` table.

### station_type

Defines the machine station type.

For example: `turret`, `magazine`, `spindle`

### name

The name of the station category.

### is_interface

Defines whether the station is capable of handling interface elements.

### is_holder

Defines whether the station is capable of handling holders.

## Constraints

| Name | Type | Columns | Description |
|---|---|---|---|
| mtsc_machine_type_id_fkey | FOREIGN KEY | type_id | provides relationship to the `specification.machine_type` table |
| mtsc_type_id_station_type_key | UNIQUE | type_id, station_type | ensures that the same station type can only exist once within a machine type |