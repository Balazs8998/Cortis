import { UiTranslationService } from '../../shared/i18n/ui-translation.service';
import { Component, inject, OnInit, signal } from '@angular/core';
import { forkJoin } from 'rxjs';
import { AuthSessionService } from '../../auth/auth-session.service';
import { WorkspaceDataService, STORAGE_CATEGORIES } from '../../shared/workspace-data.service';
import { hasAuthority } from '../../shared/access';
import type { Kind, InventoryItem } from '../../cortis/business-api.service';

interface CategoryBar { label: string; code: string; total: number; percentage: number; count: number; truncated: boolean; }

@Component({
  selector: 'app-cortis-statistics',
  standalone: true,
  templateUrl: './statistics-page.html',
  styleUrls: ['../../shared/workspace.css', './statistics-page.css']
})
export class StatisticsPage implements OnInit {
  readonly tr = inject(UiTranslationService);
  private readonly data = inject(WorkspaceDataService);
  private readonly auth = inject(AuthSessionService);
  readonly loading = signal(false);
  readonly error = signal('');
  readonly bars = signal<CategoryBar[]>([]);
  ngOnInit(): void { this.load(); }
  load(): void {
    if (!hasAuthority(this.auth.user(), 'CORTIS_INVENTORY_READ')) { this.error.set('Raktári statisztikákhoz CORTIS_INVENTORY_READ jogosultság szükséges.'); return; }
    this.loading.set(true); this.error.set('');
    const kinds: Kind[] = ['tools', 'holders', 'interfaces'];
    forkJoin(kinds.map(kind => this.data.inventory(kind, 100))).subscribe({
      next: (groups: InventoryItem[][]) => {
        const totals = groups.map(group => group.reduce((sum, row) => sum + (row.quantity ?? 0), 0));
        const max = Math.max(1, ...totals);
        this.bars.set(groups.map((group, i) => ({
          label: STORAGE_CATEGORIES.find(category => category.kind === kinds[i])?.label ?? kinds[i],
          code: kinds[i], total: totals[i], count: group.length, truncated: group.length >= 100,
          percentage: Math.round(totals[i] / max * 100)
        })));
        this.loading.set(false);
      },
      error: () => { this.error.set(this.tr.t('statistics.error')); this.loading.set(false); }
    });
  }
}
