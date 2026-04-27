CREATE EXTENSION IF NOT EXISTS pgcrypto;
-- Cryptographic functions + UUID generation
-- Example: gen_random_uuid()

-- Kriptográfiai függvények + UUID generálás
-- Példa: gen_random_uuid()


CREATE EXTENSION IF NOT EXISTS citext;
-- Case-insensitive text type
-- Uppercase / lowercase differences do not matter
-- Useful for emails, usernames, manufacturers

-- Kis- és nagybetű független szöveg típus
-- A kis- és nagybetű különbség nem számít
-- Hasznos emailhez, felhasználónevekhez, gyártókhoz


CREATE EXTENSION IF NOT EXISTS pg_trgm;
-- Trigram-based similarity search
-- Useful for typo-tolerant search and fuzzy matching
-- Example: "dril 10" can match "drill 10"

-- Trigram alapú hasonlósági keresés
-- Hasznos elíráskezelő és rugalmas kereséshez
-- Példa: "dril 10" megtalálhatja a "drill 10" értéket


CREATE EXTENSION IF NOT EXISTS unaccent;
-- Ignore accents / diacritics in text search
-- Example: Balazs = Balázs, Muller = Müller

-- Ékezetek figyelmen kívül hagyása kereséskor
-- Példa: Balazs = Balázs, Muller = Müller


/*************************************************
 !! CORTIS V2.0 !!
*************************************************/

-- CREATE EXTENSION IF NOT EXISTS timescaledb;
-- Time-series database extension
-- Useful for machine runtime data, sensor values
-- Production monitoring, cycle times, performance logs

-- Idősoros adatbázis kiegészítő
-- Hasznos gépidő adatokhoz, szenzor értékekhez
-- Gyártásfigyeléshez, ciklusidőkhöz, teljesítmény naplókhoz