SET search_path TO specification;

SELECT conname,
       contype,
       conrelid::regclass AS tableName
FROM pg_constraint
WHERE conrelid = 'specification.machine_type'::regclass;

SELECT conname,
       contype,
       conrelid::regclass AS tableName
FROM pg_constraint
WHERE conrelid = 'specification.machine_master'::regclass;

SELECT conname,
       contype,
       conrelid::regclass AS tableName
FROM pg_constraint
WHERE conrelid = 'specification.machine_tool_station_category'::regclass;

SELECT conname,
       contype,
       conrelid::regclass AS tableName
FROM pg_constraint
WHERE conrelid = 'specification.machine_station_feature_requirement'::regclass;
