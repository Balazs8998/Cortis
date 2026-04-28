-- =========================================================
-- CORTIS database common_functions - Baseline
-- =========================================================






-- =========================================================
-- Common trigger helper functions
-- =========================================================

CREATE OR REPLACE FUNCTION set_updated_at_and_by()
RETURNS trigger AS $$
    BEGIN
    NEW.updated_at := now();
    NEW.updated_by := current_setting('app.current_user',true);
    RETURN NEW;
    END;
    $$ LANGUAGE plpgsql;


CREATE OR REPLACE FUNCTION set_updated_at()
RETURNS trigger AS $$
    BEGIN
    NEW.updated_at := now();
    RETURN NEW;
    END;
    $$ LANGUAGE plpgsql;


/*
CREATE OR REPLACE FUNCTION mark_related_turbo_dirty()
    RETURNS trigger AS $$
BEGIN
    UPDATE turbo_state
    SET dirty_flag = true
    WHERE id IN (
        SELECT turbo_id
        FROM turbo_source
        WHERE source_table = TG_TABLE_SCHEMA || '.' || TG_TABLE_NAME
    );

    RETURN COALESCE(NEW, OLD);
END;
$$ LANGUAGE plpgsql;
*/

-- =========================================================
-- Common validation functions
-- =========================================================

-- =========================================================
-- Utility / helper functions
-- =========================================================
