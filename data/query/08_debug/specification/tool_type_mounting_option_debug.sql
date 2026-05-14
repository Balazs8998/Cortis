SELECT
    tt.name AS tool_type,
    ttmo.id AS mounting_option_id,
    ttmo.option_name
FROM specification.tool_type_mounting_option ttmo
         JOIN specification.tool_type tt
              ON tt.id = ttmo.type_id
ORDER BY ttmo.option_name;