SET search_path TO specification;

INSERT INTO interface_master (manufacturer_code, type_id)
SELECT
v.manufacturer_code,
it.id
FROM (
    VALUES
        -- er collet
        ('D16x30_ER_D1.0-2.0','er_collet'),
        ('D16x30_ER_D2.0-3.0','er_collet'),
        ('D16x30_ER_D3.0-4.0','er_collet'),
        ('D16x30_ER_D4.0-5.0','er_collet'),
        ('D16x30_ER_D5.0-6.0','er_collet'),
        ('D16x30_ER_D6.0-7.0','er_collet'),
        ('D16x30_ER_D7.0-8.0','er_collet'),
        ('D16x30_ER_D8.0-9.0','er_collet'),
        ('D16x30_ER_D9.0-10.0','er_collet'),
        ('D16x30_ER_D10.0-11.0','er_collet'),

-- er fix tapping collet
        ('D16x30_ER_ft_D1.0','er_fix_tapping_collet'),
        ('D16x30_ER_ft_D2.0','er_fix_tapping_collet'),
        ('D16x30_ER_ft_D3.0','er_fix_tapping_collet'),
        ('D16x30_ER_ft_D4.0','er_fix_tapping_collet'),
        ('D16x30_ER_ft_D5.0','er_fix_tapping_collet'),
        ('D16x30_ER_ft_D6.0','er_fix_tapping_collet'),
        ('D16x30_ER_ft_D7.0','er_fix_tapping_collet'),
        ('D16x30_ER_ft_D8.0','er_fix_tapping_collet'),
        ('D16x30_ER_ft_D9.0','er_fix_tapping_collet'),
        ('D16x30_ER_ft_D10.0','er_fix_tapping_collet'),

-- er extension tapping collet
        ('D16x30_ER_exttc_D1.0','er_extension_tapping_collet'),
        ('D16x30_ER_exttc_D2.0','er_extension_tapping_collet'),
        ('D16x30_ER_exttc_D3.0','er_extension_tapping_collet'),
        ('D16x30_ER_exttc_D4.0','er_extension_tapping_collet'),
        ('D16x30_ER_exttc_D5.0','er_extension_tapping_collet'),
        ('D16x30_ER_exttc_D6.0','er_extension_tapping_collet'),
        ('D16x30_ER_exttc_D7.0','er_extension_tapping_collet'),
        ('D16x30_ER_exttc_D8.0','er_extension_tapping_collet'),
        ('D16x30_ER_exttc_D9.0','er_extension_tapping_collet'),
        ('D16x30_ER_exttc_D10.0','er_extension_tapping_collet')

     ) AS v(manufacturer_code, type_name)
JOIN interface_type it ON it.name = v.type_name
ON CONFLICT DO NOTHING