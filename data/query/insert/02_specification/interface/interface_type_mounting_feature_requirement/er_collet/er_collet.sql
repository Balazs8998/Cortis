SET search_path TO specification;

INSERT INTO specification.interface_type_mounting_feature_requirement (type_id, requirement_feature_id, role)
SELECT
    it.id,
    ef.id,
    v.role
FROM (
         VALUES
             ('er_collet', 'inner_diameter_min','clamping'),
             ('er_collet', 'inner_diameter_max','clamping'),
             ('er_collet', 'outer_diameter','mounting'),
             ('er_collet', 'mounting_length','mounting'),
             ('er_collet', 'taper_angle','mounting'),

             ('er_fix_tapping_collet', 'inner_diameter_min','clamping'),
             ('er_fix_tapping_collet', 'inner_diameter_max','clamping'),
             ('er_fix_tapping_collet', 'outer_diameter','mounting'),
             ('er_fix_tapping_collet', 'mounting_length','mounting'),
             ('er_fix_tapping_collet', 'taper_angle','mounting'),

             ('er_extension_tapping_collet', 'inner_diameter_min','clamping'),
             ('er_extension_tapping_collet', 'inner_diameter_max','clamping'),
             ('er_extension_tapping_collet', 'outer_diameter','mounting'),
             ('er_extension_tapping_collet', 'mounting_length','mounting'),
             ('er_extension_tapping_collet', 'taper_angle','mounting')

     ) AS v(type_name, requirement_name, role)
         JOIN specification.interface_type it ON v.type_name = it.name
         JOIN  core.feature_definition fd ON v.requirement_name = fd.name
         JOIN core.entity_feature ef ON fd.id = ef.feature_id
WHERE ef.role = 'mounting'
ON CONFLICT DO NOTHING ;