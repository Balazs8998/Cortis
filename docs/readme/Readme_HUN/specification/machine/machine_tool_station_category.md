# machine_tool_station_category

## Feladata

A gépek szerszámállomás kategóriáinak tárolása.

Meghatározza, hogy egy adott géptípus milyen állomás típusokkal rendelkezik, valamint hogy az adott állomás interfész vagy tartó kezelésére szolgál-e.

## Felépítése

| Oszlop | Típus | Megkötések | Leírás |
|---|---|---|---|
| id | UUID | PRIMARY KEY | egyedi sorazonosító |
| type_id | UUID | NOT NULL, FOREIGN KEY | hivatkozott géptípus |
| station_type | core.machine_station_type | NOT NULL | állomás típus |
| name | TEXT | NOT NULL | állomás kategória neve |
| is_interface | BOOLEAN | NOT NULL | interfész támogatás jelölése |
| is_holder | BOOLEAN | NOT NULL | tartó támogatás jelölése |

## Oszlopok szerepe

### id

Egyedi technikai azonosító minden rekord számára.  
Kapcsolatok és hivatkozások kezelésére szolgál.

### type_id

Hivatkozás a géptípus azonosítójára.

Kapcsolatot biztosít a specification.machine_type táblával.

### station_type

Meghatározza a gépállomás típusát.

Például: turret, magazine, spindle.

### name

Az állomás kategória megnevezése.

### is_interface

Meghatározza, hogy az adott állomás interfész elemek kezelésére alkalmas-e.

### is_holder

Meghatározza, hogy az adott állomás tartók kezelésére alkalmas-e.

## Megkötések

| Név | Típus | Oszlopok | Leírás |
|---|---|---|---|
| mtsc_machine_type_id_fkey | FOREIGN KEY | type_id | kapcsolatot biztosít a specification.machine_type táblával |
| mtsc_type_id_station_type_key | UNIQUE | type_id, station_type | biztosítja, hogy egy géptípuson belül ugyanaz az állomás típus csak egyszer szerepelhessen |
