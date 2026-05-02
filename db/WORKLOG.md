## 2026-05-03

### Current task
Normalize constraint names

### Last table
specification.tool_master

### Done
- FK renamed to tm_tool_type_fkey
- UNIQUE checked
- PK OK

### Next
specification.entity_feature

### Notes
- use pattern: {table_short}_{column}_fkey
- always rename, no drop/add

```sql
SELECT
  conname,
  contype,
  conrelid::regclass AS table_name
  FROM pg_constraint
  WHERE conrelid = 'specification.tool_mounting_feature_requirement'::regclass;

alter table specification.tool_master
rename constraint tool_master_type_id_fkey
to tm_tool_type_fkey;
```

## STOPPED AT: specification.tool

### Not done
- Update DDL to use explicit constraint names (PK, FK, UNIQUE)