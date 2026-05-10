# tool_master

## Feladata:

A szerszám entitás központi táblája.
Központi hivatkozási pontként biztosítja az adatok közötti referenciális integritást.
Emellett általános adatokat tárol a szerszámról.

---

## Felépítése

| Oszlop | Típus | Megkötések | Leírás |
|---|---|---|---|
| id | UUID | PRIMARY KEY | egyedi sorazonosító |
| manufacturer_code | TEXT | NOT NULL | gyártói azonosító |
| type_id | UUID | NOT NULL, FOREIGN KEY | hivatkozott szerszámtípus |
| mounting_option_id | UUID | NOT NULL, FOREIGN KEY | hivatkozott szerelési opció |
| manufacturer | TEXT | NOT NULL | gyártó neve |
| description | TEXT | - | szerszám leírása |
| order_code | TEXT | - | rendelési azonosító |
| link | TEXT | - | külső hivatkozás |
| catalog | TEXT | - | katalógus információ |

---

## Oszlopok szerepe

### id

Egyedi technikai azonosító minden rekord számára.  
Kapcsolatok és hivatkozások kezelésére szolgál.

### manufacturer_code

A gyártó által megadott szerszámazonosító.

### type_id

Hivatkozás a szerszámtípus azonosítójára.
Kapcsolatot biztosít a specification.tool_type táblával, amely meghatározza a szerszám típusát.

### mounting_option_id

Hivatkozás a szerszám szerelési opciójára.
Biztosítja, hogy a szerszám a megfelelő szerelési követelményekhez kapcsolódjon.

### manufacturer

A szerszám gyártójának neve.

### description

Kiegészítő leírás vagy megjegyzés a szerszámhoz.

### order_code

A gyártó által használt rendelési azonosító.

### link

Közvetlen hivatkozás a gyártó termékoldalára.

### catalog

A gyártói katalógus azonosítója vagy oldalszáma.

## Megjegyzés

A jövőben a manufacturer, order_code, link és catalog mezők külön táblába kerülhetnek normalizálási célból.

---

## Megkötések

| Név | Típus | Oszlopok | Leírás |
|---|---|---|---|
| tm_mounting_option_id_fkey | FOREIGN KEY | type_id, mounting_option_id | biztosítja, hogy a szerelési opció az adott szerszámtípushoz tartozzon |
| tm_tool_type_id_fkey | FOREIGN KEY | type_id | kapcsolatot biztosít a specification.tool_type táblával |
