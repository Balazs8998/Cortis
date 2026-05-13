SET search_path TO specification;

insert into specification.tool_type(name)
values ('insert'),
       ('drill'),
       ('boring_bar')
on conflict do nothing ;