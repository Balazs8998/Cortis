import { TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { provideRouter } from '@angular/router';
import { AuthSessionService } from './auth-session.service';

describe('AuthSessionService', () => {
  let service: AuthSessionService;
  let http: HttpTestingController;
  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [provideRouter([]), provideHttpClient(), provideHttpClientTesting()]
    });
    service = TestBed.inject(AuthSessionService);
    http = TestBed.inject(HttpTestingController);
  });
  afterEach(() => http.verify());

  it('loads /me and restores a session after page reload', () => {
    const me = { userId: 'a', userName: 'tester', authenticated: true, authorities: [] };
    let allowed = false;
    service.ensureSignedIn().subscribe(value => allowed = value);
    http.expectOne('/api/auth/me').flush(me);
    expect(allowed).toBe(true);
    expect(service.user()?.userName).toBe('tester');
  });

  it('never considers a 401 authenticated', () => {
    let allowed = true;
    service.ensureSignedIn().subscribe(value => allowed = value);
    http.expectOne('/api/auth/me').flush({}, { status: 401, statusText: 'Unauthorized' });
    expect(allowed).toBe(false);
    expect(service.user()).toBeNull();
  });

  it('performs CSRF -> login -> CSRF -> /me in order', () => {
    let completed = false;
    service.signIn(' tester ', 'valid-password').subscribe({next: () => completed = true});
    http.expectOne('/api/auth/csrf').flush({headerName: 'X-XSRF-TOKEN', token: 'one'});
    const login = http.expectOne('/api/auth/login');
    expect(login.request.body).toEqual({userName: 'tester', password: 'valid-password'});
    login.flush({userId:'a', userName:'tester', authenticated:true, authorities: []});
    http.expectOne('/api/auth/csrf').flush({headerName:'X-XSRF-TOKEN', token:'two'});
    http.expectOne('/api/auth/me').flush({userId:'a', userName:'tester', authenticated:true, authorities: []});
    expect(completed).toBe(true);
    expect(service.user()?.authenticated).toBe(true);
  });

  it('performs CSRF -> logout -> CSRF then clears local session', () => {
    let completed = false;
    service.signOut().subscribe({ next: () => completed = true });
    http.expectOne('/api/auth/csrf').flush({headerName:'X-XSRF-TOKEN', token:'one'});
    http.expectOne('/api/auth/logout').flush(null, { status: 204, statusText: 'No Content' });
    http.expectOne('/api/auth/csrf').flush({headerName:'X-XSRF-TOKEN', token:'two'});
    expect(completed).toBe(true);
    expect(service.user()).toBeNull();
  });
});
