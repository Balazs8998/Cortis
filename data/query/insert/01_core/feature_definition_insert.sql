INSERT INTO core.feature_definition (name, unit, data_type)
VALUES
-- Geometry
('height', 'mm', 'number'),
('depth', 'mm', 'number'),
('radius', 'mm', 'number'),
('inner_diameter', 'mm', 'number'),
('outer_diameter', 'mm', 'number'),

-- Mounting / Interface
('connection_type', 'text', 'string'),
('interface_type', 'text', 'string'),
('taper_angle', 'degree', 'number'),
('thread_size', 'text', 'string'),
('shank_diameter', 'mm', 'number'),
('shank_length', 'mm', 'number'),
('mounting_length', 'mm', 'number'),

-- Insert / Cutting geometry
('insert_shape', 'text', 'string'),
('relief_angle', 'degree', 'number'),
('tolerance_class', 'text', 'string'),
('mounting_type', 'text', 'string'),

-- Machine constraints
('max_tool_diameter', 'mm', 'number'),
('max_tool_length', 'mm', 'number'),
('max_holder_length', 'mm', 'number'),
('station_type', 'text', 'string'),
('position_index', 'index', 'integer')

ON CONFLICT (name) DO NOTHING;