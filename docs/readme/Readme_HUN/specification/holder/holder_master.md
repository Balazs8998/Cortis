# holder_master

## Feladata

A tartó entitás központi táblája.

Központi hivatkozási pontként biztosítja az adatok közötti referenciális integritást.

Emellett általános adatokat tárol a tartóról.

## Felépítése

| Oszlop | Típus | Megkötések | Leírás |
|---|---|---|---|
| id | UUID | PRIMARY KEY | egyedi sorazonosító |
| manufacturer_code | TEXT | NOT NULL | gyártói azonosító |
| type_id | UUID | NOT NULL, FOREIGN KEY | hivatkozott tartó típus |
| mounting_option_id | UUID | NOT NULL, FOREIGN KEY | hivatkozott szerelési opció |
| manufacturer | TEXT | NOT NULL | gyártó neve |
| description | TEXT | - | tartó leírása |
| link | TEXT | - | külső hivatkozás |
| catalog | TEXT | - | katalógus információ |
| order_code | TEXT | - | rendelési azonosító |

## Oszlopok szerepe

### id

Egyedi technikai azonosító minden rekord számára.  
Kapcsolatok és hivatkozások kezelésére szolgál.

### manufacturer_code

A gyártó által megadott tartó azonosító.

### type_id

Hivatkozás a tartó típus azonosítójára.

Kapcsolatot biztosít a specification.holder_type táblával.

### mounting_option_id

Hivatkozás a tartó szerelési opciójára.

Biztosítja, hogy a tartó a megfelelő szerelési követelményekhez kapcsolódjon.

### manufacturer

A tartó gyártójának neve.

### description

Kiegészítő leírás vagy megjegyzés a tartóhoz.

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
| hm_mounting_option_id_fkey | FOREIGN KEY | type_id, mounting_option_id | biztosítja, hogy a szerelési opció az adott tartó típushoz tartozzon |
| hm_holder_type_id_fkey | FOREIGN KEY | type_id | kapcsolatot biztosít a specification.holder_type táblával |

