# interface_type_mounting_feature_requirement

## Purpose

Stores the required feature definitions associated with interface types.

Defines which features are required for compatibility validation for a given interface type.

## Structure

| Column | Type | Constraints | Description |
|---|---|---|---|
| id | UUID | PRIMARY KEY | unique row identifier |
| type_id | UUID | NOT NULL, FOREIGN KEY | referenced interface type |
| requirement_feature_id | UUID | NOT NULL, FOREIGN KEY | required feature definition |

## Column Roles

### id

Unique technical identifier for every record.  
Used for handling relationships and references.

### type_id

Reference to the interface type identifier.

Provides relationship to the `specification.interface_type` table.

### requirement_feature_id

Reference to the required feature identifier.

Defines which feature is required for the given interface type.

## Constraints

| Name | Type | Columns | Description |
|---|---|---|---|
| itmfr_interface_type_id_fkey | FOREIGN KEY | type_id | provides relationship to the `specification.interface_type` table |
| itmfr_entity_feature_id_fkey | FOREIGN KEY | requirement_feature_id | provides relationship to the `core.entity_feature` table |
| itmfr_type_id_requirement_feature_id_key | UNIQUE | type_id, requirement_feature_id | ensures that the same feature requirement can only exist once for an interface type |
