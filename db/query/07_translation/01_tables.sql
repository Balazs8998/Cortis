-- TranslationLanguage definition
CREATE TABLE IF NOT EXISTS translation.language
(
    id   uuid PRIMARY KEY DEFAULT gen_random_uuid(),
    name TEXT NOT NULL,
    code TEXT NOT NULL UNIQUE -- pl. 'hu', 'en', 'de'
);

-- TranslationKeyword definition
CREATE TABLE IF NOT EXISTS translation.keyword
(
    id      uuid PRIMARY KEY DEFAULT gen_random_uuid(),
    keyword TEXT NOT NULL
);

-- TranslationCategory definition
CREATE TABLE IF NOT EXISTS translation.category
(
    id            uuid PRIMARY KEY DEFAULT gen_random_uuid(),
    category_name TEXT NOT NULL
);

-- TranslationText definition
CREATE TABLE IF NOT EXISTS translation.text
(
    id               uuid PRIMARY KEY DEFAULT gen_random_uuid(),
    translation_text TEXT NOT NULL,
    keyword_id       uuid NOT NULL,
    language_id      uuid NOT NULL,
    category_id      uuid,
    FOREIGN KEY (keyword_id) REFERENCES translation.keyword (id),
    FOREIGN KEY (language_id) REFERENCES translation.language (id),
    FOREIGN KEY (category_id) REFERENCES translation.category (id),
    UNIQUE (keyword_id, language_id, category_id)
);