# machine_inventory

## Feladata

A céges gépkészlet nyilvántartása.

Tárolja a rendszerben használt gépek helyét, valamint kapcsolatot biztosít a specifikációs adatokhoz a machine_master táblán keresztül.

## Felépítése

| Oszlop | Típus | Megkötések | Leírás |
|---|---|---|---|
| id | UUID | PRIMARY KEY | egyedi sorazonosító |
| machine_code | TEXT | NOT NULL, UNIQUE | céges gépazonosító |
| name | TEXT | NOT NULL | gép megnevezése |
| location | TEXT | NOT NULL | gép helye |
| machine_master_id | UUID | NOT NULL, FOREIGN KEY | hivatkozott machine_master rekord |

## Oszlopok szerepe

### id

Egyedi technikai azonosító minden rekord számára.  
Kapcsolatok és hivatkozások kezelésére szolgál.

### machine_code

A cégen belül használt egyedi gépazonosító.

### name

A gép megnevezése.

### location

A gép aktuális helye vagy telepítési pozíciója.

### machine_master_id

Hivatkozás a specification.machine_master rekord azonosítójára.

Kapcsolatot biztosít a specifikációs adatokkal.

## Megkötések

| Név | Típus | Oszlopok | Leírás |
|---|---|---|---|
| mi_machine_master_id_fkey | FOREIGN KEY | machine_master_id | kapcsolatot biztosít a specification.machine_master táblával |
| mi_machine_code_key | UNIQUE | machine_code | biztosítja, hogy ugyanaz a gépazonosító csak egyszer szerepelhessen |