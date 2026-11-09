import { UiTranslationService } from '../../shared/i18n/ui-translation.service';
import { CortisLanguagePicker } from '../../shared/i18n/language-picker';
import { Component, inject } from '@angular/core';
import { AuthSessionService } from '../../auth/auth-session.service';

@Component({
  imports: [CortisLanguagePicker],
  selector: 'app-cortis-settings',
  standalone: true,
  template: `
  <div class="page-heading"><p class="eyebrow">{{ tr.t('settings.eyebrow') }}</p><h2>{{ tr.t('settings.title') }}</h2></div>
  <section class="panel"><app-cortis-language-picker /><h3>{{ tr.t('settings.active_session') }}</h3><p>Felhasználó: <strong>{{ auth.user()?.userName }}</strong></p>
    <p class="muted">{{ tr.t('settings.session_info') }}</p>
    <p class="muted">{{ tr.t('settings.profile_soon') }}</p>
  </section>`,
  styles: [`:host{display:block;color:#e6eff8;font-family:system-ui}.page-heading{margin-bottom:20px}.page-heading h2{font-size:24px}.eyebrow{font-size:11px;letter-spacing:.13em;color:#56c2dd}.panel{padding:24px;border:1px solid #35516a;border-radius:12px;background:#1a2a3a}.muted{color:#aabfd3;font-size:13px}`]
})
export class SettingsPage {
  readonly tr = inject(UiTranslationService); readonly auth = inject(AuthSessionService); }
