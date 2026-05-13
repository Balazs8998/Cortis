Added two new schema debugging views to the `core` schema:

* `debug_table_structure_overview` (materialized view)
  Provides an aggregated, human-readable column overview with summarized constraint metadata for fast schema inspection and internal debugging.

* `debug_column_constraint_details` (standard view)
  Provides detailed constraint-level inspection for precise relation and constraint analysis.

The same debugging and metadata inspection structure is planned for the remaining schemas in the future.
--------------------------------------

### Populate tool tables with data.

## Tomorrow: Review and debug the requirement tables.Something is not correct there yet.