# tool_inventory

## Purpose

Stores the company tool inventory.

Tracks the quantity and storage location of tools available in inventory, while providing relationship to specification data through the `tool_master` table.

## Structure

| Column | Type | Constraints | Description |
|---|---|---|---|
| id | UUID | PRIMARY KEY | unique row identifier |
| tool_code | TEXT | NOT NULL, UNIQUE | company tool identifier |
| name | TEXT | NOT NULL | tool name |
| quantity | INTEGER | NOT NULL, CHECK | inventory quantity |
| location | TEXT | NOT NULL | storage location |
| tool_master_id | UUID | NOT NULL, FOREIGN KEY | referenced tool master record |

## Column Roles

### id

Unique technical identifier for every record.  
Used for handling relationships and references.

### tool_code

The unique tool identifier used within the company.

### name

The name of the tool.

### quantity

The available inventory quantity.

### location

The warehouse or storage location of the tool.

### tool_master_id

Reference to the `specification.tool_master` record identifier.

Provides relationship to the specification data.

## Constraints

| Name | Type | Columns | Description |
|---|---|---|---|
| ti_tool_master_id_fkey | FOREIGN KEY | tool_master_id | provides relationship to the `specification.tool_master` table |
| ti_quantity_check | CHECK | quantity | ensures that the inventory quantity cannot be negative |
| ti_tool_code_key | UNIQUE | tool_code | ensures that the same tool identifier can only exist once |