# machine_type

## Feladata

A géptípusok definícióinak tárolása.

Meghatározza a rendszerben használható gépkategóriákat.

## Felépítése

| Oszlop | Típus | Megkötések | Leírás |
|---|---|---|---|
| id | UUID | PRIMARY KEY | egyedi sorazonosító |
| name | TEXT | NOT NULL, UNIQUE | géptípus neve |
| description | TEXT | - | géptípus leírása |

## Oszlopok szerepe

### id

Egyedi technikai azonosító minden rekord számára.  
Kapcsolatok és hivatkozások kezelésére szolgál.

### name

A géptípus megnevezése.

Például: lathe, milling machine, grinding machine.

### description

Kiegészítő leírás vagy megjegyzés a géptípusról.

## Megkötések

| Név | Típus | Oszlopok | Leírás |
|---|---|---|---|
| mt_name_key | UNIQUE | name | biztosítja, hogy ugyanaz a géptípus csak egyszer szerepelhessen |
