SET search_path TO company;

INSERT INTO company.interface_inventory (interface_code, name, interface_master_id)
SELECT
    v.interface_code,
    v.name,
    im.id
FROM (
    VALUES
    ()
     ) AS v(interface_code, name)
JOIN specification.interface_master im ON v.name = im.manufacturer_code