# tool_type

## Purpose

### Defines tool types.

---

## Structure

| Column | Type | Constraints | Description |
|---|---|---|---|
| id | UUID | PRIMARY KEY | unique row identifier |
| name | TEXT | NOT NULL, UNIQUE | tool type name |
| description | TEXT | - | tool type description |

---

## Column Roles

### id

Unique technical identifier for every record.  
Used for handling relationships and references.

### name

The name of the tool type.

### description

Description of the tool type.

---

## Constraints

| Name | Type | Columns | Description |
|---|---|---|---|
| tt_name_key | UNIQUE | name | ensures that the same tool type name can only exist once |