INSERT INTO specification.tool_mounting_feature_requirement (requirement_feature_id, mounting_option_id)
SELECT ef.id,
       ttmo.id
FROM (VALUES

          --boring_bar, screw
          ('boring_bar', 'screw', 'mounting', 'tool', 'manufacturer'),
          ('boring_bar', 'screw', 'mounting', 'tool', 'mounting_code'),
          ('boring_bar', 'screw', 'mounting', 'tool','size')

      ) AS m(type_name, option_name, role, entity_type, feature_name)
         JOIN specification.tool_type tt ON tt.name = m.type_name
         JOIN specification.tool_type_mounting_option ttmo ON tt.id = ttmo.type_id
    AND ttmo.option_name = m.option_name
         JOIN core.feature_definition fd ON fd.name = m.feature_name
         JOIN core.entity_feature ef
              ON fd.id = ef.feature_id
                  AND ef.entity_type = m.entity_type::core.entity_type
                  AND ef.role = m.role::core.role_type

ON CONFLICT DO NOTHING;