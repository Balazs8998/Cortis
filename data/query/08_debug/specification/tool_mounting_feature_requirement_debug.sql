
--CREATE MATERIALIZED VIEW IF NOT EXISTS tool_requirement_feature_option_debug AS
SELECT
    tmfr.id,
    tt.name,
    ttmo.option_name,
    fd.name,
    ef.role
FROM specification.tool_mounting_feature_requirement tmfr
JOIN specification.tool_type_mounting_option ttmo on tmfr.mounting_option_id = ttmo.id
JOIN specification.tool_type tt on ttmo.type_id = tt.id
JOIN core.entity_feature ef on tmfr.requirement_feature_id = ef.id
Join core.feature_definition fd on ef.feature_id = fd.id
ORDER BY tt.name ASC , ttmo.option_name ASC;

