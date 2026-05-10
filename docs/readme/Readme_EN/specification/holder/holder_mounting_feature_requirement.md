# holder_mounting_feature_requirement

## Purpose

Stores the required feature requirements associated with holder mounting options.

Defines which features are required for compatibility validation within a given mounting option.

## Structure

| Column | Type | Constraints | Description |
|---|---|---|---|
| id | UUID | PRIMARY KEY | unique row identifier |
| mounting_option_id | UUID | NOT NULL, FOREIGN KEY | referenced mounting option |
| requirement_feature_id | UUID | NOT NULL, FOREIGN KEY | required feature definition |

## Column Roles

### id

Unique technical identifier for every record.  
Used for handling relationships and references.

### mounting_option_id

Reference to the holder mounting option.

Provides relationship to the `specification.holder_type_mounting_option` table.

### requirement_feature_id

Reference to the required feature identifier.

Defines which feature is required for the given mounting option.

## Constraints

| Name | Type | Columns | Description |
|---|---|---|---|
| htmfr_mounting_option_id_requirement_feature_id_key | UNIQUE | mounting_option_id, requirement_feature_id | ensures that the same feature requirement can only exist once within a mounting option |
| htmfr_holder_type_id_fkey | FOREIGN KEY | mounting_option_id | provides relationship to the `specification.holder_type_mounting_option` table |
| htmfr_requirement_feature_id_fkey | FOREIGN KEY | requirement_feature_id | provides relationship to the `core.entity_feature` table |