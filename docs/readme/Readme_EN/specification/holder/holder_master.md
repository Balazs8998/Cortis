# holder_master

## Purpose

The central table of the holder entity.

Acts as a central reference point to ensure referential integrity between data.

Additionally stores general information about the holder.

## Structure

| Column | Type | Constraints | Description |
|---|---|---|---|
| id | UUID | PRIMARY KEY | unique row identifier |
| manufacturer_code | TEXT | NOT NULL | manufacturer identifier |
| type_id | UUID | NOT NULL, FOREIGN KEY | referenced holder type |
| mounting_option_id | UUID | NOT NULL, FOREIGN KEY | referenced mounting option |
| manufacturer | TEXT | NOT NULL | manufacturer name |
| description | TEXT | - | holder description |
| link | TEXT | - | external reference |
| catalog | TEXT | - | catalog information |
| order_code | TEXT | - | ordering identifier |

## Column Roles

### id

Unique technical identifier for every record.  
Used for handling relationships and references.

### manufacturer_code

The holder identifier provided by the manufacturer.

### type_id

Reference to the holder type identifier.

Provides relationship to the `specification.holder_type` table.

### mounting_option_id

Reference to the holder mounting option.

Ensures that the holder is connected to the correct mounting requirements.

### manufacturer

The name of the holder manufacturer.

### description

Additional description or note about the holder.

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
| hm_mounting_option_id_fkey | FOREIGN KEY | type_id, mounting_option_id | ensures that the mounting option belongs to the given holder type |
| hm_holder_type_id_fkey | FOREIGN KEY | type_id | provides relationship to the `specification.holder_type` table |