import { Injectable, inject, signal } from '@angular/core';
import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { Router } from '@angular/router';
import { Observable, catchError, finalize, map, of, switchMap, tap, throwError } from 'rxjs';

export interface CortisSessionUser {
  userId: string;
  userName: string;
  authenticated: boolean;
  authorities: string[];
}

interface CsrfResponse {
  headerName: string;
  token: string;
}

function authErrorMessage(error: unknown): string {
  if (!(error instanceof HttpErrorResponse)) return 'Váratlan hiba történt.';
  switch (error.status) {
    case 0: return 'A CORTIS backend nem érhető el. Ellenőrizd a Spring Bootot és az Angular proxyt.';
    case 400: return 'Hibás bejelentkezési adatok.';
    case 401: return 'Hibás felhasználónév vagy jelszó, vagy lejárt a munkamenet.';
    case 403: return 'A kérés elutasítva (CSRF vagy jogosultsági hiba).';
    case 429: return 'Túl sok próbálkozás. Próbáld újra később.';
    default: return `Szerverhiba (HTTP ${error.status}).`;
  }
}

@Injectable({ providedIn: 'root' })
export class AuthSessionService {
  private readonly http = inject(HttpClient);
  private readonly router = inject(Router);

  readonly user = signal<CortisSessionUser | null>(null);
  readonly loading = signal(false);
  readonly checked = signal(false);
  readonly error = signal('');

  /** Never read or store the session cookie/ID in JavaScript. HttpOnly session is browser-managed. */
  loadMe(): Observable<CortisSessionUser> {
    return this.http.get<CortisSessionUser>('/api/auth/me').pipe(
      tap(user => {
        this.user.set(user);
        this.checked.set(true);
      }),
      catchError(error => {
        this.user.set(null);
        this.checked.set(true);
        return throwError(() => error);
      })
    );
  }

  /** Route guards also cover browser refresh/direct navigation, not only in-app clicks. */
  ensureSignedIn(): Observable<boolean> {
    if (this.user()?.authenticated) return of(true);
    return this.loadMe().pipe(
      map(user => user.authenticated),
      catchError(() => of(false))
    );
  }

  signIn(userName: string, password: string): Observable<CortisSessionUser> {
    const normalizedUserName = userName.trim();
    if (!normalizedUserName || !password) {
      this.error.set('Add meg a felhasználónevet és a jelszót.');
      return throwError(() => new Error('Missing username or password'));
    }
    this.loading.set(true);
    this.error.set('');
    return this.http.get<CsrfResponse>('/api/auth/csrf').pipe(
      switchMap(() => this.http.post<CortisSessionUser>('/api/auth/login', {
        userName: normalizedUserName, password
      })),
      // Login changes the session ID and clears the old CSRF cookie.
      switchMap(() => this.http.get<CsrfResponse>('/api/auth/csrf')),
      switchMap(() => this.loadMe()),
      catchError(error => {
        // If login succeeded but a later request failed, a valid session could still exist.
        // A future /me call reconciles state with the server; never fabricate auth state.
        this.user.set(null);
        this.checked.set(false);
        this.error.set(authErrorMessage(error));
        return throwError(() => error);
      }),
      finalize(() => this.loading.set(false))
    );
  }

  signOut(): Observable<void> {
    this.loading.set(true);
    this.error.set('');
    return this.http.get<CsrfResponse>('/api/auth/csrf').pipe(
      switchMap(() => this.http.post<void>('/api/auth/logout', {})),
      tap(() => this.forgetLocalSession()),
      // Logout clears cookies. Refresh the anonymous CSRF cookie best-effort;
      // if this GET fails, the next sign-in will request a fresh token anyway.
      switchMap(() => this.http.get<CsrfResponse>('/api/auth/csrf').pipe(
        map(() => void 0),
        catchError(() => of(void 0))
      )),
      catchError(error => {
        if (error instanceof HttpErrorResponse && error.status === 401) {
          this.forgetLocalSession();
          return of(void 0);
        }
        this.error.set(authErrorMessage(error));
        return throwError(() => error);
      }),
      finalize(() => this.loading.set(false))
    );
  }

  forgetLocalSession(): void {
    this.user.set(null);
    this.checked.set(true);
  }

  routeHome(): void {
    void this.router.navigateByUrl('/');
  }
}
