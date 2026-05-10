# tool_inventory

## Feladata

A céges szerszámkészlet nyilvántartása.

Tárolja a raktárban található szerszámok mennyiségét és helyét, valamint kapcsolatot biztosít a specifikációs adatokhoz a tool_master táblán keresztül.

## Felépítése

| Oszlop | Típus | Megkötések | Leírás                               |
|---|---|---|--------------------------------------|
| id | UUID | PRIMARY KEY | egyedi sorazonosító                  |
| tool_code | TEXT | NOT NULL, UNIQUE | céges szerszám azonosító             |
| name | TEXT | NOT NULL | szerszám megnevezése                 |
| quantity | INTEGER | NOT NULL, CHECK | készlet mennyiség                    |
| location | TEXT | NOT NULL | raktári hely                         |
| tool_master_id | UUID | NOT NULL, FOREIGN KEY | hivatkozott szerszám master rekord |

## Oszlopok szerepe

### id

Egyedi technikai azonosító minden rekord számára.  
Kapcsolatok és hivatkozások kezelésére szolgál.

### tool_code

A cégen belül használt egyedi szerszámazonosító.

### name

A szerszám megnevezése.

### quantity

A rendelkezésre álló készlet mennyisége.

### location

A szerszám raktári vagy tárolási helye.

### tool_master_id

Hivatkozás a specification.tool_master rekord azonosítójára.

Kapcsolatot biztosít a specifikációs adatokkal.

## Megkötések

| Név | Típus | Oszlopok | Leírás |
|---|---|---|---|
| ti_tool_master_id_fkey | FOREIGN KEY | tool_master_id | kapcsolatot biztosít a specification.tool_master táblával |
| ti_quantity_check | CHECK | quantity | biztosítja, hogy a készlet mennyisége nem lehet negatív |
| ti_tool_code_key | UNIQUE | tool_code | biztosítja, hogy ugyanaz a szerszámazonosító csak egyszer szerepelhessen |
