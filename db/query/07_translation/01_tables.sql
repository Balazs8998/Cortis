-- TranslationLanguage definition
CREATE TABLE IF NOT EXISTS translation.language
(
    id         uuid PRIMARY KEY     DEFAULT gen_random_uuid(),
    name       TEXT        NOT NULL,
    code       TEXT        NOT NULL UNIQUE,

    created_by TEXT                 DEFAULT current_setting('app.current_user', true),
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at TIMESTAMPTZ,
    updated_by TEXT        NOT NULL DEFAULT current_setting('app.current_user', true),
    deleted_at TIMESTAMPTZ
);

-- TranslationKeyword definition
CREATE TABLE IF NOT EXISTS translation.keyword
(
    id         uuid PRIMARY KEY     DEFAULT gen_random_uuid(),
    keyword    TEXT        NOT NULL,

    created_by TEXT                 DEFAULT current_setting('app.current_user', true),
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at TIMESTAMPTZ,
    updated_by TEXT        NOT NULL DEFAULT current_setting('app.current_user', true),
    deleted_at TIMESTAMPTZ
);

-- TranslationCategory definition
CREATE TABLE IF NOT EXISTS translation.category
(
    id            uuid PRIMARY KEY     DEFAULT gen_random_uuid(),
    category_name TEXT        NOT NULL,

    created_by    TEXT                 DEFAULT current_setting('app.current_user', true),
    created_at    TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at    TIMESTAMPTZ,
    updated_by    TEXT        NOT NULL DEFAULT current_setting('app.current_user', true),
    deleted_at    TIMESTAMPTZ
);

-- TranslationText definition
CREATE TABLE IF NOT EXISTS translation.text
(
    id               uuid PRIMARY KEY     DEFAULT gen_random_uuid(),
    translation_text TEXT        NOT NULL,
    keyword_id       uuid        NOT NULL,
    language_id      uuid        NOT NULL,
    category_id      uuid,

    created_by       TEXT                 DEFAULT current_setting('app.current_user', true),
    created_at       TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at       TIMESTAMPTZ,
    updated_by       TEXT        NOT NULL DEFAULT current_setting('app.current_user', true),
    deleted_at       TIMESTAMPTZ,
    FOREIGN KEY (keyword_id) REFERENCES translation.keyword (id),
    FOREIGN KEY (language_id) REFERENCES translation.language (id),
    FOREIGN KEY (category_id) REFERENCES translation.category (id),
    UNIQUE (keyword_id, language_id, category_id)
);