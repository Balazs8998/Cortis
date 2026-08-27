-- =========================================================
-- CORTIS - Translation constraints
-- =========================================================


-- Category


ALTER TABLE translation.category
    ADD CONSTRAINT uq_translation_category_name
        UNIQUE (category_name);


-- Keyword


ALTER TABLE translation.keyword
    ADD CONSTRAINT uq_translation_keyword
        UNIQUE (keyword);