SET search_path TO company;

SELECT conname,
       contype,
       conrelid::regclass AS tableName
FROM pg_constraint
WHERE conrelid = 'company.tool_inventory'::regclass;

SELECT conname,
       contype,
       conrelid::regclass AS tableName
FROM pg_constraint
WHERE conrelid = 'company.interface_inventory'::regclass;

SELECT conname,
       contype,
       conrelid::regclass AS tableName
FROM pg_constraint
WHERE conrelid = 'company.holder_inventory'::regclass;

SELECT conname,
       contype,
       conrelid::regclass AS tableName
FROM pg_constraint
WHERE conrelid = 'company.machine_inventory'::regclass;
