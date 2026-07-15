# Cortis Architektúra

## 1. A projekt célja

A Cortis egy saját fejlesztésű teljes körű vállalatirányítási és raktárkezelő rendszer, amelynek hosszú távú célja szerszámok, befogók, gépek, kompatibilitások, raktárkészlet, felhasználók, jogosultságok, fordítások és audit adatok kezelése.

A fejlesztés jelenlegi szakaszában a hangsúly még nem az üzleti logikán, hanem egy stabil és újra felhasználható magrendszer (Core) kialakításán van.

Jelenlegi technológiák:

* Backend: Spring Boot
* Frontend: JavaFX Desktop
* Adatbázis: PostgreSQL
* Hitelesítés: JWT
* Jogosultságkezelés: Role + Permission alapú
* Fordítás: Backend által kiszolgált fordítási rendszer

---

# 2. Rétegzett architektúra

A backend klasszikus rétegzett felépítést követ.

```text
Controller
    ↓
Service
    ↓
Repository
    ↓
Database
```

## Controller

Feladata:

* HTTP kérések fogadása
* DTO-k fogadása és visszaadása
* megfelelő Service meghívása

A Controller nem tartalmaz üzleti logikát.

---

## Service

A Service réteg tartalmazza az alkalmazás üzleti működését.

Feladata:

* hitelesítés
* felhasználók kezelése
* jogosultságok kezelése
* fordítások kezelése
* chip azonosítás
* üzleti szabályok
* naplózás
* saját kivételek dobása

---

## Repository

A Repository kizárólag adatbázis műveleteket végez.

Feladata:

* adatok lekérdezése
* mentése
* keresések
* JPA műveletek

Üzleti logika itt nem szerepelhet.

---

# 3. Hitelesítési folyamat

Bejelentkezési folyamat:

```text
JavaFX Login
        ↓
POST /api/auth/login
        ↓
AuthController
        ↓
AuthService
        ↓
Felhasználó betöltése
        ↓
Jelszó ellenőrzése
        ↓
JWT létrehozása
        ↓
LoginResponse
        ↓
SessionManager
```

Sikeres bejelentkezés után a frontend eltárolja:

* JWT tokent
* felhasználó adatait

Minden további kérésnél a token kerül elküldésre.

---

# 4. JWT működése

Védett végpont esetén:

```text
HTTP kérés
        ↓
JwtAuthenticationFilter
        ↓
Bearer token kiolvasása
        ↓
Token ellenőrzése
        ↓
Felhasználó betöltése
        ↓
Authentication objektum
        ↓
SecurityContext
        ↓
Controller
```

Érvénytelen vagy hiányzó token esetén a kérés elutasításra kerül.

---

# 5. Jogosultsági rendszer

A rendszer Role + Permission alapú.

Felépítés:

```text
User
    ↓
UserRole
    ↓
Role
    ↓
RolePermission
    ↓
Permission
```

Egy felhasználónak több szerepköre lehet.

Egy szerepkör több jogosultságot tartalmazhat.

A Permission objektumok Spring Security Authority-vé alakulnak.

Példák:

```text
USER_READ
USER_CREATE
TOOL_UPDATE
WAREHOUSE_DELETE
```

A cél nem kizárólag ADMIN és USER szerepkörök használata, hanem finomhangolható jogosultsági rendszer kialakítása.

---

# 6. Fordítási rendszer

A fordításokat a backend szolgáltatja.

Működése:

```text
Frontend
      ↓
GET /api/translation/{language}
      ↓
TranslationController
      ↓
TranslationService
      ↓
Adatbázis
      ↓
TranslationResponse
      ↓
Frontend Cache
```

A frontend a teljes nyelvet egyszer tölti be, majd helyi cache-ből dolgozik.

---

# 7. Kivételkezelés

A rendszer globális exception kezelőt használ.

Céljai:

* egységes hibaválasz
* frontend számára fordítható üzenetkulcs
* nyers Java hibák elrejtése
* könnyebb hibakeresés

Példa:

```json
{
    "timestamp": "...",
    "status": 400,
    "errorCode": "...",
    "messageKey": "...",
    "path": "..."
}
```

---

# 8. Naplózás

A naplózás célja az alkalmazás fontos eseményeinek követése.

Példák:

* sikeres bejelentkezés
* sikertelen bejelentkezés
* jogosultsági hibák
* váratlan kivételek
* chip azonosítás
* fontos üzleti műveletek

Nem cél minden metódus minden sorának naplózása.

---

# 9. Chip alapú azonosítás

A rendszer támogatja (illetve támogatni fogja) a chipkód alapú hitelesítést.

A chipkód soha nem kerül nyers formában eltárolásra.

Tervezett működés:

```text
Chip kód
      ↓
HMAC / Hash
      ↓
Adatbázis
      ↓
Felhasználó keresése
      ↓
Hitelesítés
```

A chipes azonosítás elkülönül a jelszavas bejelentkezéstől, de ugyanazt a hitelesített állapotot hozza létre.

---

# 10. Frontend

A frontend JavaFX alapú.

Feladatai:

* bejelentkezés
* JWT kezelése
* SessionManager
* fordítások betöltése
* API hívások
* főmenü
* widgetek
* üzleti felületek

A frontend nem tartalmaz üzleti logikát.

---

# 11. Adatbázis

Az adatbázis több sémára van bontva.

Jelenlegi sémák:

```text
core
company
map
personal
specification
translation
archive
```

Például:

Personal:

* users
* roles
* permissions
* user_roles
* role_permissions

Translation:

* language
* category
* keyword
* text

---

# 12. Core modulok

A jelenlegi magrendszer fő moduljai:

```text
auth
security
user
role
permission
translation
exception
logging
chip
configuration
```

Minden későbbi üzleti modul ezekre épül.

---

# 13. Verziók

## V0

Stabil alap:

* JWT
* jogosultságok
* fordítás
* globális exception
* logolás
* első üzleti modulok

---

## V1

Biztonsági fejlesztések:

* ideiglenes jelszó generálása
* első belépés utáni kötelező jelszócsere
* jelszó lejárati szabály
* token lejárat kezelése
* audit napló
* chip hitelesítés továbbfejlesztése

---

## V2

További biztonsági funkciók:

* jelszó visszaállítás
* rate limiting
* login lock
* refresh token
* eszközkezelés
* jogosultság kezelő felület
* admin biztonsági modul

---

# 14. Fejlesztési alapelvek

* A Controller maradjon vékony.
* Az üzleti logika mindig a Service rétegben legyen.
* A Repository csak adatbázissal foglalkozzon.
* API kommunikáció DTO-kon keresztül történjen.
* Entity közvetlenül ne kerüljön a frontend felé.
* Minden kivétel a Global Exception Handleren keresztül menjen.
* Biztonsági adat (jelszó, token, chipkód) soha ne kerüljön naplózásra.
* Minden új funkció implementálása előtt el kell dönteni, hogy V0, V1 vagy V2 része lesz.

---

# 15. Jelenlegi prioritás

A jelenlegi cél nem a végtelen Core bővítése.

A következő lépések:

1. A jelenlegi Core rendbetétele.
2. A hitelesítési folyamat stabilizálása.
3. A fordítási rendszer véglegesítése.
4. Az első üzleti logika implementálása.
5. A további biztonsági funkciók áthelyezése a V1 fejlesztési szakaszba.
