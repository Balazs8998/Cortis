SET search_path TO specification;

SELECT -- Written with notes and AI assistance.
       tc.table_name      AS connectFromTable,
       tc.constraint_name AS constraintName,
       kcu.column_name    AS columnNameFromTable,
       ccu.table_name     AS connectToTable,
       ccu.column_name    AS columnNameToTable
FROM information_schema.table_constraints tc
         JOIN information_schema.key_column_usage kcu ON kcu.constraint_name = tc.constraint_name
    AND kcu.constraint_schema = tc.constraint_schema
         JOIN information_schema.constraint_column_usage ccu on kcu.constraint_name = ccu.constraint_name
    AND kcu.constraint_schema = ccu.constraint_schema
WHERE tc.constraint_type = 'FOREIGN KEY'
  AND tc.constraint_schema = 'specification'
  AND tc.table_name = 'machine_master';


SELECT -- Written with notes and AI assistance.
       tc.table_name      AS connectFromTable,
       tc.constraint_name AS constraintName,
       kcu.column_name    AS columnNameFromTable,
       ccu.table_name     AS connectToTable,
       ccu.column_name    AS columnNameToTable
FROM information_schema.table_constraints tc
         JOIN information_schema.key_column_usage kcu ON kcu.constraint_name = tc.constraint_name
    AND kcu.constraint_schema = tc.constraint_schema
         JOIN information_schema.constraint_column_usage ccu on kcu.constraint_name = ccu.constraint_name
    AND kcu.constraint_schema = ccu.constraint_schema
WHERE tc.constraint_type = 'FOREIGN KEY'
  AND tc.constraint_schema = 'specification'
  AND tc.table_name = 'machine_tool_station_category';


SELECT -- Written with notes and AI assistance.
       tc.table_name      AS connectFromTable,
       tc.constraint_name AS constraintName,
       kcu.column_name    AS columnNameFromTable,
       ccu.table_name     AS connectToTable,
       ccu.column_name    AS columnNameToTable
FROM information_schema.table_constraints tc
         JOIN information_schema.key_column_usage kcu ON kcu.constraint_name = tc.constraint_name
    AND kcu.constraint_schema = tc.constraint_schema
         JOIN information_schema.constraint_column_usage ccu on kcu.constraint_name = ccu.constraint_name
    AND kcu.constraint_schema = ccu.constraint_schema
WHERE tc.constraint_type = 'FOREIGN KEY'
  AND tc.constraint_schema = 'specification'
  AND tc.table_name = 'machine_station_feature_requirement';



