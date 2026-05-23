INSERT INTO company.tool_inventory
(tool_code, name, quantity, location, tool_master_id)
SELECT
    v.tool_code,
    v.name,
    v.quantity,
    v.location,
    tm.id
FROM (
         VALUES
             ('DCMT-02-11', 'W001001',       'DCMT-02-11 insert',        0, 'pending'),
             ('DRILL-10',        'W002001',    'DRILL-10 drill',           2, 'pending'),
             ('R105.1819.005.1.4', 'W003001',  'R105.1819.005.1.4 boring bar', 0, 'pending'),
             ('GROOVE-3-ISCAR',    'W001002',  'Iscar 3 mm grooving insert',     12, 'pending'),
             ('DRILL-WD-16',      'W002002',   '16 mm weldon drill',             4, 'pending'),
             ('BB-NUT-12',        'W003002',   'Sandvik boring bar nut system',  2, 'pending')

     ) AS v(manufacturer_code, tool_code, name, quantity, location)
         JOIN specification.tool_master tm
              ON tm.manufacturer_code = v.manufacturer_code
ON CONFLICT DO NOTHING;