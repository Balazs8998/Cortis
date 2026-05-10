SET search_path TO specification;

SELECT column_name,
       data_type,
       is_nullable,
       column_default
FROM information_schema.columns
WHERE table_schema = 'specification'
  AND table_name = 'machine_type';

SELECT column_name,
       data_type,
       is_nullable,
       column_default
FROM information_schema.columns
WHERE table_schema = 'specification'
  AND table_name = 'machine_master';

SELECT column_name,
       data_type,
       is_nullable,
       column_default
FROM information_schema.columns
WHERE table_schema = 'specification'
  AND table_name = 'machine_tool_station_category';

SELECT column_name,
       data_type,
       is_nullable,
       column_default
FROM information_schema.columns
WHERE table_schema = 'specification'
  AND table_name = 'machine_station_feature_requirement';
