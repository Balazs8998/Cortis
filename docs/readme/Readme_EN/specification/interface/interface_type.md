# interface_type

## Purpose

Stores interface type definitions.

Defines the interface categories that can be used within the system.

## Structure

| Column | Type | Constraints | Description |
|---|---|---|---|
| id | UUID | PRIMARY KEY | unique row identifier |
| name | TEXT | NOT NULL, UNIQUE | interface type name |
| description | TEXT | - | interface type description |

## Column Roles

### id

Unique technical identifier for every record.  
Used for handling relationships and references.

### name

The name of the interface type.

For example: `er_collet`, `er_fix_taping_collet`, `er_extension_tapping_collet`

### description

Additional description or note about the interface type.

## Constraints

| Name | Type | Columns | Description |
|---|---|---|---|
| it_name_key | UNIQUE | name | ensures that the same interface type can only exist once |