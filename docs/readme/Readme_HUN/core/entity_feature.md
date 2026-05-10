# entity_feature

## Feladata:

### A feature definíciók összekapcsolása entitástípussal és szerepkörrel.

---

## Felépítése

| Oszlop | Típus | Megkötések              | Leírás |
|---|---|-------------------------|---|
| id | UUID | PRIMARY KEY             | egyedi sorazonosító |
| entity_type | core.entity_type | NOT NULL          | entitás típusa |
| feature_id | UUID | NOT NULL, FOREIGN KEY | hivatkozott feature definíció |
| role | core.role_type | NOT NULL          | feature szerepköre |

---

## Oszlopok szerepe

### id

Egyedi technikai azonosító minden rekord számára.  
Kapcsolatok és hivatkozások kezelésére szolgál.


### entity_type
Meghatározza, hogy a feature mely entitástípushoz tartozik.  
Például: interface, tool, holder, machine, machine_station.

### feature_id
A feature_definition tábla id mezőjére hivatkozó FOREIGN KEY.
Meghatározza a kapcsolódó feature definíciót.


### role
Meghatározza, hogy az adott feature milyen szerepet tölt be.
Például : mounting, geometry, constraint

---

## Megkötések

| Név | Típus | Oszlopok | Leírás |
|---|---|---|---|
| ef_feature_id_entity_type_role_key | UNIQUE | feature_id, entity_type, role | megakadályozza a duplikált feature szerepkör hozzárendeléseket |


