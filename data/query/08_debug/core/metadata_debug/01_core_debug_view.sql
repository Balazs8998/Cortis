SET search_path TO metadata_debug;

REFRESH MATERIALIZED VIEW debug_table_structure_overview;

CREATE VIEW debug_column_constraint_details AS
WITH
    table_structure AS (
        SELECT
            table_name,
            column_name,
            data_type,
            is_nullable,
            column_default
        FROM information_schema.columns
        WHERE table_schema = 'core'
    ),
    constraint_fk AS (
        SELECT
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
          AND tc.constraint_schema = 'core'
    ),
    constraint_all AS(
        SELECT
               tc.table_name AS tableName,
               tc.constraint_name AS constraintName,
               tc.constraint_type AS constraintType,
               kcu.column_name
        FROM information_schema.table_constraints tc
                 JOIN information_schema.key_column_usage kcu
                      ON kcu.constraint_name = tc.constraint_name
                          AND kcu.constraint_schema = tc.constraint_schema
                          AND kcu.table_schema = tc.table_schema
                          AND kcu.table_name = tc.table_name
        WHERE tc.constraint_schema = 'core'
        ORDER BY
            tc.constraint_name
    )
SELECT
    ts.table_name AS tableName,
    ts.column_name AS columnName,
    ts.data_type AS dataType,
    ts.is_nullable AS isNull,
    ts.column_default AS columnDefault,
    cu.constraintName,
    cu.constraintType,
    cf.connectToTable,
    cf.columnNameToTable
    FROM table_structure ts
LEFT JOIN constraint_fk cf ON cf.connectFromTable = ts.table_name
AND cf.columnNameFromTable = ts.column_name
LEFT JOIN constraint_all cu ON cu.tableName = ts.table_name
AND  cu.column_name = ts.column_name
ORDER BY ts.table_name ASC ;


CREATE MATERIALIZED VIEW debug_table_structure_overview AS
WITH
    table_structure AS (
        SELECT
            table_name,
            column_name,
            data_type,
            is_nullable,
            column_default
        FROM information_schema.columns
        WHERE table_schema = 'core'
    ),

    constraint_details AS (
        SELECT
            tc.table_name,
            kcu.column_name,
            tc.constraint_name,
            tc.constraint_type,
            ccu.table_name AS referenced_table,
            ccu.column_name AS referenced_column
        FROM information_schema.table_constraints tc

                 JOIN information_schema.key_column_usage kcu
                      ON kcu.constraint_name = tc.constraint_name
                          AND kcu.constraint_schema = tc.constraint_schema
                          AND kcu.table_name = tc.table_name

                 LEFT JOIN information_schema.constraint_column_usage ccu
                           ON ccu.constraint_name = tc.constraint_name
                               AND ccu.constraint_schema = tc.constraint_schema

        WHERE tc.constraint_schema = 'core'
    ),
    -- Constraint aggregation created with AI-assisted development.
    aggregated_constraints AS (
        SELECT
            table_name,
            column_name,

            string_agg(
                    DISTINCT constraint_type,
                    ', '
                    ORDER BY constraint_type
            ) AS constraint_types,

            string_agg(
                    DISTINCT constraint_name,
                    ', '
                    ORDER BY constraint_name
            ) AS constraint_names,

            string_agg(
            DISTINCT referenced_table,
            ', '
            ORDER BY referenced_table
                      ) FILTER (
                WHERE referenced_table IS NOT NULL
                ) AS referenced_tables,

            string_agg(
            DISTINCT referenced_column,
            ', '
            ORDER BY referenced_column
                      ) FILTER (
                WHERE referenced_column IS NOT NULL
                ) AS referenced_columns

        FROM constraint_details

        GROUP BY
            table_name,
            column_name
    )

SELECT
    ts.table_name,
    ts.column_name,
    ts.data_type,
    ts.is_nullable,
    ts.column_default,

    ac.constraint_types,
    ac.constraint_names,
    ac.referenced_tables,
    ac.referenced_columns,

    CURRENT_TIMESTAMP AS snapshot_created_at

FROM table_structure ts

         LEFT JOIN aggregated_constraints ac
                   ON ac.table_name = ts.table_name
                       AND ac.column_name = ts.column_name

ORDER BY
    ts.table_name,
    ts.column_name;