SET search_path TO map;

INSERT INTO tool_mounting_option_map (tool_master_id, option_id)
SELECT
    tm.id,
    ttmo.id
FROM (
         VALUES

             ('DCMT-02-11','unknown','insert','iso_standard'),
             ('GROOVE-3-ISCAR','unknown','insert','grooving_system'),
             ('DRILL-10','unknown','drill','clamping'),
             ('DRILL-WD-16','unknown','drill','weldon'),
             ('DRILL-WD-16','unknown','drill','clamping'),
             ('R105.1819.005.1.4','unknown','boring_bar','screw'),
             ('BB-NUT-12','unknown','boring_bar','nut')

     ) AS v(manufacturer_code,manufacturer,type_name, option_name)
JOIN specification.tool_type tt ON v.type_name = tt.name
JOIN specification.tool_type_mounting_option ttmo ON v.option_name = ttmo.option_name AND tt.id = ttmo.type_id
JOIN specification.tool_master tm ON v.manufacturer = tm.manufacturer AND v.manufacturer_code = tm.manufacturer_code
ON CONFLICT (tool_master_id, option_id) DO NOTHING;