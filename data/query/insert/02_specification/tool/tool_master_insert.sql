insert into specification.tool_master(manufacturer_code, type_id)
select
    v.manufacturer_code,
    tt.id
from (
         values
             ('DCMT-02-11','insert'),
             ('DRILL-10','drill'),
             ('R105.1819.005.1.4','boring_bar'),
             ('DCMT-02-11','insert'),
             ('DRILL-10','drill'),
             ('R105.1819.005.1.4','boring_bar'),
             ('GROOVE-3-ISCAR', 'insert'),
             ('DRILL-WD-16', 'drill'),
             ('BB-NUT-12', 'boring_bar')

     ) AS v(manufacturer_code, type_name)
         join specification.tool_type tt on tt.name = v.type_name
on conflict do nothing ;

