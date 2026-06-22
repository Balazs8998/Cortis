# machine_type

## Purpose

Stores machine type definitions.

Defines the machine categories that can be used within the system.

## Structure

| Column | Type | Constraints | Description |
|---|---|---|---|
| id | UUID | PRIMARY KEY | unique row identifier |
| name | TEXT | NOT NULL, UNIQUE | machine type name |
| description | TEXT | - | machine type description |

## Column Roles

### id

Unique technical identifier for every record.  
Used for handling relationships and references.

### name

The name of the machine type.

For example: `lathe`, `milling_machine`, `grinding_machine`

### description

Additional description or note about the machine type.

## Constraints

| Name | Type | Columns | Description |
|---|---|---|---|
| mt_name_key | UNIQUE | name | ensures that the same machine type can only exist once |