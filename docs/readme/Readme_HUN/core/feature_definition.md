# feature_definition

## Feladata:

### Fizikai és technológiai feature definíciók tárolása.

---

## Felépítése

| Oszlop | Típus |  Megkötések | Leírás |
|---|-------|------|--|
| id | UUID  | PRIMARY KEY | egyedi sorazonosító |
| name | TEXT | NOT NULL, UNIQUE | feature neve  |
| unit | TEXT | NOT NULL | mértékegység |
| data_type | TEXT | NOT NULL | érték adattípusa |

---

## Oszlopok szerepe

### id

Egyedi technikai azonosító minden rekord számára.  
Kapcsolatok és hivatkozások kezelésére szolgál.

### name

A feature megnevezése.  
Például: diameter, length, angle.

UNIQUE megkötést használ, hogy ugyanaz a feature definíció ne szerepelhessen többször.

### unit

A feature mértékegysége.  
Például: mm, degree, rpm.

### data_type

Meghatározza, hogy a feature milyen típusú értéket tárol.  
Például: text, number, boolean.