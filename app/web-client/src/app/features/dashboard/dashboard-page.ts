import { Component, inject, OnInit, signal } from '@angular/core';
import { RouterLink } from '@angular/router';
import { forkJoin } from 'rxjs';
import { AuthSessionService } from '../../auth/auth-session.service';
import { WorkspaceDataService, STORAGE_CATEGORIES } from '../../shared/workspace-data.service';
import { hasAuthority } from '../../shared/access';
import type { InventoryItem, Kind } from '../../cortis/business-api.service';

interface StockMetric { kind: Kind; label: string; rows: number; quantity: number | null; truncated: boolean; }

@Component({
  selector: 'app-cortis-dashboard',
  standalone: true,
  imports: [RouterLink],
  templateUrl: './dashboard-page.html',
  styleUrls: ['../../shared/workspace.css', './dashboard-page.css']
})
export class DashboardPage implements OnInit {
  private readonly data = inject(WorkspaceDataService);
  readonly auth = inject(AuthSessionService);
  readonly loading = signal(false);
  readonly error = signal('');
  readonly metrics = signal<StockMetric[]>([]);
  readonly hasRead = () => hasAuthority(this.auth.user(), 'CORTIS_INVENTORY_READ');

  ngOnInit(): void { this.refresh(); }

  refresh(): void {
    if (!this.hasRead()) { this.error.set('A raktári áttekintéshez CORTIS_INVENTORY_READ jogosultság kell.'); return; }
    this.loading.set(true); this.error.set('');
    const kinds: Kind[] = ['tools', 'holders', 'interfaces', 'machines'];
    forkJoin(kinds.map(kind => this.data.inventory(kind, 100))).subscribe({
      next: groups => {
        this.metrics.set(groups.map((items: InventoryItem[], i) => ({
          kind: kinds[i], label: STORAGE_CATEGORIES.find(category => category.kind === kinds[i])?.label ?? kinds[i],
          rows: items.length, truncated: items.length === 100,
          quantity: kinds[i] === 'machines' ? null : items.reduce((sum, item) => sum + (item.quantity ?? 0), 0)
        })));
        this.loading.set(false);
      },
      error: () => { this.error.set('A raktári adatok nem tölthetők be. Ellenőrizd a jogosultságokat és a backend kapcsolatát.'); this.loading.set(false); }
    });
  }
}
