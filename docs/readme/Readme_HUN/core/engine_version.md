# engine_version

## Feladata

A rendszermodulok verzióinak és kiadási állapotának nyilvántartása.

---

## Felépítése

| Oszlop | Típus | Megkötések | Leírás |
|---|---|---|---|
| module | TEXT | NOT NULL, PRIMARY KEY | modul neve |
| version | TEXT | NOT NULL, PRIMARY KEY | verziószám |
| release_date | TIMESTAMPTZ | NOT NULL | kiadás időpontja |
| deployed_date | TIMESTAMPTZ | - | telepítés időpontja |
| is_stable | BOOLEAN | NOT NULL | stabil kiadás jelölése |
| description | TEXT | - | verzió leírása |

---

## Oszlopok szerepe

### module

Meghatározza, hogy mely rendszer modulhoz tartozik a verzió.

Például: core, tool, machine, order.

### version

A modul verziószáma.

Például: 0.0.1, 1.2.0, 2.0.0.

### release_date

A verzió létrehozásának vagy kiadásának időpontja.

### deployed_date

Meghatározza, hogy a verzió mikor került ténylegesen telepítésre vagy használatba.

### is_stable

Jelzi, hogy a verzió stabil kiadásnak számít-e.

### description

Kiegészítő információ vagy változásleírás a verzióhoz.

---

## Megkötések

| Név | Típus | Oszlopok | Leírás |
|---|---|---|---|
| ev_pkey | PRIMARY KEY | module, version | biztosítja, hogy egy modulon belül ugyanaz a verzió csak egyszer szerepelhessen |