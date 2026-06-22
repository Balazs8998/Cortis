# machine_station_feature_requirement

## Purpose

Stores the required feature requirements associated with machine stations.

Defines which feature requirements a given machine station is compatible with.

## Structure

| Column | Type | Constraints | Description |
|---|---|---|---|
| id | UUID | PRIMARY KEY | unique row identifier |
| requirement_feature_id | UUID | NOT NULL, FOREIGN KEY | required feature definition |
| tool_station_id | UUID | NOT NULL, FOREIGN KEY | referenced machine station category |

## Column Roles

### id

Unique technical identifier for every record.  
Used for handling relationships and references.

### requirement_feature_id

Reference to the required feature identifier.

Defines which feature requirement is used by the given machine station.

### tool_station_id

Reference to the machine station category identifier.

Provides relationship to the `specification.machine_tool_station_category` table.

## Constraints

| Name | Type | Columns | Description |
|---|---|---|---|
| msfr_requirement_feature_id_fkey | FOREIGN KEY | requirement_feature_id | provides relationship to the `core.entity_feature` table |
| msfr_tool_station_id_fkey | FOREIGN KEY | tool_station_id | provides relationship to the `specification.machine_tool_station_category` table |