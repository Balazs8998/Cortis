# PostgreSQL Extensions Used in CORTIS

This document explains the PostgreSQL extensions used in the CORTIS project, why they were selected, and what benefits they provide.

---

# 1. pgcrypto

## English

`pgcrypto` provides cryptographic functions and secure UUID generation.

Main use in CORTIS:

- Generate unique identifiers with `gen_random_uuid()`
- Safer public IDs than incremental integers
- Better for APIs and distributed systems

Example:

```sql
id UUID PRIMARY KEY DEFAULT gen_random_uuid()
```
Magyar

A pgcrypto kriptográfiai függvényeket és biztonságos UUID generálást biztosít.

Fő felhasználás CORTIS-ban:

Egyedi azonosítók generálása gen_random_uuid() segítségével
Biztonságosabb publikus azonosítók, mint a sima növekvő számok
Jobb API-khoz és elosztott rendszerekhez

Példa:
```
id UUID PRIMARY KEY DEFAULT gen_random_uuid()
```
2. citext
English

-------------------------------------------------------------------------

citext means case-insensitive text.

It allows text comparisons without caring about uppercase or lowercase letters.

Useful for:

Emails
Usernames
Manufacturer names
Search fields

Example:

manufacturer CITEXT

Then these are treated equally:

Sandvik
sandvik
SANDVIK
Magyar

A citext kis- és nagybetű független szövegtípust jelent.

Lehetővé teszi a szöveg összehasonlítását úgy, hogy a kis- és nagybetűk nem számítanak.

Hasznos:

Email címekhez
Felhasználónevekhez
Gyártónevekhez
Keresési mezőkhöz

Példa:

manufacturer CITEXT

Ezek azonosnak számítanak:

Sandvik
sandvik
SANDVIK
3. pg_trgm
English

---------------------------------------------------------------

pg_trgm provides trigram-based similarity search.

It is useful for:

Typo tolerant search
Fuzzy matching
Ranking similar results

Example:
```
SELECT *
FROM tool
WHERE tool_name % 'dril 10';
```
This may match:

drill 10
drill 10mm
dril Ø10
Magyar

A pg_trgm trigram alapú hasonlósági keresést biztosít.

Hasznos:

Elírásokat toleráló kereséshez
Rugalmas egyezéshez
Hasonló találatok rangsorolásához

Példa:
```
SELECT *
FROM tool
WHERE tool_name % 'dril 10';
```
Lehetséges találatok:

drill 10
drill 10mm
dril Ø10
4. unaccent
English

unaccent removes accent sensitivity during search.

Useful when users type names without accents.

Example:
```
Balazs = Balázs
Muller = Müller
```
Useful for international systems.

Magyar

Az unaccent figyelmen kívül hagyja az ékezeteket keresés közben.

Hasznos, ha a felhasználó ékezet nélkül ír be neveket.

Példa:
```
Balazs = Balázs
Muller = Müller
```
Nemzetközi rendszereknél különösen hasznos.

------------------------------------------------------------------------------------

5. timescaledb (Planned for CORTIS V2)
English

timescaledb is a time-series database extension.

Planned use in CORTIS V2:

Machine runtime logs
Sensor data
Production monitoring
Cycle time tracking
Performance history

Useful for industrial analytics.

Magyar

A timescaledb egy idősoros adatbázis kiegészítő.
Tervezett felhasználás CORTIS V2-ben:

Gépidő naplók
Szenzoradatok
Gyártásfigyelés
Ciklusidő követés
Teljesítmény előzmények



Ipari elemzésekhez különösen hasznos.

Summary / Összegzés

Extension	Main Purpose

pgcrypto	UUID + security

citext	Case-insensitive text

pg_trgm	Smart similarity search

unaccent	Accent-free search

timescaledb	Time-series industrial data