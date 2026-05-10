SET search_path TO specification;

SELECT conname,
       contype,
       conrelid::regclass AS tableName
FROM pg_constraint
WHERE conrelid = 'specification.holder_type'::regclass;

SELECT conname,
       contype,
       conrelid::regclass AS tableName
FROM pg_constraint
WHERE conrelid = 'specification.holder_master'::regclass;

SELECT conname,
       contype,
       conrelid::regclass AS tableName
FROM pg_constraint
WHERE conrelid = 'specification.holder_type_mounting_option'::regclass;

SELECT conname,
       contype,
       conrelid::regclass AS tableName
FROM pg_constraint
WHERE conrelid = 'specification.holder_mounting_feature_requirement'::regclass;
