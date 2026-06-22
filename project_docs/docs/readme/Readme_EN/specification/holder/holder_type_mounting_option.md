# holder_type_mounting_option

## Purpose

Stores mounting options associated with holder types.

Defines how a specific holder type can connect to tools, interfaces, or other mounting elements based on mounting requirements.

## Structure

| Column | Type | Constraints | Description |
|---|---|---|---|
| id | UUID | PRIMARY KEY | unique row identifier |
| type_id | UUID | NOT NULL, FOREIGN KEY | referenced holder type |
| option_name | TEXT | NOT NULL | mounting option name |
| requirement_basis | core.holder_clamping_requirement | NOT NULL, UNIQUE | mounting requirement basis |

## Column Roles

### id

Unique technical identifier for every record.  
Used for handling relationships and references.

### type_id

Reference to the holder type identifier.

Provides relationship to the `specification.holder_type` table.

### option_name

The name of the mounting option.

For example: `collet_clamping`, `hydraulic_clamping`, `side_lock_clamping`

### requirement_basis

Defines the requirement basis used to interpret the mounting option.

For example: `round`, `square`

## Constraints

| Name | Type | Columns | Description |
|---|---|---|---|
| htmo_type_id_requirement_basis_key | UNIQUE | type_id, requirement_basis | ensures that the same requirement basis can only exist once for a holder type |
| htmo_type_id_id_key | UNIQUE | type_id, id | provides a composite reference base for other tables |
| htmo_holder_type_id_fkey | FOREIGN KEY | type_id | provides relationship to the `specification.holder_type` table |