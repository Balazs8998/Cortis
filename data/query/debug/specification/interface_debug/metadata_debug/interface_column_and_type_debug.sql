SET search_path TO specification;

SELECT column_name,
       data_type,
       is_nullable,
       column_default
FROM information_schema.columns
WHERE table_schema = 'specification'
  AND table_name = 'interface_type';

SELECT column_name,
       data_type,
       is_nullable,
       column_default
FROM information_schema.columns
WHERE table_schema = 'specification'
  AND table_name = 'interface_master';

SELECT column_name,
       data_type,
       is_nullable,
       column_default
FROM information_schema.columns
WHERE table_schema = 'specification'
  AND table_name = 'interface_type_mounting_feature_requirement';
