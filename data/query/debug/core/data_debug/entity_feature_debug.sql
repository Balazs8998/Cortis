SELECT
    ef.id,
    ef.entity_type,
    fd.name AS feature_name,
    ef.role,
    fd.unit,
    fd.data_type
FROM core.entity_feature ef
         JOIN core.feature_definition fd
              ON fd.id = ef.feature_id;