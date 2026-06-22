# holder_type

## Purpose

Stores holder type definitions.

Defines the holder categories that can be used within the system.

## Structure

| Column | Type | Constraints | Description |
|---|---|---|---|
| id | UUID | PRIMARY KEY | unique row identifier |
| name | TEXT | NOT NULL, UNIQUE | holder type name |
| is_interface | BOOLEAN | NOT NULL | interface type indicator |
| description | TEXT | - | holder type description |

## Column Roles

### id

Unique technical identifier for every record.  
Used for handling relationships and references.

### name

The name of the holder type.

For example: `collet_holder`, `graf_holder`, `toolshank`

### is_interface

Defines whether the holder requires an interface.

### description

Additional description or note about the holder type.

## Constraints

| Name | Type | Columns | Description |
|---|---|---|---|
| ht_name_key | UNIQUE | name | ensures that the same holder type can only exist once |