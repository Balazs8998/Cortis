SET search_path TO company;

SELECT -- Written with notes and AI assistance.
    tc.table_name AS tableName,
    kcu.column_name AS columnName,
    tc.constraint_type AS constraintType,
    tc.constraint_name AS constraintName
FROM information_schema.table_constraints tc
         JOIN information_schema.key_column_usage kcu ON kcu.constraint_name = tc.constraint_name
    AND kcu.constraint_schema = tc.constraint_schema
WHERE tc.constraint_type = 'UNIQUE'
  AND tc.constraint_schema = 'company'
  AND tc.table_name = 'tool_inventory';

SELECT -- Written with notes and AI assistance.
       tc.table_name AS tableName,
       kcu.column_name AS columnName,
       tc.constraint_type AS constraintType,
       tc.constraint_name AS constraintName
FROM information_schema.table_constraints tc
         JOIN information_schema.key_column_usage kcu ON kcu.constraint_name = tc.constraint_name
    AND kcu.constraint_schema = tc.constraint_schema
WHERE tc.constraint_type = 'UNIQUE'
  AND tc.constraint_schema = 'company'
  AND tc.table_name = 'interface_inventory';

SELECT -- Written with notes and AI assistance.
       tc.table_name AS tableName,
       kcu.column_name AS columnName,
       tc.constraint_type AS constraintType,
       tc.constraint_name AS constraintName
FROM information_schema.table_constraints tc
         JOIN information_schema.key_column_usage kcu ON kcu.constraint_name = tc.constraint_name
    AND kcu.constraint_schema = tc.constraint_schema
WHERE tc.constraint_type = 'UNIQUE'
  AND tc.constraint_schema = 'company'
  AND tc.table_name = 'holder_inventory';

SELECT -- Written with notes and AI assistance.
       tc.table_name AS tableName,
       kcu.column_name AS columnName,
       tc.constraint_type AS constraintType,
       tc.constraint_name AS constraintName
FROM information_schema.table_constraints tc
         JOIN information_schema.key_column_usage kcu ON kcu.constraint_name = tc.constraint_name
    AND kcu.constraint_schema = tc.constraint_schema
WHERE tc.constraint_type = 'UNIQUE'
  AND tc.constraint_schema = 'company'
  AND tc.table_name = 'machine_inventory';
