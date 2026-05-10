# interface_type_mounting_feature_requirement

## Feladata

Az interfész típusokhoz tartozó szükséges feature definíciók tárolása.

Meghatározza, hogy egy adott interfész típushoz mely feature-ök szükségesek a kompatibilitás ellenőrzéséhez.

## Felépítése

| Oszlop | Típus | Megkötések | Leírás |
|---|---|---|---|
| id | UUID | PRIMARY KEY | egyedi sorazonosító |
| type_id | UUID | NOT NULL, FOREIGN KEY | hivatkozott interfész típus |
| requirement_feature_id | UUID | NOT NULL, FOREIGN KEY | szükséges feature definíció |

## Oszlopok szerepe

### id

Egyedi technikai azonosító minden rekord számára.  
Kapcsolatok és hivatkozások kezelésére szolgál.

### type_id

Hivatkozás az interfész típus azonosítójára.

Kapcsolatot biztosít a `specification.interface_type` táblával.

### requirement_feature_id

Hivatkozás a szükséges feature azonosítójára.

Meghatározza, hogy az adott interfész típushoz mely feature szükséges.

## Megkötések

| Név | Típus | Oszlopok | Leírás |
|---|---|---|---|
| itmfr_interface_type_id_fkey | FOREIGN KEY | type_id | kapcsolatot biztosít a `specification.interface_type` táblával |
| itmfr_entity_feature_id_fkey | FOREIGN KEY | requirement_feature_id | kapcsolatot biztosít a `core.entity_feature` táblával |
| itmfr_type_id_requirement_feature_id_key | UNIQUE | type_id, requirement_feature_id | biztosítja, hogy ugyanaz a feature követelmény egy interfész típushoz csak egyszer szerepelhessen |
