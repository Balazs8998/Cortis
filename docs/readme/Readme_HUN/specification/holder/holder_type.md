# holder_type

## Feladata

A tartó típusok definícióinak tárolása.

Meghatározza a rendszerben használható tartó kategóriákat.

## Felépítése

| Oszlop | Típus | Megkötések | Leírás |
|---|---|---|---|
| id | UUID | PRIMARY KEY | egyedi sorazonosító |
| name | TEXT | NOT NULL, UNIQUE | tartó típus neve |
| is_interface | BOOLEAN | NOT NULL | interfész típus jelölése |
| description | TEXT | - | tartó típus leírása |

## Oszlopok szerepe

### id

Egyedi technikai azonosító minden rekord számára.  
Kapcsolatok és hivatkozások kezelésére szolgál.

### name

A tartó típus megnevezése.

Például: collet holder, graf holder, toolshank.

### is_interface

Meghatározza, hogy a tartóhoz szükséges-e befogó (interface).

### description

Kiegészítő leírás vagy megjegyzés a tartó típusról.

## Megkötések

| Név | Típus | Oszlopok | Leírás |
|---|---|---|---|
| ht_name_key | UNIQUE | name | biztosítja, hogy ugyanaz a tartó típus csak egyszer szerepelhessen |
