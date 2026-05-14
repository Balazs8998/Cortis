SET search_path TO data_debug;

CREATE VIEW data_debug.v_tool_inventory_missing_feature_value AS
SELECT ti.name          AS tool_name,
       tm.id            AS tool_master_id,
       tt.name          AS type_name,
       ttmo.option_name AS mounting_option,
       fd.name          AS feature_def_name,
       ef.role          AS definition_role,
       efv.value_text   AS value_text,
       efv.value_number AS value_number
FROM company.tool_inventory ti
         JOIN specification.tool_master tm ON ti.tool_master_id = tm.id
         JOIN specification.tool_type tt ON tm.type_id = tt.id
         JOIN specification.tool_type_mounting_option ttmo
              ON tm.type_id = ttmo.type_id and tm.mounting_option_id = ttmo.id
         JOIN specification.tool_mounting_feature_requirement tmfr ON ttmo.id = tmfr.mounting_option_id
         JOIN core.entity_feature ef ON tmfr.requirement_feature_id = ef.id
         JOIN core.feature_definition fd ON ef.feature_id = fd.id
         LEFT JOIN specification.entity_feature_value efv
                   ON efv.entity_id = tm.id
                       AND ef.id = efv.entity_feature_id
WHERE efv.id IS NULL
ORDER BY ti.name ASC, ttmo.option_name ASC;