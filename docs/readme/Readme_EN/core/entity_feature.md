# entity_feature

## Purpose

### Connects feature definitions to entity types and feature roles.

---

## Structure

| Column | Type | Constraints | Description |
|---|---|---|---|
| id | UUID | PRIMARY KEY | unique row identifier |
| entity_type | core.entity_type | NOT NULL | entity type |
| feature_id | UUID | NOT NULL, FOREIGN KEY | referenced feature definition |
| role | core.role_type | NOT NULL | feature role |

---

## Column Roles

### id

Unique technical identifier for every record.  
Used for handling relationships and references.

### entity_type

Defines which entity type the feature belongs to.  
For example: interface, tool, holder, machine, machine_station.

### feature_id

FOREIGN KEY referencing the id column of the feature_definition table.  
Defines the related feature definition.

### role

Defines the role of the given feature.  
For example: mounting, geometry, constraint.

---

## Constraints

| Name | Type | Columns | Description |
|---|---|---|---|
| ef_feature_id_entity_type_role_key | UNIQUE | feature_id, entity_type, role | prevents duplicate feature role assignments |