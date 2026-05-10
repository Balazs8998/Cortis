# machine_master

## Purpose

The central table of the machine entity.

Acts as a central reference point to ensure referential integrity between data.

Additionally stores general information about the machine.

## Structure

| Column | Type | Constraints | Description |
|---|---|---|---|
| id | UUID | PRIMARY KEY | unique row identifier |
| manufacturer_code | TEXT | NOT NULL | manufacturer identifier |
| type_id | UUID | NOT NULL, FOREIGN KEY | referenced machine type |
| manufacturer | TEXT | NOT NULL | manufacturer name |
| description | TEXT | - | machine description |
| link | TEXT | - | external reference |
| catalog | TEXT | - | catalog information |
| order_code | TEXT | - | ordering identifier |

## Column Roles

### id

Unique technical identifier for every record.  
Used for handling relationships and references.

### manufacturer_code

The machine identifier provided by the manufacturer.

### type_id

Reference to the machine type identifier.

Provides relationship to the `specification.machine_type` table.

### manufacturer

The name of the machine manufacturer.

### description

Additional description or note about the machine.

### link

Direct link to the manufacturer’s product page.

### catalog

Manufacturer catalog identifier or page number.

### order_code

The ordering identifier used by the manufacturer.

## Notes

In the future, the `manufacturer`, `order_code`, `link`, and `catalog` fields may be moved into separate tables for normalization purposes.

## Constraints

| Name | Type | Columns | Description |
|---|---|---|---|
| mm_pkey | PRIMARY KEY | id | ensures a unique record identifier |
| mm_machine_type_id_fkey | FOREIGN KEY | type_id | provides relationship to the `specification.machine_type` table |