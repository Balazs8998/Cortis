# machine_inventory

## Purpose

Stores the company machine inventory.

Tracks the location of machines used within the system, while providing relationship to specification data through the `machine_master` table.

## Structure

| Column | Type | Constraints | Description |
|---|---|---|---|
| id | UUID | PRIMARY KEY | unique row identifier |
| machine_code | TEXT | NOT NULL, UNIQUE | company machine identifier |
| name | TEXT | NOT NULL | machine name |
| location | TEXT | NOT NULL | machine location |
| machine_master_id | UUID | NOT NULL, FOREIGN KEY | referenced machine_master record |

## Column Roles

### id

Unique technical identifier for every record.  
Used for handling relationships and references.

### machine_code

The unique machine identifier used within the company.

### name

The name of the machine.

### location

The current location or installation position of the machine.

### machine_master_id

Reference to the `specification.machine_master` record identifier.

Provides relationship to the specification data.

## Constraints

| Name | Type | Columns | Description |
|---|---|---|---|
| mi_machine_master_id_fkey | FOREIGN KEY | machine_master_id | provides relationship to the `specification.machine_master` table |
| mi_machine_code_key | UNIQUE | machine_code | ensures that the same machine identifier can only exist once |