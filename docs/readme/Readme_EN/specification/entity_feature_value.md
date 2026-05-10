# entity_feature_value

## Purpose

### Stores feature values assigned to entities.

---

## Structure

| Column | Type | Constraints | Description |
|---|---|---|---|
| id | UUID | PRIMARY KEY | unique row identifier |
| entity_id | UUID | NOT NULL | referenced entity identifier |
| entity_feature_id | UUID | NOT NULL, FOREIGN KEY | referenced entity_feature identifier |
| value_number | NUMERIC | - | numeric feature value |
| value_text | TEXT | - | textual feature value |

---

## Column Roles

### id

Unique technical identifier for every record.  
Used for handling relationships and references.

### entity_id

Reference to the entity identifier.  
Ensures that the feature value is connected to a specific entity.

### entity_feature_id

Reference to the entity_feature record identifier.  
Defines which feature definition the given value belongs to.

### value_number

Stores numeric feature values.

### value_text

Stores textual feature values.

---

## Constraints

| Name | Type | Columns | Description |
|---|---|---|---|
| efv_pkey | PRIMARY KEY | id | ensures a unique record identifier |
| efv_text_or_number_check | CHECK | value_text, value_number | ensures that at least one value field is filled |
| efv_entity_id_entity_feature_id_key | UNIQUE | entity_id, entity_feature_id | ensures that an entity can only be connected once to the same feature |
| efv_entity_feature_id_fkey | FOREIGN KEY | entity_feature_id | provides relationship to the core.entity_feature table |