# interface_master

## Purpose

The central table of the interface entity.  
Acts as a central reference point to ensure referential integrity between data.  
Additionally stores general information about the interface.

## Structure

| Column | Type | Constraints | Description |
|---|---|---|---|
| id | UUID | PRIMARY KEY | unique row identifier |
| manufacturer_code | TEXT | NOT NULL | manufacturer identifier |
| type_id | UUID | NOT NULL, FOREIGN KEY | referenced interface type |
| manufacturer | TEXT | NOT NULL | manufacturer name |
| description | TEXT | - | interface description |
| order_code | TEXT | - | ordering identifier |
| link | TEXT | - | external reference |
| catalog | TEXT | - | catalog information |

## Column Roles

### id

Unique technical identifier for every record.  
Used for handling relationships and references.

### manufacturer_code

The interface identifier provided by the manufacturer.

### type_id

Reference to the interface type identifier.

Provides relationship to the `specification.interface_type` table.

### manufacturer

The name of the interface manufacturer.

### description

Additional description or note about the interface.

### order_code

The ordering identifier used by the manufacturer.

### link

Direct link to the manufacturer’s product page.

### catalog

Manufacturer catalog identifier or page number.

## Notes

In the future, the `manufacturer`, `order_code`, `link`, and `catalog` fields may be moved into separate tables for normalization purposes.

## Constraints

| Name | Type | Columns | Description |
|---|---|---|---|
| im_interface_type_id_fkey | FOREIGN KEY | type_id | provides relationship to the `specification.interface_type` table |