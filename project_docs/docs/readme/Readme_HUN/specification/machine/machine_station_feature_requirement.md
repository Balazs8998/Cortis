# machine_station_feature_requirement

## Feladata

A gépállomásokhoz tartozó szükséges feature követelmények tárolása.

Meghatározza, hogy egy adott gépállomás milyen feature követelményekkel kompatibilis.

## Felépítése

| Oszlop | Típus | Megkötések | Leírás |
|---|---|---|---|
| id | UUID | PRIMARY KEY | egyedi sorazonosító |
| requirement_feature_id | UUID | NOT NULL, FOREIGN KEY | szükséges feature definíció |
| tool_station_id | UUID | NOT NULL, FOREIGN KEY | hivatkozott gépállomás kategória |

## Oszlopok szerepe

### id

Egyedi technikai azonosító minden rekord számára.  
Kapcsolatok és hivatkozások kezelésére szolgál.

### requirement_feature_id

Hivatkozás a szükséges feature azonosítójára.

Meghatározza, hogy az adott gépállomás milyen feature követelményt használ.

### tool_station_id

Hivatkozás a gépállomás kategória azonosítójára.

Kapcsolatot biztosít a specification.machine_tool_station_category táblával.

## Megkötések

| Név | Típus | Oszlopok | Leírás |
|---|---|---|---|
| msfr_requirement_feature_id_fkey | FOREIGN KEY | requirement_feature_id | kapcsolatot biztosít a core.entity_feature táblával |
| msfr_tool_station_id_fkey | FOREIGN KEY | tool_station_id | kapcsolatot biztosít a specification.machine_tool_station_category táblával |
