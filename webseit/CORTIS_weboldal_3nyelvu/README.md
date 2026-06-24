# CORTIS webes főoldal

Ez egy statikus, publikálható, háromnyelvű bemutatóoldal HTML, CSS és JavaScript használatával.

## Fájlok

- `index.html` – az oldal felépítése
- `styles.css` – teljes reszponzív megjelenés
- `script.js` – magyar, német és angol nyelvváltás, mobilmenü, animációk, számlálók és demóinterakciók

## Helyi megnyitás

Az `index.html` fájl közvetlenül is megnyitható böngészőben.

Fejlesztéshez célszerű egy egyszerű helyi szervert használni:

```bash
python3 -m http.server 8000
```

Ezután:

```text
http://localhost:8000
```

## Publikálás

A három fájl feltölthető például GitHub Pages, Netlify, Vercel vagy bármely hagyományos webtárhely használatával.

## Fontos

A kapcsolatfelvételi űrlap jelenleg csak frontend demó. Valódi küldéshez backend végpontra vagy űrlapszolgáltatásra kell kötni.

## Nyelvek

A fejlécben a `HU`, `DE` és `EN` gombokkal váltható a nyelv. A böngésző megjegyzi az utolsó választást.
