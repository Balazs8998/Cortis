# entity_feature_value

## Feladata:

### Az entitások feature értékeinek tárolása.

---

## Felépítése

| Oszlop | Típus | Megkötések | Leírás |
|---|---|---|---|
| id | UUID | PRIMARY KEY | egyedi sorazonosító |
| entity_id | UUID | NOT NULL | hivatkozott entitás azonosító |
| entity_feature_id | UUID | NOT NULL, FOREIGN KEY | hivatkozott entity_feature azonosító |
| value_number | NUMERIC | - | numerikus feature érték |
| value_text | TEXT | - | szöveges feature érték |

---
## Oszlopok szerepe

### id

Egyedi technikai azonosító minden rekord számára.  
Kapcsolatok és hivatkozások kezelésére szolgál.

### entity_id

Hivatkozás az entitás azonosítójára.  
Biztosítja, hogy a feature érték kapcsolódjon egy konkrét entitáshoz.

### entity_feature_id

Hivatkozás az entity_feature rekord azonosítójára.  
Meghatározza, hogy az adott érték mely feature definícióhoz tartozik.

### value_number

Numerikus feature érték tárolása.

### value_text

Szöveges feature érték tárolása.

---

## Megkötések

| Név | Típus | Oszlopok | Leírás |
|---|---|---|---|
| efv_pkey | PRIMARY KEY | id | egyedi rekordazonosítót biztosít |
| efv_text_or_number_check | CHECK | value_text, value_number | biztosítja, hogy legalább az egyik értékmező ki legyen töltve |
| efv_entity_id_entity_feature_id_key | UNIQUE | entity_id, entity_feature_id | biztosítja, hogy egy entitás ugyanahhoz a feature-höz csak egyszer kapcsolódhasson |
| efv_entity_feature_id_fkey | FOREIGN KEY | entity_feature_id | kapcsolatot biztosít a core.entity_feature táblával |
