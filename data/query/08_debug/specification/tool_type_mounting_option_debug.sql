SET search_path TO data_debug;

CREATE VIEW data_debug.v_tool_type_mounting_option_debug AS
SELECT
    tt.name AS tool_type,
    ttmo.id AS mounting_option_id,
    ttmo.option_name AS mounting_option
FROM specification.tool_type_mounting_option ttmo
         JOIN specification.tool_type tt
              ON tt.id = ttmo.type_id
ORDER BY tt.name ASC;