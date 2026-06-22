# holder_inventory

## Feladata

A céges tartó készlet nyilvántartása.

Tárolja a raktárban található tartók mennyiségét és helyét, valamint kapcsolatot biztosít a specifikációs adatokhoz a holder_master táblán keresztül.

## Felépítése

| Oszlop | Típus | Megkötések | Leírás |
|---|---|---|---|
| id | UUID | PRIMARY KEY | egyedi sorazonosító |
| holder_code | TEXT | NOT NULL, UNIQUE | céges tartó azonosító |
| name | TEXT | NOT NULL | tartó megnevezése |
| quantity | INTEGER | NOT NULL, CHECK | készlet mennyiség |
| location | TEXT | NOT NULL | raktári hely |
| holder_master_id | UUID | NOT NULL, FOREIGN KEY | hivatkozott holder_master rekord |

## Oszlopok szerepe

### id

Egyedi technikai azonosító minden rekord számára.  
Kapcsolatok és hivatkozások kezelésére szolgál.

### holder_code

A cégen belül használt egyedi tartóazonosító.

### name

A tartó megnevezése.

### quantity

A rendelkezésre álló készlet mennyisége.

### location

A tartó raktári vagy tárolási helye.

### holder_master_id

Hivatkozás a specification.holder_master rekord azonosítójára.

Kapcsolatot biztosít a specifikációs adatokkal.

## Megkötések

| Név | Típus | Oszlopok | Leírás |
|---|---|---|---|
| hi_holder_master_id_fkey | FOREIGN KEY | holder_master_id | kapcsolatot biztosít a specification.holder_master táblával |
| hi_quantity_check | CHECK | quantity | biztosítja, hogy a készlet mennyisége nem lehet negatív |
| hi_holder_code_key | UNIQUE | holder_code | biztosítja, hogy ugyanaz a tartóazonosító csak egyszer szerepelhessen |