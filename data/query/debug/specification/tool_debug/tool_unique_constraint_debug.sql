SET search_path TO specification;

SELECT -- Written with notes and AI assistance.
       tc.table_name AS tableName,
       kcu.column_name AS columnName,
       tc.constraint_type AS constraintType,
       tc.constraint_name AS constraintName
FROM information_schema.table_constraints tc
         JOIN information_schema.key_column_usage kcu ON kcu.constraint_name = tc.constraint_name
    AND kcu.constraint_schema = tc.constraint_schema
WHERE tc.constraint_type = 'UNIQUE'
  AND tc.constraint_schema = 'specification'
  AND tc.table_name = 'tool_type';

SELECT --Create with AI
       tc.table_name AS tableName,
       tc.constraint_name AS constraintName,
       tc.constraint_type AS constraintType,
       string_agg(
               kcu.column_name,
               ', '
               ORDER BY kcu.ordinal_position
       ) AS uniqueColumns
FROM information_schema.table_constraints tc
         JOIN information_schema.key_column_usage kcu
              ON kcu.constraint_name = tc.constraint_name
                  AND kcu.constraint_schema = tc.constraint_schema
                  AND kcu.table_schema = tc.table_schema
                  AND kcu.table_name = tc.table_name
WHERE tc.constraint_type = 'UNIQUE'
  AND tc.constraint_schema = 'specification'
  AND tc.table_name = 'tool_type_mounting_option'
GROUP BY
    tc.table_name,
    tc.constraint_name,
    tc.constraint_type
ORDER BY
    tc.constraint_name;

