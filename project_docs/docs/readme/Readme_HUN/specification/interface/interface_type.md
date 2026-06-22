# interface_type

## Feladata

Az interfész típusok definícióinak tárolása.

Meghatározza a rendszerben használható interfész kategóriákat.

## Felépítése

| Oszlop | Típus | Megkötések | Leírás |
|---|---|---|---|
| id | UUID | PRIMARY KEY | egyedi sorazonosító |
| name | TEXT | NOT NULL, UNIQUE | interfész típus neve |
| description | TEXT | - | interfész típus leírása |

## Oszlopok szerepe

### id

Egyedi technikai azonosító minden rekord számára.  
Kapcsolatok és hivatkozások kezelésére szolgál.

### name

Az interfész típus megnevezése.

Például: er_collet, er_fix_taping_collet, er_extension_tapping_collet

### description

Kiegészítő leírás vagy megjegyzés az interfész típusról.

## Megkötések

| Név | Típus | Oszlopok | Leírás |
|---|---|---|---|
| it_name_key | UNIQUE | name | biztosítja, hogy ugyanaz az interfész típus csak egyszer szerepelhessen |
