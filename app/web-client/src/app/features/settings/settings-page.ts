import { Component, inject } from '@angular/core';
import { AuthSessionService } from '../../auth/auth-session.service';

@Component({
  selector: 'app-cortis-settings',
  standalone: true,
  template: `
  <div class="page-heading"><p class="eyebrow">EINSTELLUNGEN</p><h2>Beállítások</h2></div>
  <section class="panel"><h3>Aktív munkamenet</h3><p>Felhasználó: <strong>{{ auth.user()?.userName }}</strong></p>
    <p class="muted">A session HTTP-only cookie-ban van; nincs JWT a böngészőben.</p>
    <p class="muted">Új profilbeállításokat a következő kiadásban lehet módosítani.</p>
  </section>`,
  styles: [`:host{display:block;color:#e6eff8;font-family:system-ui}.page-heading{margin-bottom:20px}.page-heading h2{font-size:24px}.eyebrow{font-size:11px;letter-spacing:.13em;color:#56c2dd}.panel{padding:24px;border:1px solid #35516a;border-radius:12px;background:#1a2a3a}.muted{color:#aabfd3;font-size:13px}`]
})
export class SettingsPage { readonly auth = inject(AuthSessionService); }
