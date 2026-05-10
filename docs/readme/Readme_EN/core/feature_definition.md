# feature_definition

## Purpose

### Stores physical and technological feature definitions.

---

## Structure

| Column | Type | Constraints | Description |
|---|---|---|---|
| id | UUID | PRIMARY KEY | unique row identifier |
| name | TEXT | NOT NULL, UNIQUE | feature name |
| unit | TEXT | NOT NULL | unit of measurement |
| data_type | TEXT | NOT NULL | value data type |

---

## Column Roles

### id

Unique technical identifier for every record.  
Used for handling relationships and references.

### name

The name of the feature.  
For example: diameter, length, angle.

Uses a UNIQUE constraint to prevent duplicate feature definitions.

### unit

The unit of measurement of the feature.  
For example: mm, degree, rpm.

### data_type

Defines what type of value the feature stores.  
For example: text, number, boolean.