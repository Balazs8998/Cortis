```sql
-- Az előző hibás tranzakció lezárása.
-- Ha nincs aktív tranzakció, legfeljebb figyelmeztetést kapsz.
ROLLBACK;

BEGIN;

-- =========================================================
-- 0. EGYEDISÉGI SZABÁLYOK
-- Az ON CONFLICT működéséhez szükségesek.
-- =========================================================

CREATE UNIQUE INDEX IF NOT EXISTS ux_translation_language_code
    ON translation.language (code);

CREATE UNIQUE INDEX IF NOT EXISTS ux_translation_category_name
    ON translation.category (category_name);

CREATE UNIQUE INDEX IF NOT EXISTS ux_translation_keyword
    ON translation.keyword (keyword);

CREATE UNIQUE INDEX IF NOT EXISTS ux_translation_text_key
    ON translation.text (
        keyword_id,
        language_id,
        category_id
    );


-- =========================================================
-- 1. NYELVEK
-- =========================================================

INSERT INTO translation.language (
    code,
    name
)
VALUES
    ('hu', 'Magyar'),
    ('de', 'Deutsch'),
    ('en', 'English')
ON CONFLICT (code)
DO UPDATE SET
    name = EXCLUDED.name,
    updated_at = NOW();


-- =========================================================
-- 2. LOGIN KATEGÓRIA
-- =========================================================

INSERT INTO translation.category (
    category_name
)
VALUES
    ('login')
ON CONFLICT (category_name)
DO NOTHING;


-- =========================================================
-- 3. FORDÍTÁSI KULCSSZAVAK
-- =========================================================

INSERT INTO translation.keyword (
    keyword
)
VALUES
    ('title'),
    ('subtitle'),

    ('username.label'),
    ('username.prompt'),

    ('password.label'),
    ('password.prompt'),

    ('button.submit'),

    ('error.empty_credentials'),
    ('error.invalid_credentials'),
    ('error.inactive_user'),
    ('error.connection'),
    ('error.timeout'),
    ('error.server'),
    ('error.unexpected')
ON CONFLICT (keyword)
DO NOTHING;


-- =========================================================
-- 4. LOGIN FORDÍTÁSOK
-- =========================================================

WITH translation_seed (
    language_code,
    keyword,
    translation_text
) AS (
    VALUES

        -- =================================================
        -- MAGYAR
        -- =================================================

        (
            'hu',
            'title',
            'Bejelentkezés'
        ),
        (
            'hu',
            'subtitle',
            'Lépj be a CORTIS rendszerbe'
        ),
        (
            'hu',
            'username.label',
            'Felhasználónév'
        ),
        (
            'hu',
            'username.prompt',
            'Add meg a felhasználóneved'
        ),
        (
            'hu',
            'password.label',
            'Jelszó'
        ),
        (
            'hu',
            'password.prompt',
            'Add meg a jelszavad'
        ),
        (
            'hu',
            'button.submit',
            'Bejelentkezés'
        ),
        (
            'hu',
            'error.empty_credentials',
            'A felhasználónév és a jelszó nem lehet üres.'
        ),
        (
            'hu',
            'error.invalid_credentials',
            'Hibás felhasználónév vagy jelszó.'
        ),
        (
            'hu',
            'error.inactive_user',
            'Ez a felhasználói fiók inaktív.'
        ),
        (
            'hu',
            'error.connection',
            'Nem sikerült kapcsolódni a szerverhez.'
        ),
        (
            'hu',
            'error.timeout',
            'A szerver nem válaszolt időben.'
        ),
        (
            'hu',
            'error.server',
            'Szerverhiba történt. Próbáld újra később.'
        ),
        (
            'hu',
            'error.unexpected',
            'Váratlan hiba történt.'
        ),


        -- =================================================
        -- NÉMET
        -- =================================================

        (
            'de',
            'title',
            'Anmeldung'
        ),
        (
            'de',
            'subtitle',
            'Melden Sie sich beim CORTIS-System an'
        ),
        (
            'de',
            'username.label',
            'Benutzername'
        ),
        (
            'de',
            'username.prompt',
            'Geben Sie Ihren Benutzernamen ein'
        ),
        (
            'de',
            'password.label',
            'Passwort'
        ),
        (
            'de',
            'password.prompt',
            'Geben Sie Ihr Passwort ein'
        ),
        (
            'de',
            'button.submit',
            'Anmelden'
        ),
        (
            'de',
            'error.empty_credentials',
            'Benutzername und Passwort dürfen nicht leer sein.'
        ),
        (
            'de',
            'error.invalid_credentials',
            'Benutzername oder Passwort ist falsch.'
        ),
        (
            'de',
            'error.inactive_user',
            'Dieses Benutzerkonto ist deaktiviert.'
        ),
        (
            'de',
            'error.connection',
            'Die Verbindung zum Server konnte nicht hergestellt werden.'
        ),
        (
            'de',
            'error.timeout',
            'Der Server hat nicht rechtzeitig geantwortet.'
        ),
        (
            'de',
            'error.server',
            'Ein Serverfehler ist aufgetreten. Versuchen Sie es später erneut.'
        ),
        (
            'de',
            'error.unexpected',
            'Ein unerwarteter Fehler ist aufgetreten.'
        ),


        -- =================================================
        -- ANGOL
        -- =================================================

        (
            'en',
            'title',
            'Login'
        ),
        (
            'en',
            'subtitle',
            'Sign in to the CORTIS system'
        ),
        (
            'en',
            'username.label',
            'Username'
        ),
        (
            'en',
            'username.prompt',
            'Enter your username'
        ),
        (
            'en',
            'password.label',
            'Password'
        ),
        (
            'en',
            'password.prompt',
            'Enter your password'
        ),
        (
            'en',
            'button.submit',
            'Login'
        ),
        (
            'en',
            'error.empty_credentials',
            'Username and password cannot be empty.'
        ),
        (
            'en',
            'error.invalid_credentials',
            'Invalid username or password.'
        ),
        (
            'en',
            'error.inactive_user',
            'This user account is inactive.'
        ),
        (
            'en',
            'error.connection',
            'Could not connect to the server.'
        ),
        (
            'en',
            'error.timeout',
            'The server did not respond in time.'
        ),
        (
            'en',
            'error.server',
            'A server error occurred. Please try again later.'
        ),
        (
            'en',
            'error.unexpected',
            'An unexpected error occurred.'
        )
)

INSERT INTO translation.text (
    keyword_id,
    language_id,
    category_id,
    translation_text
)
SELECT
    keyword_table.id,
    language_table.id,
    category_table.id,
    seed.translation_text
FROM translation_seed seed
JOIN translation.language language_table
    ON language_table.code = seed.language_code
JOIN translation.keyword keyword_table
    ON keyword_table.keyword = seed.keyword
JOIN translation.category category_table
    ON category_table.category_name = 'login'
ON CONFLICT (
    keyword_id,
    language_id,
    category_id
)
DO UPDATE SET
    translation_text = EXCLUDED.translation_text,
    updated_at = NOW();


COMMIT;


-- =========================================================
-- 5. ELLENŐRZÉS
-- =========================================================

SELECT
    language_table.code AS language_code,
    category_table.category_name
        || '.'
        || keyword_table.keyword AS translation_key,
    text_table.translation_text
FROM translation.text text_table
JOIN translation.language language_table
    ON language_table.id = text_table.language_id
JOIN translation.keyword keyword_table
    ON keyword_table.id = text_table.keyword_id
JOIN translation.category category_table
    ON category_table.id = text_table.category_id
WHERE category_table.category_name = 'login'
ORDER BY
    language_table.code,
    keyword_table.keyword;
```
