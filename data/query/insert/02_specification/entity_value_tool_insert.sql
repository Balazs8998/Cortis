SET search_path TO specification;

INSERT INTO specification.entity_feature_value
(entity_id, entity_type, entity_feature_id, value_number, value_text)
SELECT
    tm.id,
    v.entity_type,
    ef.id,
    v.value_number::numeric,
    v.value_text
FROM (
         VALUES
             -- DCMT-02-11 insert
             ('tool_master','DCMT-02-11', 'tolerance_class', NULL, 'M'),
             ('tool_master','DCMT-02-11', 'mounting_type',   NULL, 'T'),
             ('tool_master','DCMT-02-11', 'shape_size',      11,   NULL),
             ('tool_master','DCMT-02-11', 'insert_shape',    NULL, 'D'),
             ('tool_master','DCMT-02-11', 'relief_angle',    NULL,    'C'),

             -- DRILL-10 drill
             ('tool_master','DRILL-10', 'outer_diameter', 10,  NULL),
             ('tool_master','DRILL-10', 'length',         120, NULL),

             -- 16 mm weldon drill
             ('tool_master','DRILL-WD-16', 'outer_diameter', 16,  NULL),
             ('tool_master','DRILL-WD-16', 'length',         160, NULL),

             -- Iscar 3 mm grooving insert
             ('tool_master','GROOVE-3-ISCAR', 'manufacturer',  NULL, 'Iscar'),
             ('tool_master','GROOVE-3-ISCAR', 'mounting_code', NULL, 'TangGrip'),
             ('tool_master','GROOVE-3-ISCAR', 'shape_width',   3,    NULL),

             -- R105 boring bar screw system
             ('tool_master','R105.1819.005.1.4', 'size',          12,   NULL),
             ('tool_master','R105.1819.005.1.4', 'mounting_code', NULL, 'R105'),
             ('tool_master','R105.1819.005.1.4', 'manufacturer',  NULL, 'Sandvik'),

             -- Sandvik boring bar nut system
             ('tool_master','BB-NUT-12', 'outer_diameter', 20,   NULL),
             ('tool_master','BB-NUT-12', 'manufacturer',   NULL, 'Sandvik')
     ) AS v(entity_type, manufacturer_code,  feature_name, value_number, value_text)

         JOIN specification.tool_master tm
              ON tm.manufacturer_code = v.manufacturer_code

         JOIN core.feature_definition fd
              ON fd.name = v.feature_name

         JOIN core.entity_feature ef
              ON ef.feature_id = fd.id
                  AND ef.entity_type = 'tool'::core.entity_type
   AND ef.role = 'mounting'::core.role_type

ON CONFLICT (entity_id, entity_type, entity_feature_id) DO NOTHING;