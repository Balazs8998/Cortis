ALTER TYPE core.entity_type
    RENAME VALUE 'tool' TO 'tool_master';

ALTER TYPE core.entity_type
    RENAME VALUE 'holder' TO 'holder_master';

ALTER TYPE core.entity_type
    RENAME VALUE 'interface' TO 'interface_master';

ALTER TYPE core.entity_type
    RENAME VALUE 'machine' TO 'machine_master';

ALTER TYPE core.entity_type
    RENAME VALUE
    'machine_station'
    TO
    'machine_tool_station_category';