insert into specification.tool_type(type)
values ('indexable_turning'),
       ('indexable_parting'),
       ('drill'),
       ('boring_bar')
on conflict do nothing ;