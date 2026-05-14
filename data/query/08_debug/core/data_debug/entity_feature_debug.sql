SET search_path TO data_debug;

CREATE VIEW data_debug.v_entity_feature_debug AS
SELECT
    ef.id AS entity_feature_id,
    ef.entity_type AS entity_type,
    fd.name AS feature_name,
    ef.role AS definition_role,
    fd.unit AS unit,
    fd.data_type AS data_type
FROM core.entity_feature ef
         JOIN core.feature_definition fd
              ON fd.id = ef.feature_id
ORDER BY ef.entity_type ASC ;