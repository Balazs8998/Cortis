SET search_path TO specification;

SELECT
    manufacturer_code,
    type_id,
    mounting_option_id,
    COUNT(*) AS duplicate_count
FROM specification.tool_master
GROUP BY manufacturer_code, type_id, mounting_option_id
HAVING COUNT(*) > 1
ORDER BY duplicate_count DESC;