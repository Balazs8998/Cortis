# CORTIS

## Magyar verzió

# CORTIS

A CORTIS (CNC Operations Real-Time Intelligent System) egy moduláris, adatvezérelt backend rendszer, amely CNC gyártási környezetek támogatására készült.

A projekt célja egy skálázható és strukturált architektúra biztosítása:

* raktárkezeléshez
* szerszám kompatibilitás ellenőrzéshez
* gép- és erőforrás kezeléshez
* gyártással kapcsolatos adatkezeléshez
* jövőbeli monitorozó és elemző rendszerekhez

A CORTIS adatbázis alapú logikára és domain szétválasztásra épül a hardcodeolt működés helyett.

---

# 🎯 Cél

A CORTIS elsődleges célja egy egységes gyártástámogató rendszer létrehozása, amely képes:

* szerszámok, tartók, interfészek, gépek és anyagok kezelésére
* kompatibilitási kapcsolatok ellenőrzésére
* céges technológiai adatok rendszerezésére
* jövőbeli monitorozó és döntéstámogató rendszerek kiszolgálására
* hosszú távon is skálázható architektúra biztosítására

---

# 🧠 Alapkoncepció

A CORTIS strukturált domain logikára épül.

A rendszer különválasztja:

* a közös rendszer definíciókat
* a domain specifikációkat
* a céges működési adatokat
* a kapcsoló- és szabályrendszereket
* a felhasználó- és jogosultságkezelést
* a többnyelvű támogatást

A kompatibilitás és a rendszerlogika nem hardcodeolt feltételek alapján működik.

A rendszer ehelyett:

* feature definíciókat
* type absztrakciókat
* mapping táblákat
* kompatibilitási szabályokat
* domain kapcsolatokat

használ.

Ez lehetővé teszi, hogy az architektúra rugalmas, bővíthető és újrahasznosítható maradjon.

---

# 🏗️ Adatbázis architektúra

Az adatbázis több sémára van bontva a következők érdekében:

* modularitás
* jobb olvashatóság
* könnyebb karbantarthatóság
* skálázhatóság
* felelősségi körök elkülönítése

## Sémák áttekintése

| Séma          | Cél                                              |
| ------------- | ------------------------------------------------ |
| core          | Rendszer táblák és közös definíciók              |
| specification | Domain specifikációk, tulajdonságok és szabályok |
| company       | Céges adatok és raktárinformációk                |
| map           | Kapcsoló- és szabálytáblák                       |
| archiv        | Inaktív entitások és archivált adatok            |
| personal      | Felhasználók és jogosultságok                    |
| translation   | Fordítási rendszer táblái                        |

---

# ⚙️ Főbb rendszerterületek

## Core

Közös rendszer szintű struktúrákat tartalmaz.

Példák:

* feature definíciók
* enum jellegű struktúrák
* metaadat táblák
* közös alap definíciók

---

## Specification

Domain specifikációkat és szabály definíciókat tartalmaz.

Példák:

* type definíciók
* master adatok
* mounting option definíciók
* feature requirement struktúrák
* entity feature value táblák

---

## Company

Céges működési és raktár adatokat tartalmaz.

Példák:

* inventory táblák
* order táblák
* céges gép- és szerszám adatok
* jövőbeli statisztikai adatok

---

## Map

Kapcsoló-, kompatibilitási- és szabályrendszeri struktúrákat tartalmaz.

Példák:

* mapping táblák
* segéd szabályrendszerek
* kompatibilitási segédtáblák

---

## Archiv

Inaktív vagy történeti céges adatokat tartalmaz.

Példák:

* inaktív entitások
* archivált megrendelések
* történeti statisztikák

---

## Personal

Felhasználó- és jogosultságkezelési struktúrákat tartalmaz.

Példák:

* users
* roles
* permissions
* user-role kapcsolatok

---

## Translation

A rendszer többnyelvű támogatását biztosítja.

Példák:

* language definíciók
* fordítási keywordök
* kategóriák
* translation text kapcsolatok

---

# 📂 Projekt struktúra

```text
cortis/
├── app/
│   ├── core/
│   ├── domain/
│   ├── services/
│   ├── repositories/
│   └── api/
│
├── data/
├── scripts/
├── docs/
├── test/
└── alembic/
```

---

# 🚧 Jelenlegi állapot

A fejlesztés jelenleg az alábbi területekre fókuszál:

* adatbázis architektúra
* séma szétválasztás
* domain modellezés
* kompatibilitási struktúrák kialakítása
* dokumentáció
* backend újrastrukturálás

A projekt jelenleg aktív architektúra- és backend fejlesztési fázisban van.

---

# 🔜 Tervezett funkciók

A jövőben tervezett rendszerek:

* kompatibilitás ellenőrző rendszer
* inventory workflow kezelés
* gyártás monitorozás
* futási statisztikák
* szerszám használat követés
* gép teljesítmény elemzés
* riport rendszerek
* API és backend szolgáltatások

---

# ⚠️ Megjegyzés

A CORTIS nem egy egyszerű CRUD alkalmazásnak készül.

A hosszú távú cél egy skálázható gyártástámogató és döntéstámogató rendszer létrehozása, amely képes komplex ipari workflow-k és kompatibilitási logikák kezelésére.

---
