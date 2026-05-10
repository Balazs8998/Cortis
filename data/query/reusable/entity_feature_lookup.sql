select e.role, e.entity_type, fd.name, e.id
From core.feature_definition fd
         join core.entity_feature e on fd.id = e.feature_id
where entity_type = 'tool' and role = 'mounting';
