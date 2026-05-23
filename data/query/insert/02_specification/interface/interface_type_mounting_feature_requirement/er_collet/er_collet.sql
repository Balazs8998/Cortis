SET search_path TO specification;

INSERT INTO specification.interface_type_mounting_feature_requirement (type_id, requirement_feature_id, role)
SELECT
    it.id,
    ef.id,
    v.role
FROM (
         VALUES
             ('er_collet', 'inner_diameter','mounting'),
             ('er_collet', 'outer_diameter','clamping'),
             ('er_collet', 'mounting_length','clamping'),
             ('er_collet', 'taper_angle','clamping'),

             ('er_fix_tapping_collet', 'inner_diameter','mounting'),
             ('er_fix_tapping_collet', 'outer_diameter','clamping'),
             ('er_fix_tapping_collet', 'mounting_length','clamping'),
             ('er_fix_tapping_collet', 'taper_angle','clamping'),

             ('er_extension_tapping_collet', 'inner_diameter','mounting'),
             ('er_extension_tapping_collet', 'outer_diameter','clamping'),
             ('er_extension_tapping_collet', 'mounting_length','clamping'),
             ('er_extension_tapping_collet', 'taper_angle','clamping')

     ) AS v(type_name, requirement_name, role)
         JOIN specification.interface_type it ON v.type_name = it.name
         JOIN  core.feature_definition fd ON v.requirement_name = fd.name
         JOIN core.entity_feature ef ON fd.id = ef.feature_id
WHERE ef.role = 'mounting'
ON CONFLICT DO NOTHING ;