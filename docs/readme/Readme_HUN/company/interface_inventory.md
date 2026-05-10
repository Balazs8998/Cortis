# interface_inventory

## Feladata

A céges interfész készlet nyilvántartása.

Tárolja a raktárban található interfészek mennyiségét és helyét, valamint kapcsolatot biztosít a specifikációs adatokhoz az interface_master táblán keresztül.

## Felépítése

| Oszlop | Típus | Megkötések | Leírás |
|---|---|---|---|
| id | UUID | PRIMARY KEY | egyedi sorazonosító |
| interface_code | TEXT | NOT NULL, UNIQUE | céges interfész azonosító |
| name | TEXT | NOT NULL | interfész megnevezése |
| quantity | INTEGER | NOT NULL, CHECK | készlet mennyiség |
| location | TEXT | NOT NULL | raktári hely |
| interface_master_id | UUID | NOT NULL, FOREIGN KEY | hivatkozott interface_master rekord |

## Oszlopok szerepe

### id

Egyedi technikai azonosító minden rekord számára.  
Kapcsolatok és hivatkozások kezelésére szolgál.

### interface_code

A cégen belül használt egyedi interfészazonosító.

### name

Az interfész megnevezése.

### quantity

A rendelkezésre álló készlet mennyisége.

### location

Az interfész raktári vagy tárolási helye.

### interface_master_id

Hivatkozás a specification.interface_master rekord azonosítójára.

Kapcsolatot biztosít a specifikációs adatokkal.

## Megkötések

| Név | Típus | Oszlopok | Leírás |
|---|---|---|---|
| ii_interface_master_id_fkey | FOREIGN KEY | interface_master_id | kapcsolatot biztosít a specification.interface_master táblával |
| ii_quantity_check | CHECK | quantity | biztosítja, hogy a készlet mennyisége nem lehet negatív |
| ii_interface_code_key | UNIQUE | interface_code | biztosítja, hogy ugyanaz az interfészazonosító csak egyszer szerepelhessen |