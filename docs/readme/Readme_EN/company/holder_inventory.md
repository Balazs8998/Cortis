# holder_inventory

## Purpose

Stores the company holder inventory.

Tracks the quantity and storage location of holders available in inventory, while providing relationship to specification data through the `holder_master` table.

## Structure

| Column | Type | Constraints | Description |
|---|---|---|---|
| id | UUID | PRIMARY KEY | unique row identifier |
| holder_code | TEXT | NOT NULL, UNIQUE | company holder identifier |
| name | TEXT | NOT NULL | holder name |
| quantity | INTEGER | NOT NULL, CHECK | inventory quantity |
| location | TEXT | NOT NULL | storage location |
| holder_master_id | UUID | NOT NULL, FOREIGN KEY | referenced holder_master record |

## Column Roles

### id

Unique technical identifier for every record.  
Used for handling relationships and references.

### holder_code

The unique holder identifier used within the company.

### name

The name of the holder.

### quantity

The available inventory quantity.

### location

The warehouse or storage location of the holder.

### holder_master_id

Reference to the `specification.holder_master` record identifier.

Provides relationship to the specification data.

## Constraints

| Name | Type | Columns | Description |
|---|---|---|---|
| hi_holder_master_id_fkey | FOREIGN KEY | holder_master_id | provides relationship to the `specification.holder_master` table |
| hi_quantity_check | CHECK | quantity | ensures that the inventory quantity cannot be negative |
| hi_holder_code_key | UNIQUE | holder_code | ensures that the same holder identifier can only exist once |