# Schemas

### Az adatbázis több sémára van osztva:
- modularitás
- jobb átláthatóság
- felelősségi körök elkülönítése
- könnyebb karbantarthatóság
- jobb olvashatóság
- részleges újraépíthetőség

###  Sémák áttekintés 
| Séma | cél |
|---|---|
| core | Rendszer táblák, közös definíciók |
| specification | Domain specifikációk, tulajdonságok és szabályok |
| company | Céges adatok és raktárinformációk |
| map | Kapcsoló- és szabálytáblák |
| archiv | Inaktivált egységek és archivált adatok |
| personal | Felhasználók és jogosultságok |
| translation | Többnyelvű rendszerhez szükséges fordítási táblák |


 ---

## Core

### A core séma a rendszer alapvető és közösen használt definíciós tábláit tartalmazza.

### Feladata:
- közös definíciók biztosítása
- egységes struktúra kialakítása
- rendszer szintű alapadatok tárolása

### Példák:
- feature definíciók
- enum jellegű táblák
- rendszer szintű metaadatok

---

## Specification

### A specification séma a domain egységek specifikációit, tulajdonságait és alapvető szabályait tartalmazza.

### Feladata:
- domainenként rendszerezni a tulajdonságokat és szabályokat
- egységes domain struktúra kialakítása

### Példák:
- type definíciók
- master adatok
- mounting option definíciók
- mounting feature requirement táblák
- entity feature value táblák

---

## Company

### A company séma a céges készlet- és üzemi adatokat tartalmazza, amelyek domain specifikus master táblákon keresztül kapcsolódnak a rendszerhez.

### Feladata:
- céges készletadatok tárolása
- céges működési adatok tárolása
- a privát technológiai és használati adatok leválasztása a közös rendszerről

### Példák:
- inventory táblák
- order táblák
- statisztikai táblák (később)

---

## Map

### A map séma a rendszer kapcsoló-, párosító- és szabálytábláit tartalmazza.

### Feladata:
- rendszer szintű szabályok definiálása
- domain specifikus kapcsoló- és segédszabályok tárolása

### Megjegyzés:
A séma jelenleg előkészített struktúraként szolgál a későbbi kapcsoló- és szabályrendszerek számára.

---

## Archiv

### Az archiv séma a már nem aktív céges domain entitások, statisztikák és kapcsolódó adatok tárolására szolgál.

### Feladata:
- már nem használt céges adatok archiválása
- régi statisztikai adatok tárolása
- lezárt vagy inaktív megrendelések adatainak mentése

---

## Personal

### A personal séma a cég alkalmazottainak azonosítására, jogosultságainak és feladatköreinek kezelésére, valamint a szoftver hozzáférési szintjeinek meghatározására szolgál.

### Feladata:
- felhasználók azonosítása
- jogosultságok és tevékenységi körök kezelése
- munkaidő és szünetek követése (később)
- részlegek közötti munkafolyamatok követése (később)
- alkalmazotti státusz és terheltség követése (később)
- távollétek és azok okainak nyilvántartása (később)

### Példák:
- user adatok
- role definíciók
- permission definíciók
- user_role kapcsolatok
- role_permission kapcsolatok

---

## Translation

### A translation séma a szoftver többnyelvű működését és a fordítási rendszer bővíthetőségét biztosítja.

### Feladata:
- a szoftver és adatbázis kimeneti szövegeinek többnyelvű kezelése

### Példák:
- language definíciók
- keyword adatok
- category definíciók
- központi text kapcsolótábla

---

# Általános meta mezők

Az adatbázis táblák egy része közös audit és életciklus követő mezőket tartalmaz.

| Oszlop | Leírás |
|---|---|
| created_by | létrehozó felhasználó |
| created_at | létrehozás időpontja |
| updated_by | utolsó módosítást végző felhasználó |
| updated_at | utolsó módosítás időpontja |
| deleted_at | logikai törlés időpontja |