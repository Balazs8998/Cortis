insert into specification.tool_master(manufacturer_code, type_id,mounting_option_id)
select
    v.manufacturer_code,
    ttmo.type_id,
    ttmo.id
from (
         values
             ('DCMT-02-11','insert','iso_standard'),
             ('DRILL-10','drill', 'clamping'),
             ('R105.1819.005.1.4','boring_bar', 'screw'),
             ('DCMT-02-11','insert','iso_standard'),
             ('DRILL-10','drill', 'clamping'),
             ('R105.1819.005.1.4','boring_bar', 'screw'),
             ('GROOVE-3-ISCAR', 'insert', 'grooving_system'),
             ('DRILL-WD-16', 'drill', 'weldon'),
             ('BB-NUT-12', 'boring_bar', 'nut')

     ) AS v(manufacturer_code, type_name, option_name)
         join specification.tool_type tt on tt.name = v.type_name
         join specification.tool_type_mounting_option ttmo ON tt.id = ttmo.type_id
    AND ttmo.option_name = v.option_name
on conflict do nothing ;

