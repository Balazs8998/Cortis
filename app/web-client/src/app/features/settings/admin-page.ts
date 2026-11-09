import { UiTranslationService } from '../../shared/i18n/ui-translation.service';
import { Component, inject} from '@angular/core';

@Component({
  selector: 'app-cortis-admin',
  standalone: true,
  template: `<section style="padding:24px;border:1px solid #38546b;border-radius:12px;background:#1b293a;color:#d9eaf5;font-family:system-ui">
    <h2>{{ tr.t('admin.title') }}</h2><p>{{ tr.t('admin.not_ready') }}</p>
  </section>`
})
export class AdminPage {
  readonly tr = inject(UiTranslationService);}
