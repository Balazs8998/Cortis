# machine_master

## Feladata

A gép entitás központi táblája.

Központi hivatkozási pontként biztosítja az adatok közötti referenciális integritást.

Emellett általános adatokat tárol a gépről.

## Felépítése

| Oszlop | Típus | Megkötések | Leírás |
|---|---|---|---|
| id | UUID | PRIMARY KEY | egyedi sorazonosító |
| manufacturer_code | TEXT | NOT NULL | gyártói azonosító |
| type_id | UUID | NOT NULL, FOREIGN KEY | hivatkozott géptípus |
| manufacturer | TEXT | NOT NULL | gyártó neve |
| description | TEXT | - | gép leírása |
| link | TEXT | - | külső hivatkozás |
| catalog | TEXT | - | katalógus információ |
| order_code | TEXT | - | rendelési azonosító |

## Oszlopok szerepe

### id

Egyedi technikai azonosító minden rekord számára.  
Kapcsolatok és hivatkozások kezelésére szolgál.

### manufacturer_code

A gyártó által megadott gép azonosító.

### type_id

Hivatkozás a géptípus azonosítójára.

Kapcsolatot biztosít a specification.machine_type táblával.

### manufacturer

A gép gyártójának neve.

### description

Kiegészítő leírás vagy megjegyzés a géphez.

### link

Közvetlen hivatkozás a gyártó termékoldalára.

### catalog

A gyártói katalógus azonosítója vagy oldalszáma.

### order_code

A gyártó által használt rendelési azonosító.


## Megjegyzés

A jövőben a manufacturer, order_code, link és catalog mezők külön táblába kerülhetnek normalizálási célból.

## Megkötések

| Név | Típus | Oszlopok | Leírás |
|---|---|---|---|
| mm_pkey | PRIMARY KEY | id | egyedi rekordazonosítót biztosít |
| mm_machine_type_id_fkey | FOREIGN KEY | type_id | kapcsolatot biztosít a specification.machine_type táblával |
