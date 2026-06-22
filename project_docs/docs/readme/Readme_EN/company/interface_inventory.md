# interface_inventory

## Purpose

Stores the company interface inventory.

Tracks the quantity and storage location of interfaces available in inventory, while providing relationship to specification data through the `interface_master` table.

## Structure

| Column | Type | Constraints | Description |
|---|---|---|---|
| id | UUID | PRIMARY KEY | unique row identifier |
| interface_code | TEXT | NOT NULL, UNIQUE | company interface identifier |
| name | TEXT | NOT NULL | interface name |
| quantity | INTEGER | NOT NULL, CHECK | inventory quantity |
| location | TEXT | NOT NULL | storage location |
| interface_master_id | UUID | NOT NULL, FOREIGN KEY | referenced interface_master record |

## Column Roles

### id

Unique technical identifier for every record.  
Used for handling relationships and references.

### interface_code

The unique interface identifier used within the company.

### name

The name of the interface.

### quantity

The available inventory quantity.

### location

The warehouse or storage location of the interface.

### interface_master_id

Reference to the `specification.interface_master` record identifier.

Provides relationship to the specification data.

## Constraints

| Name | Type | Columns | Description |
|---|---|---|---|
| ii_interface_master_id_fkey | FOREIGN KEY | interface_master_id | provides relationship to the `specification.interface_master` table |
| ii_quantity_check | CHECK | quantity | ensures that the inventory quantity cannot be negative |
| ii_interface_code_key | UNIQUE | interface_code | ensures that the same interface identifier can only exist once |