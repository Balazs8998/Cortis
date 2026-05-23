SET search_path TO specification;

INSERT INTO interface_type (name)
values
    ('er_collet'),
    ('er_fix_tapping_collet'),
    ('er_extension_tapping_collet')
ON CONFLICT DO NOTHING;
