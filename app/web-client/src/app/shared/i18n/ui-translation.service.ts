import { Injectable, inject, signal } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { catchError, finalize, map, Observable, of, shareReplay, tap } from 'rxjs';

export type UiLanguage = 'hu' | 'de' | 'en';
export type UiTranslationParams = Record<string, string | number | boolean | null | undefined>;
interface TranslationResponse { translations: Record<string, string>; }

const FALLBACK: Record<string, string> = {
  'login.title': 'Bejelentkezés',
  'login.button.submit': 'Bejelentkezés',
  'login.username.label': 'Felhasználónév',
  'login.password.label': 'Jelszó',
  'login.enter': 'Belépés',
  'login.form_intro': 'Add meg a felhasználóneved és a jelszavad.',
  'login.username.prompt': 'Add meg a felhasználóneved',
  'login.password.prompt': 'Add meg a jelszavad',
  'login.show_password': 'Jelszó megjelenítése',
  'login.no_registration': 'Nincs publikus regisztráció.',
  'errors.login_unauthorized': 'Hibás felhasználónév vagy jelszó, vagy lejárt a munkamenet.',
  'errors.session_expired': 'A munkamenet lejárt. Jelentkezz be újra.',
  'errors.backend_unreachable_login': 'A CORTIS backend nem érhető el.',
  'navigation.logout': 'Kijelentkezés',
  'common.button.close': 'Bezárás',
};
const LANGUAGES: readonly UiLanguage[] = ['hu', 'de', 'en'];
const STORAGE_KEY = 'cortis.ui.language';

function localeFromStorage(): UiLanguage {
  try {
    const value = typeof localStorage === 'undefined' ? null : localStorage.getItem(STORAGE_KEY);
    return LANGUAGES.includes(value as UiLanguage) ? value as UiLanguage : 'hu';
  } catch { return 'hu'; }
}

/** PostgreSQL (/api/translation/hu|de|en) is the authoritative source for interface copy. */
@Injectable({ providedIn: 'root' })
export class UiTranslationService {
  private readonly http = inject(HttpClient);
  readonly language = signal<UiLanguage>(localeFromStorage());
  readonly loading = signal(false);
  readonly lastError = signal('');
  private readonly catalogs = signal<Partial<Record<UiLanguage, Record<string, string>>>>({});
  private readonly requests = new Map<UiLanguage, Observable<Record<string, string>>>();
  private started = false;
  private warned = new Set<string>();

  ensureLoaded(): void {
    if (this.started) return;
    this.started = true;
    this.fetch('hu').subscribe();
    if (this.language() !== 'hu') this.fetch(this.language()).subscribe();
  }

  change(language: UiLanguage): void {
    if (!LANGUAGES.includes(language)) return;
    this.ensureLoaded();
    this.language.set(language); // Latest selection always wins, even with out-of-order responses.
    try { if (typeof localStorage !== 'undefined') localStorage.setItem(STORAGE_KEY, language); } catch { /* optional preference */ }
    this.fetch(language).subscribe();
  }

  private fetch(language: UiLanguage): Observable<Record<string, string>> {
    const cached = this.catalogs()[language];
    if (cached) return of(cached);
    const inFlight = this.requests.get(language);
    if (inFlight) return inFlight;
    this.loading.set(true);
    const source$ = this.http.get<TranslationResponse>(`/api/translation/${language}`).pipe(
      map(response => response.translations ?? {}),
      tap(translations => {
        this.catalogs.update(current => ({ ...current, [language]: translations }));
        this.lastError.set('');
      }),
      catchError(() => {
        this.lastError.set('TRANSLATION_API_UNAVAILABLE');
        return of({});
      }),
      finalize(() => {
        this.requests.delete(language);
        this.loading.set(this.requests.size > 0);
      }),
      shareReplay({ bufferSize: 1, refCount: false }),
    );
    this.requests.set(language, source$);
    return source$;
  }

  t(key: string, params: UiTranslationParams = {}): string {
    const language = this.language();
    const value = this.catalogs()[language]?.[key]
      ?? this.catalogs().hu?.[key]
      ?? FALLBACK[key];
    if (!value) {
      if (!this.warned.has(key)) {
        this.warned.add(key);
        // A missing key is a development/seed problem; never display undefined.
        console.warn('[CORTIS i18n] Missing:', key);
      }
      return key;
    }
    return value.replace(/\{([A-Za-z][A-Za-z0-9_]*)\}/g,
      (_whole, name: string) => String(params[name] ?? `{${name}}`));
  }

  date(value: string | null | undefined): string {
    if (!value) return '—';
    const date = /^\d{4}-\d{2}-\d{2}$/.test(value) ? new Date(`${value}T12:00:00`) : new Date(value);
    if (Number.isNaN(date.getTime())) return value;
    return new Intl.DateTimeFormat(this.language(), { day: '2-digit', month: '2-digit', year: 'numeric' }).format(date);
  }

  number(value: number | null | undefined, maximumFractionDigits = 3): string {
    return value == null ? '—' : new Intl.NumberFormat(this.language(), { maximumFractionDigits }).format(value);
  }
}
