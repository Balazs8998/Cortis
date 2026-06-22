# tool_mounting_feature_requirement

## Purpose

Stores the required feature requirements associated with tool mounting options.

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

Reference to the tool mounting option.

Provides relationship to the `specification.tool_type_mounting_option` table.

### requirement_feature_id

Reference to the required feature identifier.

Defines which feature is required for the given mounting option.

## Constraints

| Name | Type | Columns | Description |
|---|---|---|---|
| tmfr_mounting_option_id_requirement_feature_id_key | UNIQUE | mounting_option_id, requirement_feature_id | ensures that the same feature requirement can only exist once within a mounting option |
| tmfr_tool_mounting_feature_requirement_id_fkey | FOREIGN KEY | mounting_option_id | provides relationship to the `specification.tool_type_mounting_option` table |
| tmfr_entity_feature_id_fkey | FOREIGN KEY | requirement_feature_id | provides relationship to the `core.entity_feature` table |