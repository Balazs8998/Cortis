SET search_path TO specification;



INSERT INTO specification.entity_feature_value
(entity_id, entity_type, entity_feature_id, value_number, value_text)
SELECT
    im.id,
    v.entity_type,
    ef.id,
    v.value_number::numeric,
    v.value_text
FROM (
         VALUES

             -- er collet
             ('interface_master','ER16_D1.0-2.0',   'inner_diameter_min', '1.0',  NULL),
             ('interface_master','ER16_D1.0-2.0',   'inner_diameter_max', '2.0',  NULL),
             ('interface_master','ER16_D1.0-2.0',   'outer_diameter',     '17.0', NULL),
             ('interface_master','ER16_D1.0-2.0',   'mounting_length',    '27.5', NULL),
             ('interface_master','ER16_D1.0-2.0',   'taper_angle',        '16.0', NULL),

             ('interface_master','ER16_D2.0-3.0',   'inner_diameter_min', '2.0',  NULL),
             ('interface_master','ER16_D2.0-3.0',   'inner_diameter_max', '3.0',  NULL),
             ('interface_master','ER16_D2.0-3.0',   'outer_diameter',     '17.0', NULL),
             ('interface_master','ER16_D2.0-3.0',   'mounting_length',    '27.5', NULL),
             ('interface_master','ER16_D2.0-3.0',   'taper_angle',        '16.0', NULL),

             ('interface_master','ER16_D3.0-4.0',   'inner_diameter_min', '3.0',  NULL),
             ('interface_master','ER16_D3.0-4.0',   'inner_diameter_max', '4.0',  NULL),
             ('interface_master','ER16_D3.0-4.0',   'outer_diameter',     '17.0', NULL),
             ('interface_master','ER16_D3.0-4.0',   'mounting_length',    '27.5', NULL),
             ('interface_master','ER16_D3.0-4.0',   'taper_angle',        '16.0', NULL),

             ('interface_master','ER16_D4.0-5.0',   'inner_diameter_min', '4.0',  NULL),
             ('interface_master','ER16_D4.0-5.0',   'inner_diameter_max', '5.0',  NULL),
             ('interface_master','ER16_D4.0-5.0',   'outer_diameter',     '17.0', NULL),
             ('interface_master','ER16_D4.0-5.0',   'mounting_length',    '27.5', NULL),
             ('interface_master','ER16_D4.0-5.0',   'taper_angle',        '16.0', NULL),

             ('interface_master','ER16_D5.0-6.0',   'inner_diameter_min', '5.0',  NULL),
             ('interface_master','ER16_D5.0-6.0',   'inner_diameter_max', '6.0',  NULL),
             ('interface_master','ER16_D5.0-6.0',   'outer_diameter',     '17.0', NULL),
             ('interface_master','ER16_D5.0-6.0',   'mounting_length',    '27.5', NULL),
             ('interface_master','ER16_D5.0-6.0',   'taper_angle',        '16.0', NULL),

             ('interface_master','ER16_D6.0-7.0',   'inner_diameter_min', '6.0',  NULL),
             ('interface_master','ER16_D6.0-7.0',   'inner_diameter_max', '7.0',  NULL),
             ('interface_master','ER16_D6.0-7.0',   'outer_diameter',     '17.0', NULL),
             ('interface_master','ER16_D6.0-7.0',   'mounting_length',    '27.5', NULL),
             ('interface_master','ER16_D6.0-7.0',   'taper_angle',        '16.0', NULL),

             ('interface_master','ER16_D7.0-8.0',   'inner_diameter_min', '7.0',  NULL),
             ('interface_master','ER16_D7.0-8.0',   'inner_diameter_max', '8.0',  NULL),
             ('interface_master','ER16_D7.0-8.0',   'outer_diameter',     '17.0', NULL),
             ('interface_master','ER16_D7.0-8.0',   'mounting_length',    '27.5', NULL),
             ('interface_master','ER16_D7.0-8.0',   'taper_angle',        '16.0', NULL),

             ('interface_master','ER16_D8.0-9.0',   'inner_diameter_min', '8.0',  NULL),
             ('interface_master','ER16_D8.0-9.0',   'inner_diameter_max', '9.0',  NULL),
             ('interface_master','ER16_D8.0-9.0',   'outer_diameter',     '17.0', NULL),
             ('interface_master','ER16_D8.0-9.0',   'mounting_length',    '27.5', NULL),
             ('interface_master','ER16_D8.0-9.0',   'taper_angle',        '16.0', NULL),

             ('interface_master','ER16_D9.0-10.0',  'inner_diameter_min', '9.0',  NULL),
             ('interface_master','ER16_D9.0-10.0',  'inner_diameter_max', '10.0', NULL),
             ('interface_master','ER16_D9.0-10.0',  'outer_diameter',     '17.0', NULL),
             ('interface_master','ER16_D9.0-10.0',  'mounting_length',    '27.5', NULL),
             ('interface_master','ER16_D9.0-10.0',  'taper_angle',        '16.0', NULL),

             ('interface_master','ER16_D10.0-11.0', 'inner_diameter_min', '10.0', NULL),
             ('interface_master','ER16_D10.0-11.0', 'inner_diameter_max', '11.0', NULL),
             ('interface_master','ER16_D10.0-11.0', 'outer_diameter',     '17.0', NULL),
             ('interface_master','ER16_D10.0-11.0', 'mounting_length',    '27.5', NULL),
             ('interface_master','ER16_D10.0-11.0', 'taper_angle',        '16.0', NULL)

     ) AS v(entity_type, manufacturer_code, feature_name, value_number, value_text)

         JOIN specification.interface_master im
              ON im.manufacturer_code = v.manufacturer_code

         JOIN core.feature_definition fd
              ON fd.name = v.feature_name

         JOIN core.entity_feature ef
              ON ef.feature_id = fd.id
                  AND ef.entity_type = 'interface'::core.entity_type
                  AND ef.role = 'mounting'::core.role_type

ON CONFLICT (entity_id, entity_type, entity_feature_id) DO NOTHING;