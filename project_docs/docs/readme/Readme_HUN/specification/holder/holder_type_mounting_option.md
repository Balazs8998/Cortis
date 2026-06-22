# holder_type_mounting_option

## Feladata

A tartó típusokhoz tartozó szerelési opciók tárolása.

Meghatározza, hogy egy adott tartó típus milyen szerelési követelmény alapján kapcsolódhat szerszámhoz, interfészhez vagy más befogási elemhez.

## Felépítése

| Oszlop | Típus | Megkötések | Leírás |
|---|---|---|---|
| id | UUID | PRIMARY KEY | egyedi sorazonosító |
| type_id | UUID | NOT NULL, FOREIGN KEY | hivatkozott tartó típus |
| option_name | TEXT | NOT NULL | szerelési opció neve |
| requirement_basis | core.holder_clamping_requirement | NOT NULL, UNIQUE | szerelési követelmény alapja |

## Oszlopok szerepe

### id

Egyedi technikai azonosító minden rekord számára.  
Kapcsolatok és hivatkozások kezelésére szolgál.

### type_id

Hivatkozás a tartó típus azonosítójára.

Kapcsolatot biztosít a specification.holder_type táblával.

### option_name

A szerelési opció megnevezése.

Például: collet clamping, hydraulic clamping, side lock clamping.

### requirement_basis

Meghatározza, hogy a szerelési opció milyen követelmény alapján kerül értelmezésre.

Például: round, square

## Megkötések

| Név | Típus | Oszlopok | Leírás |
|---|---|---|---|
| htmo_type_id_requirement_basis_key | UNIQUE | type_id, requirement_basis | biztosítja, hogy egy tartó típushoz ugyanaz a követelményalap csak egyszer szerepelhessen |
| htmo_type_id_id_key | UNIQUE | type_id, id | összetett hivatkozási alapot biztosít más táblák számára |
| htmo_holder_type_id_fkey | FOREIGN KEY | type_id | kapcsolatot biztosít a specification.holder_type táblával |
