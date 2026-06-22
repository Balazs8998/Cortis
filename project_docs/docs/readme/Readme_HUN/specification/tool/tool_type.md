# tool_type

## Feladata:

### Meghatározza a szerszámok típusát

---

## Felépítése

| Oszlop | Típus | Megkötések | Leírás |
|---|---|---|---|
| id | UUID | PRIMARY KEY | egyedi sorazonosító |
| name | TEXT | NOT NULL, UNIQUE | szerszámtípus neve |
| description | TEXT | - | szerszámtípus leírása |

---

## Oszlopok szerepe

### id

Egyedi technikai azonosító minden rekord számára.  
Kapcsolatok és hivatkozások kezelésére szolgál.

### name

A szerszámtípus megnevezése.

### description

A szerszámtípus leírása.

---

## Megkötések

| Név | Típus | Oszlopok | Leírás |
|---|---|---|---|
| tt_name_key | UNIQUE | name | biztosítja, hogy ugyanaz a szerszámtípus név csak egyszer szerepelhessen |
