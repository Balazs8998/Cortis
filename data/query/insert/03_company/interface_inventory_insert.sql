SET search_path TO company;

INSERT INTO company.interface_inventory
(
    interface_code,
    name,
    quantity,
    location,
    interface_master_id
)
SELECT
    v.interface_code,
    v.name,
    v.quantity,
    v.location,
    im.id
FROM (
         VALUES

             -- ER collet
             ('I001001', 'ER16 collet 1.0-2.0',              5, 'Rack-A1', 'ER16_D1.0-2.0'),
             ('I001002', 'ER16 collet 2.0-3.0',              5, 'Rack-A1', 'ER16_D2.0-3.0'),
             ('I001003', 'ER16 collet 3.0-4.0',              5, 'Rack-A1', 'ER16_D3.0-4.0'),
             ('I001004', 'ER16 collet 4.0-5.0',              5, 'Rack-A1', 'ER16_D4.0-5.0'),
             ('I001005', 'ER16 collet 5.0-6.0',              5, 'Rack-A1', 'ER16_D5.0-6.0'),
             ('I001006', 'ER16 collet 6.0-7.0',              5, 'Rack-A1', 'ER16_D6.0-7.0'),
             ('I001007', 'ER16 collet 7.0-8.0',              5, 'Rack-A1', 'ER16_D7.0-8.0'),
             ('I001008', 'ER16 collet 8.0-9.0',              5, 'Rack-A1', 'ER16_D8.0-9.0'),
             ('I001009', 'ER16 collet 9.0-10.0',             5, 'Rack-A1', 'ER16_D9.0-10.0'),
             ('I001010', 'ER16 collet 10.0-11.0',            5, 'Rack-A1', 'ER16_D10.0-11.0'),

             -- ER fix tapping collet
             ('I002001', 'ER16 fix tapping collet 1.0-2.0',  3, 'Rack-B1', 'ER-FT16_D1.0-2.0'),
             ('I002002', 'ER16 fix tapping collet 2.0-3.0',  3, 'Rack-B1', 'ER-FT16_D2.0-3.0'),
             ('I002003', 'ER16 fix tapping collet 3.0-4.0',  3, 'Rack-B1', 'ER-FT16_D3.0-4.0'),
             ('I002004', 'ER16 fix tapping collet 4.0-5.0',  3, 'Rack-B1', 'ER-FT16_D4.0-5.0'),
             ('I002005', 'ER16 fix tapping collet 5.0-6.0',  3, 'Rack-B1', 'ER-FT16_D5.0-6.0'),
             ('I002006', 'ER16 fix tapping collet 6.0-7.0',  3, 'Rack-B1', 'ER-FT16_D6.0-7.0'),
             ('I002007', 'ER16 fix tapping collet 7.0-8.0',  3, 'Rack-B1', 'ER-FT16_D7.0-8.0'),
             ('I002008', 'ER16 fix tapping collet 8.0-9.0',  3, 'Rack-B1', 'ER-FT16_D8.0-9.0'),
             ('I002009', 'ER16 fix tapping collet 9.0-10.0', 3, 'Rack-B1', 'ER-FT16_D9.0-10.0'),
             ('I002010', 'ER16 fix tapping collet 10.0-11.0',3, 'Rack-B1', 'ER-FT16_D10.0-11.0'),

             -- ER extension collet
             ('I003001', 'ER16 extension tapping 1.0-2.0',  2, 'Rack-C1', 'ER-EX16_D1.0-2.0'),
             ('I003002', 'ER16 extension tapping 2.0-3.0',  2, 'Rack-C1', 'ER-EX16_D2.0-3.0'),
             ('I003003', 'ER16 extension tapping 3.0-4.0',  2, 'Rack-C1', 'ER-EX16_D3.0-4.0'),
             ('I003004', 'ER16 extension tapping 4.0-5.0',  2, 'Rack-C1', 'ER-EX16_D4.0-5.0'),
             ('I003005', 'ER16 extension tapping 5.0-6.0',  2, 'Rack-C1', 'ER-EX16_D5.0-6.0'),
             ('I003006', 'ER16 extension tapping 6.0-7.0',  2, 'Rack-C1', 'ER-EX16_D6.0-7.0'),
             ('I003007', 'ER16 extension tapping 7.0-8.0',  2, 'Rack-C1', 'ER-EX16_D7.0-8.0'),
             ('I003008', 'ER16 extension tapping 8.0-9.0',  2, 'Rack-C1', 'ER-EX16_D8.0-9.0'),
             ('I003009', 'ER16 extension tapping 9.0-10.0', 2, 'Rack-C1', 'ER-EX16_D9.0-10.0'),
             ('I003010', 'ER16 extension tapping 10.0-11.0',2, 'Rack-C1', 'ER-EX16_D10.0-11.0')

     ) AS v(
            interface_code,
            name,
            quantity,
            location,
            manufacturer_code
    )

         JOIN specification.interface_master im
              ON im.manufacturer_code = v.manufacturer_code

ON CONFLICT (interface_code) DO NOTHING;