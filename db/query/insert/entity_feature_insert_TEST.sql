INSERT INTO core.entity_feature (entity_type, feature_id, role)
SELECT e.entity_type::core.entity_type, f.id, 'geometry'::core.role_type
FROM core.feature_definition f
         CROSS JOIN (VALUES
                         ('tool'),
                         ('holder'),
                         ('interface')
) AS e(entity_type)
WHERE f.name IN (
                 'height', 'depth', 'radius',
                 'inner_diameter', 'outer_diameter'
    )
    ON CONFLICT DO NOTHING;

INSERT INTO core.entity_feature (entity_type, feature_id, role)
SELECT e.entity_type::core.entity_type, f.id, 'mounting'::core.role_type
FROM core.feature_definition f
         CROSS JOIN (VALUES
                         ('tool'),
                         ('holder'),
                         ('interface')
) AS e(entity_type)
WHERE f.name IN (
                 'connection_type',
                 'interface_type',
                 'taper_angle',
                 'thread_size',
                 'shank_diameter',
                 'shank_length',
                 'mounting_length'
    )
    ON CONFLICT DO NOTHING;

INSERT INTO core.entity_feature (entity_type, feature_id, role)
SELECT 'machine_station'::core.entity_type, f.id, 'constraint'::core.role_type
FROM core.feature_definition f
WHERE f.name IN (
                 'max_tool_diameter',
                 'max_tool_length',
                 'max_holder_length',
                 'station_type',
                 'position_index'
    )
    ON CONFLICT DO NOTHING;

INSERT INTO core.entity_feature (entity_type, feature_id, role)
SELECT e.entity_type::core.entity_type, f.id, 'mounting'::core.role_type
FROM core.feature_definition f
         CROSS JOIN (VALUES
                         ('tool'),
                         ('holder')
) AS e(entity_type)
WHERE f.name IN (
                 '',
                 'insert_shape',
                 'relief_angle',
                 'tolerance_class',
                 'mounting_type'
    )
ON CONFLICT DO NOTHING;