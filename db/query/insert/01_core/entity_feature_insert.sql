INSERT INTO core.entity_feature (entity_type, feature_id, role)
SELECT
    m.entity_type::core.entity_type,
    f.id,
    m.role::core.role_type
FROM (
         VALUES
             -- geometry: tool / holder / interface
             ('tool', 'height', 'geometry'),
             ('tool', 'depth', 'geometry'),
             ('tool', 'radius', 'geometry'),
             ('tool', 'inner_diameter', 'geometry'),
             ('tool', 'outer_diameter', 'geometry'),


             ('holder', 'height', 'geometry'),
             ('holder', 'depth', 'geometry'),
             ('holder', 'radius', 'geometry'),
             ('holder', 'inner_diameter', 'geometry'),
             ('holder', 'outer_diameter', 'geometry'),

             ('interface', 'height', 'geometry'),
             ('interface', 'depth', 'geometry'),
             ('interface', 'radius', 'geometry'),
             ('interface', 'inner_diameter', 'geometry'),
             ('interface', 'outer_diameter', 'geometry'),

             -- mounting: tool / holder / interface
             ('tool', 'connection_type', 'mounting'),
             ('tool', 'interface_type', 'mounting'),
             ('tool', 'taper_angle', 'mounting'),
             ('tool', 'thread_size', 'mounting'),
             ('tool', 'shank_diameter', 'mounting'),
             ('tool', 'shank_length', 'mounting'),
             ('tool', 'mounting_length', 'mounting'),
             ('tool', 'outer_diameter', 'mounting'),


             ('holder', 'connection_type', 'mounting'),
             ('holder', 'interface_type', 'mounting'),
             ('holder', 'taper_angle', 'mounting'),
             ('holder', 'thread_size', 'mounting'),
             ('holder', 'shank_diameter', 'mounting'),
             ('holder', 'shank_length', 'mounting'),
             ('holder', 'mounting_length', 'mounting'),
             ('holder', 'outer_diameter', 'mounting'),
             ('holder', 'inner_diameter', 'mounting'),

             ('interface', 'connection_type', 'mounting'),
             ('interface', 'interface_type', 'mounting'),
             ('interface', 'taper_angle', 'mounting'),
             ('interface', 'thread_size', 'mounting'),
             ('interface', 'shank_diameter', 'mounting'),
             ('interface', 'shank_length', 'mounting'),
             ('interface', 'mounting_length', 'mounting'),
             ('interface', 'outer_diameter', 'mounting'),
             ('interface', 'inner_diameter', 'mounting'),



             -- machine_station constraints
             ('machine_station', 'max_tool_diameter', 'constraint'),
             ('machine_station', 'max_tool_length', 'constraint'),
             ('machine_station', 'max_holder_length', 'constraint'),
             ('machine_station', 'station_type', 'constraint'),
             ('machine_station', 'position_index', 'constraint'),

             -- insert / mounting features
             ('tool', 'insert_shape', 'mounting'),
             ('tool', 'relief_angle', 'mounting'),
             ('tool', 'tolerance_class', 'mounting'),
             ('tool', 'mounting_type', 'mounting'),

             ('holder', 'insert_shape', 'mounting'),
             ('holder', 'relief_angle', 'mounting'),
             ('holder', 'tolerance_class', 'mounting'),
             ('holder', 'mounting_type', 'mounting'),

             -- additional shared mounting features
             ('machine_station', 'inner_diameter', 'mounting'),
             ('machine_station', 'outer_diameter', 'mounting'),
             ('machine_station', 'mounting_length', 'mounting')
     ) AS m(entity_type, feature_name, role)
         JOIN core.feature_definition f
              ON f.name = m.feature_name
ON CONFLICT DO NOTHING;