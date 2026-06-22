# tool_master

## Purpose

The central table of the tool entity.  
Acts as a central reference point to ensure referential integrity between data.  
Additionally stores general information about the tool.

---

## Structure

| Column | Type | Constraints | Description |
|---|---|---|---|
| id | UUID | PRIMARY KEY | unique row identifier |
| manufacturer_code | TEXT | NOT NULL | manufacturer identifier |
| type_id | UUID | NOT NULL, FOREIGN KEY | referenced tool type |
| mounting_option_id | UUID | NOT NULL, FOREIGN KEY | referenced mounting option |
| manufacturer | TEXT | NOT NULL | manufacturer name |
| description | TEXT | - | tool description |
| order_code | TEXT | - | ordering identifier |
| link | TEXT | - | external reference |
| catalog | TEXT | - | catalog information |

---

## Column Roles

### id

Unique technical identifier for every record.  
Used for handling relationships and references.

### manufacturer_code

The tool identifier provided by the manufacturer.

### type_id

Reference to the tool type identifier.  
Provides relationship to the `specification.tool_type` table, which defines the tool type.

### mounting_option_id

Reference to the tool mounting option.  
Ensures that the tool is connected to the correct mounting requirements.

### manufacturer

The name of the tool manufacturer.

### description

Additional description or note about the tool.

### order_code

The ordering identifier used by the manufacturer.

### link

Direct link to the manufacturer’s product page.

### catalog

Manufacturer catalog identifier or page number.

## Notes

In the future, the `manufacturer`, `order_code`, `link`, and `catalog` fields may be moved into separate tables for normalization purposes.

---

## Constraints

| Name | Type | Columns | Description |
|---|---|---|---|
| tm_mounting_option_id_fkey | FOREIGN KEY | type_id, mounting_option_id | ensures that the mounting option belongs to the given tool type |
| tm_tool_type_id_fkey | FOREIGN KEY | type_id | provides relationship to the `specification.tool_type` table |