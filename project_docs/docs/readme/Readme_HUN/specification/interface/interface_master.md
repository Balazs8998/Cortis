# interface_master

## Feladata

Az interfész entitás központi táblája.
Központi hivatkozási pontként biztosítja az adatok közötti referenciális integritást.
Emellett általános adatokat tárol az interfészről.

## Felépítése

| Oszlop | Típus | Megkötések | Leírás |
|---|---|---|---|
| id | UUID | PRIMARY KEY | egyedi sorazonosító |
| manufacturer_code | TEXT | NOT NULL | gyártói azonosító |
| type_id | UUID | NOT NULL, FOREIGN KEY | hivatkozott interfész típus |
| manufacturer | TEXT | NOT NULL | gyártó neve |
| description | TEXT | - | interfész leírása |
| order_code | TEXT | - | rendelési azonosító |
| link | TEXT | - | külső hivatkozás |
| catalog | TEXT | - | katalógus információ |

## Oszlopok szerepe

### id

Egyedi technikai azonosító minden rekord számára.  
Kapcsolatok és hivatkozások kezelésére szolgál.

### manufacturer_code

A gyártó által megadott interfész azonosító.

### type_id

Hivatkozás az interfész típus azonosítójára.

Kapcsolatot biztosít a specification.interface_type táblával.

### manufacturer

Az interfész gyártójának neve.

### description

Kiegészítő leírás vagy megjegyzés az interfészhez.

### order_code

A gyártó által használt rendelési azonosító.

### link

Közvetlen hivatkozás a gyártó termékoldalára.

### catalog

A gyártói katalógus azonosítója vagy oldalszáma.

## Megjegyzés

A jövőben a manufacturer, order_code, link és catalog mezők külön táblába kerülhetnek normalizálási célból.


## Megkötések

| Név | Típus | Oszlopok | Leírás |
|---|---|---|---|
| im_interface_type_id_fkey | FOREIGN KEY | type_id | kapcsolatot biztosít a specification.interface_type táblával |
