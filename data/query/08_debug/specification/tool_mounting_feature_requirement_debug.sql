SET search_path TO data_debug;

CREATE  VIEW data_debug.v_tool_requirement_feature_option_debug AS
SELECT
    tmfr.id AS feat_requirement_id,
    tt.name AS type_name,
    ttmo.option_name AS mounting_option ,
    fd.name AS feature_def_naem,
    ef.role AS definition_role
FROM specification.tool_mounting_feature_requirement tmfr
JOIN specification.tool_type_mounting_option ttmo on tmfr.mounting_option_id = ttmo.id
JOIN specification.tool_type tt on ttmo.type_id = tt.id
JOIN core.entity_feature ef on tmfr.requirement_feature_id = ef.id
Join core.feature_definition fd on ef.feature_id = fd.id
ORDER BY tt.name ASC , ttmo.option_name ASC;

