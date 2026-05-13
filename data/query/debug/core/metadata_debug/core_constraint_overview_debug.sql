SET search_path TO core;

SELECT conname,
       contype,
       conrelid::regclass AS tableName
FROM pg_constraint
WHERE conrelid = 'core.engine_version'::regclass;

SELECT conname,
       contype,
       conrelid::regclass AS tableName
FROM pg_constraint
WHERE conrelid = 'core.feature_definition'::regclass;

SELECT conname,
       contype,
       conrelid::regclass AS tableName
FROM pg_constraint
WHERE conrelid = 'core.entity_feature'::regclass;
