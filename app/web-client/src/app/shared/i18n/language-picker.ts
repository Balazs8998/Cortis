import { Component, inject, OnInit } from '@angular/core';
import { UiLanguage, UiTranslationService } from './ui-translation.service';

@Component({
  selector: 'app-cortis-language-picker',
  standalone: true,
  template: `
    <label class="cortis-language-control">
      <span>{{ i18n.t('settings.language') }}</span>
      <select [value]="i18n.language()" (change)="setLanguage($any($event.target).value)"
              [attr.aria-label]="i18n.t('settings.language_change')">
        <option value="hu">Magyar</option>
        <option value="de">Deutsch</option>
        <option value="en">English</option>
      </select>
    </label>
  `,
  styles: [`
    :host{display:block}
    .cortis-language-control{display:flex;align-items:center;justify-content:flex-end;gap:10px;flex-wrap:wrap;color:#b7d4e5;font-size:12px}
    .cortis-language-control select{min-width:132px;border:1px solid #34546f;border-radius:9px;padding:8px 12px;background:#102b40;color:#f1fbff;cursor:pointer}
    .cortis-language-control select:focus-visible{outline:2px solid #4ed0eb;outline-offset:2px}
  `]
})
export class CortisLanguagePicker implements OnInit {
  readonly i18n = inject(UiTranslationService);
  ngOnInit(): void { this.i18n.ensureLoaded(); }
  setLanguage(value: string): void {
    if (value === 'hu' || value === 'de' || value === 'en') this.i18n.change(value as UiLanguage);
  }
}
