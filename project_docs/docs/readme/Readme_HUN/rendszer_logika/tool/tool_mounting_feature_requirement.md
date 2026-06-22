# Tool Mounting Feature Requirement

# Readme generált. még ellenőrizni kell. !

## Áttekintés

A `tool_mounting_feature_requirement` tábla azt definiálja, hogy egy adott
szerszámtípus és szerelési rendszer kompatibilitásának meghatározásához
milyen feature-ök szükségesek.

Ez a réteg a CORTIS kompatibilitási motor része, és szabályalapú kapcsolatot
hoz létre az alábbi elemek között:

- szerszámtípus
- szerelési opció
- szükséges feature-készlet

A cél az, hogy a kompatibilitási logika ne hardkódolt formában létezzen,
hanem konfigurálható metaadat- és feature-rendszer alapján működjön.

---

# Kompatibilitási filozófia

Nem minden szerszámrendszer azonos logika alapján azonosítható.

Egyes rendszerek nagyrészt szabványosítottak, ezért kompatibilitásuk
geometriai és ISO formaadatok alapján meghatározható.

Más rendszerek — különösen a beszúró és leszúró rendszerek —
gyártóspecifikusak, ezért platform- vagy családalapú azonosítást igényelnek.

Ezért a CORTIS többféle kompatibilitási stratégiát támogat.

---

# Mounting Requirement stratégiák

## geometry

A kompatibilitás főként geometriai és méretadatok alapján kerül meghatározásra.

Példák:
- átmérő
- hossz
- kúpszög

Tipikus felhasználás:
- fúrók
- hengeres szerszámok
- egyszerű befogók

---

## form

A kompatibilitás szabványos formai adatok alapján kerül meghatározásra.

Példák:
- lapkaforma
- tűrésosztály
- hátszög

Tipikus felhasználás:
- ISO lapkarendszerek

---

## form_and_geometry

A kompatibilitáshoz egyszerre szükséges:
- szabványos forma
- geometriai méret

Példa:
- lapkacsalád + lapkaméret

---

## manufacturer_and_code

Olyan proprietary vagy félig proprietary rendszerekhez használatos,
ahol a geometria önmagában nem elegendő a kompatibilitás biztos
azonosításához.

A kompatibilitás meghatározása az alábbiak alapján történik:
- gyártó
- mounting family / mounting code
- kritikus referencia méretek

Tipikus felhasználás:
- beszúró rendszerek
- leszúró rendszerek
- gyártóspecifikus lapkarendszerek

Példák:
- Iscar Tang-Grip
- Sandvik CoroCut
- Applitec rendszerek

Valós gyártási környezetben ezeket a rendszereket a gépbeállítók
és programozók jellemzően:
- gyártó,
- lapkaszélesség,
- valamint mounting rendszer

alapján azonosítják, nem pedig tisztán ISO geometria alapján.

---

# Jelenlegi feature requirement definíciók

## Insert — ISO Standard

Mounting option:
`iso_standard`

Szükséges feature-ök:
- insert_shape
- relief_angle
- tolerance_class
- mounting_type
- shape_size

Cél:
Szabványos ISO lapkarendszerek kompatibilitásának meghatározása.

Példák:
- DCMT
- CNMG
- VNMG

A feature-k kombinációja meghatározza:
- a lapkacsaládot,
- valamint a kompatibilis lapkafészket és tartót.

---

## Insert — Grooving System

Mounting option:
`grooving_system`

Szükséges feature-ök:
- shape_width
- manufacturer
- mounting_code

Cél:
Beszúró és leszúró rendszerek kompatibilitásának meghatározása,
ahol az ISO geometria önmagában nem elegendő.

Ezek a rendszerek általában:
- gyártóspecifikusak,
- platformalapúak,
- és mounting family szerint működnek.

---

## Drill — Clamping

Mounting option:
`clamping`

Szükséges feature-ök:
- outer_diameter
- length

Cél:
Szorítással rögzített fúrórendszerek kompatibilitásának meghatározása
geometriai adatok alapján.

---

## Drill — Screw

Mounting option:
`screw`

Szükséges feature-ök:
- outer_diameter
- length

Cél:
Csavaros rögzítésű fúrórendszerek kompatibilitásának meghatározása.

---

## Boring Bar — Nut

Mounting option:
`nut`

Szükséges feature-ök:
- manufacturer
- mounting_code
- size
- outer_diameter

Cél:
Anyás rögzítésű furatkés rendszerek kompatibilitásának meghatározása.

A gyártóspecifikus mounting code szükséges, mivel a geometria önmagában
nem minden esetben elegendő.

---

# Tervezési megjegyzések

A rendszer szándékosan külön kezeli:
- a geometriát,
- a mounting logikát,
- a gyártói ökoszisztémákat,
- valamint a kompatibilitási szabályokat.

A kompatibilitási motor célja, hogy:
- bővíthető,
- konfigurálható,
- és adatvezérelt maradjon.

A jövőben további feature-ök adhatók hozzá a rendszer újratervezése nélkül.

Lehetséges későbbi bővítések:
- clamp_style
- insert_height
- chipbreaker_family
- minimum_bore
- holder_series
- proprietary interface family rendszerek

---

# Architektúrális cél

Ennek a rétegnek a célja:
- a hardkódolt kompatibilitási logika csökkentése,
- több tooling ökoszisztéma támogatása,
- skálázható kompatibilitási rendszer biztosítása,
- későbbi AI-kompatibilitás előkészítése,
- valamint adatvezérelt kompatibilitási szabályrendszer kialakítása.