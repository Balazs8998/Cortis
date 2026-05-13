SET search_path TO core;

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
  AND tc.constraint_schema = 'core'
  AND tc.table_name = 'entity_feature'
GROUP BY
    tc.table_name,
    tc.constraint_name,
    tc.constraint_type
ORDER BY
    tc.constraint_name;


