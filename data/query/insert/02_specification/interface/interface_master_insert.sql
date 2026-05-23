SET search_path TO specification;

INSERT INTO interface_master (manufacturer_code, type_id)
SELECT
v.manufacturer_code,
it.id
FROM (
    VALUES
        -- er collet
        ('ER16_D1.0-2.0','er_collet'),
        ('ER16_D2.0-3.0','er_collet'),
        ('ER16_D3.0-4.0','er_collet'),
        ('ER16_D4.0-5.0','er_collet'),
        ('ER16_D5.0-6.0','er_collet'),
        ('ER16_D6.0-7.0','er_collet'),
        ('ER16_D7.0-8.0','er_collet'),
        ('ER16_D8.0-9.0','er_collet'),
        ('ER16_D9.0-10.0','er_collet'),
        ('ER16_D10.0-11.0','er_collet'),

-- er fix tapping collet
        ('ER-FT16_D1.0-2.0','er_fix_tapping_collet'),
        ('ER-FT16_D2.0-3.0','er_fix_tapping_collet'),
        ('ER-FT16_D3.0-4.0','er_fix_tapping_collet'),
        ('ER-FT16_D4.0-5.0','er_fix_tapping_collet'),
        ('ER-FT16_D5.0-6.0','er_fix_tapping_collet'),
        ('ER-FT16_D6.0-7.0','er_fix_tapping_collet'),
        ('ER-FT16_D7.0-8.0','er_fix_tapping_collet'),
        ('ER-FT16_D8.0-9.0','er_fix_tapping_collet'),
        ('ER-FT16_D9.0-10.0','er_fix_tapping_collet'),
        ('ER-FT16_D10.0-11.0','er_fix_tapping_collet'),

-- er extension tapping collet
        ('ER-EX16_D1.0-2.0','er_extension_tapping_collet'),
        ('ER-EX16_D2.0-3.0','er_extension_tapping_collet'),
        ('ER-EX16_D3.0-4.0','er_extension_tapping_collet'),
        ('ER-EX16_D4.0-5.0','er_extension_tapping_collet'),
        ('ER-EX16_D5.0-6.0','er_extension_tapping_collet'),
        ('ER-EX16_D6.0-7.0','er_extension_tapping_collet'),
        ('ER-EX16_D7.0-8.0','er_extension_tapping_collet'),
        ('ER-EX16_D8.0-9.0','er_extension_tapping_collet'),
        ('ER-EX16_D9.0-10.0','er_extension_tapping_collet'),
        ('ER-EX16_D10.0-11.0','er_extension_tapping_collet')

     ) AS v(manufacturer_code, type_name)
JOIN interface_type it ON it.name = v.type_name
ON CONFLICT DO NOTHING