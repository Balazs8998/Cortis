insert into specification.tool_master(manufacturer_code, type_id)
select
    v.manufacturer_code,
    t.id
from (
         values
             ('DCMT-02-11', 'indexable_turning'),
             ('DRILL-10', 'drill'),
             ('R105.1819.005.1.4', 'boring_bar')
     ) AS v(manufacturer_code, type_name)
         join specification.tool_type t on t.type = v.type_name
on conflict do nothing ;