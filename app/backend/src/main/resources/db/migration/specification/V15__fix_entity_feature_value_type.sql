ALTER TABLE specification.entity_feature_value
DROP CONSTRAINT efv_entity_type_check;

ALTER TABLE specification.entity_feature_value
ALTER COLUMN entity_type
TYPE core.entity_type
USING (
    CASE entity_type
        WHEN 'tool'
            THEN 'tool_master'

        WHEN 'holder'
            THEN 'holder_master'

        WHEN 'interface'
            THEN 'interface_master'

        WHEN 'machine'
            THEN 'machine_master'

        WHEN 'machine_tool_station'
            THEN 'machine_tool_station_category'

        ELSE entity_type
    END
)::core.entity_type;