# tool_type_mounting_option

## Feladata

A szerszámtípusokhoz tartozó szerelési opciók tárolása.

Meghatározza, hogy egy adott szerszámtípus milyen szerelési követelmény alapján kapcsolódhat tartóhoz, interfészhez vagy más befogási elemhez.

## Felépítése

| Oszlop | Típus | Megkötések | Leírás |
|---|---|---|---|
| id | UUID | PRIMARY KEY | egyedi sorazonosító |
| type_id | UUID | NOT NULL, FOREIGN KEY | hivatkozott szerszámtípus |
| option_name | TEXT | NOT NULL | szerelési opció neve |
| requirement_basis | core.tool_mounting_requirement | NOT NULL, UNIQUE | szerelési követelmény alapja |

## Oszlopok szerepe

### id

Egyedi technikai azonosító minden rekord számára.  
Kapcsolatok és hivatkozások kezelésére szolgál.

### type_id

Hivatkozás a szerszámtípus azonosítójára.

Kapcsolatot biztosít a specification.tool_type táblával.

### option_name

A szerelési opció megnevezése.

Például: turning_iso_standard, grooving_system

### requirement_basis

Meghatározza, hogy a szerelési opció milyen követelmény alapján kerül értelmezésre.

Például:  geometry, form, form and geometry

### Megjegyzés:
Egy szerelési opcióhoz több feature követelmény tartozhat.

## Megkötések

| Név | Típus | Oszlopok | Leírás |
|---|---|---|---|
| ttmo_type_id_requirement_basis_key | UNIQUE | type_id, requirement_basis | biztosítja, hogy egy szerszámtípushoz ugyanaz a követelményalap csak egyszer szerepelhessen |
| ttmo_type_id_id_key | UNIQUE | type_id, id | összetett hivatkozási alapot biztosít más táblák számára |
| ttmo_tool_type_id_fkey | FOREIGN KEY | type_id | kapcsolatot biztosít a specification.tool_type táblával |
