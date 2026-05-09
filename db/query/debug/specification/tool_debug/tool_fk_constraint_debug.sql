SET search_path TO specification;

SELECT --Create with AI
    conrelid::regclass  AS from_table,
    a.attname           AS from_column,
    confrelid::regclass AS to_table,
    af.attname          AS to_column,
    conname             AS constraint_name
FROM pg_constraint c
         JOIN unnest(c.conkey) WITH ORDINALITY AS fk(attnum, ord) ON true
         JOIN unnest(c.confkey) WITH ORDINALITY AS pk(attnum, ord) ON pk.ord = fk.ord
         JOIN pg_attribute a
              ON a.attrelid = c.conrelid
                  AND a.attnum = fk.attnum
         JOIN pg_attribute af
              ON af.attrelid = c.confrelid
                  AND af.attnum = pk.attnum
WHERE c.contype = 'f'
  AND c.connamespace = 'specification'::regnamespace
  AND c.conrelid = 'specification.tool_master'::regclass;


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
  AND tc.table_name = 'tool_type_mounting_option';


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
  AND tc.table_name = 'tool_mounting_feature_requirement';


