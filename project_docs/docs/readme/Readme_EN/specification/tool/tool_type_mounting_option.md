# tool_type_mounting_option

## Purpose

Stores mounting options associated with tool types.

Defines how a specific tool type can connect to holders, interfaces, or other mounting elements based on mounting requirements.

## Structure

| Column | Type | Constraints | Description |
|---|---|---|---|
| id | UUID | PRIMARY KEY | unique row identifier |
| type_id | UUID | NOT NULL, FOREIGN KEY | referenced tool type |
| option_name | TEXT | NOT NULL | mounting option name |
| requirement_basis | core.tool_mounting_requirement | NOT NULL, UNIQUE | mounting requirement basis |

## Column Roles

### id

Unique technical identifier for every record.  
Used for handling relationships and references.

### type_id

Reference to the tool type identifier.

Provides relationship to the `specification.tool_type` table.

### option_name

The name of the mounting option.

For example: `turning_iso_standard`, `grooving_system`

### requirement_basis

Defines the requirement basis used to interpret the mounting option.

For example: `geometry`, `form`, `form_and_geometry`

### Note:
A mounting option can contain multiple feature requirements.

## Constraints

| Name | Type | Columns | Description |
|---|---|---|---|
| ttmo_type_id_requirement_basis_key | UNIQUE | type_id, requirement_basis | ensures that the same requirement basis can only exist once for a tool type |
| ttmo_type_id_id_key | UNIQUE | type_id, id | provides a composite reference base for other tables |
| ttmo_tool_type_id_fkey | FOREIGN KEY | type_id | provides relationship to the `specification.tool_type` table |