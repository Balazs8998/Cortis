# tool_mounting_feature_requirement

## Feladata

A szerszám szerelési opciókhoz tartozó szükséges feature követelmények tárolása.

Meghatározza, hogy egy adott szerelési opcióhoz mely feature-ök szükségesek a kompatibilitás ellenőrzéséhez.

## Felépítése

| Oszlop | Típus | Megkötések | Leírás |
|---|---|---|---|
| id | UUID | PRIMARY KEY | egyedi sorazonosító |
| mounting_option_id | UUID | NOT NULL, FOREIGN KEY | hivatkozott szerelési opció |
| requirement_feature_id | UUID | NOT NULL, FOREIGN KEY | szükséges feature definíció |

## Oszlopok szerepe

### id

Egyedi technikai azonosító minden rekord számára.  
Kapcsolatok és hivatkozások kezelésére szolgál.

### mounting_option_id

Hivatkozás a szerszám szerelési opciójára.

Kapcsolatot biztosít a specification.tool_type_mounting_option táblával.

### requirement_feature_id

Hivatkozás a szükséges feature azonosítójára.

Meghatározza, hogy az adott szerelési opcióhoz mely feature szükséges.

## Megkötések

| Név | Típus | Oszlopok | Leírás |
|---|---|---|---|
| tmfr_tool_mounting_feature_requirement_id_fkey | FOREIGN KEY | mounting_option_id | kapcsolatot biztosít a specification.tool_type_mounting_option táblával |
| tmfr_entity_feature_id_fkey | FOREIGN KEY | requirement_feature_id | kapcsolatot biztosít a core.entity_feature táblával |
