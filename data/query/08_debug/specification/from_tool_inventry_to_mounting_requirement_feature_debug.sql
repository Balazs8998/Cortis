SET search_path TO data_debug;

CREATE VIEW data_debug.v_tool_inventory_mounting_requirements_debug AS
SELECT ti.name          AS tool_name,
       tm.id            AS tool_master_id,
       tt.name          AS type_name,
       ttmo.option_name AS mounting_option,
       fd.name          AS feature_def_name,
       ef.role          AS definition_role,
       tmfr.id          AS feat_requirement_id,
       ef.id            AS etity_feature_id
FROM company.tool_inventory ti
         JOIN specification.tool_master tm ON ti.tool_master_id = tm.id
         JOIN specification.tool_type tt ON tm.type_id = tt.id
         JOIN specification.tool_type_mounting_option ttmo
              ON tm.type_id = ttmo.type_id and tm.mounting_option_id = ttmo.id
         JOIN specification.tool_mounting_feature_requirement tmfr ON ttmo.id = tmfr.mounting_option_id
         JOIN core.entity_feature ef ON tmfr.requirement_feature_id = ef.id
         JOIN core.feature_definition fd ON ef.feature_id = fd.id
ORDER BY ti.name ASC, ttmo.option_name ASC;