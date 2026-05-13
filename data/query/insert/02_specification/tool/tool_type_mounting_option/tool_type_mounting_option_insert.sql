SET search_path TO specification;

INSERT INTO tool_type_mounting_option (type_id, option_name, requirement_basis)
SELECT
    tt.id,
    m.option_name,
    m.requirement_basis::core.tool_mounting_requirement
FROM (
    VALUES

        --insert
    ('insert','iso_standard','form_and_geometry'),
    ('insert', 'grooving_system', 'manufacturer_and_code_and_geometry'),

        -- drill
    ('drill', 'clamping','geometry'),
    ('drill', 'weldon','geometry'),

        --boring_bar
    ('boring_bar','screw','form_and_geometry'),
    ('boring_bar','nut','geometry')

     ) AS m(type_name,option_name,requirement_basis)
JOIN specification.tool_type tt ON tt.name = m.type_name
ON CONFLICT DO NOTHING;






