SELECT tm.code,

       MAX(efv.value_text) FILTER (WHERE fd.name = 'insert_shape')        AS insert_shape,
    MAX(efv.value_text) FILTER (WHERE fd.name = 'relief_angle')        AS relief_angle,
    MAX(efv.value_text) FILTER (WHERE fd.name = 'tolerance_class')     AS tolerance_class,
    MAX(efv.value_text) FILTER (WHERE fd.name = 'mounting_type')       AS mounting_type,

    MAX(efv.value_number) FILTER (WHERE fd.name = 'insert_shape_size') AS insert_shape_size,
    MAX(ef.role)                                                       AS role
FROM specification.entity_feature_value efv
         JOIN specification.tool_master tm
              ON tm.id = efv.entity_id
         JOIN core.entity_feature ef
              ON ef.id = efv.entity_feature_id
         JOIN core.feature_definition fd
              ON fd.id = ef.feature_id

WHERE efv.entity_id = 'b485d55a-cabd-4218-8bda-469a624f608d'

GROUP BY tm.code;