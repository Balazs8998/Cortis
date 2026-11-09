import { UiTranslationService } from '../../shared/i18n/ui-translation.service';
import { Component, inject, OnInit, signal } from '@angular/core';
import { RouterLink } from '@angular/router';
import { HttpClient } from '@angular/common/http';
import { AuthSessionService } from '../../auth/auth-session.service';
import { WorkspaceDataService, STORAGE_CATEGORIES } from '../../shared/workspace-data.service';
import { hasAuthority } from '../../shared/access';
import type { Kind } from '../../cortis/business-api.service';

interface OrderOverview { thisMonth: number; overdue: number; inProgress: number; }
interface StockMetric { kind: Kind; label: string; rows: number; quantity: number | null; }

@Component({
  selector: 'app-cortis-dashboard',
  standalone: true,
  imports: [RouterLink],
  templateUrl: './dashboard-page.html',
  styleUrls: ['../../shared/workspace.css', './dashboard-page.css']
})
export class DashboardPage implements OnInit {
  readonly tr = inject(UiTranslationService);
  private readonly data = inject(WorkspaceDataService);
  private readonly http = inject(HttpClient);
  readonly auth = inject(AuthSessionService);
  readonly loading = signal(false);
  readonly error = signal('');
  readonly metrics = signal<StockMetric[]>([]);
  readonly ordersOverview = signal<OrderOverview | null>(null);
  readonly ordersLoading = signal(false);
  readonly ordersError = signal('');
  readonly ordersReadable = () => hasAuthority(this.auth.user(), 'CORTIS_ORDERS_READ');
  readonly hasRead = () => hasAuthority(this.auth.user(), 'CORTIS_INVENTORY_READ');

  ngOnInit(): void { this.refresh(); }

  refresh(): void {
    this.refreshOrders();
    if (!this.hasRead()) { this.error.set('A raktári áttekintéshez CORTIS_INVENTORY_READ jogosultság kell.'); return; }
    this.loading.set(true); this.error.set('');
    this.data.inventorySummary().subscribe({
      next: groups => {
        this.metrics.set(groups.map(item => ({
          kind: item.kind,
          label: STORAGE_CATEGORIES.find(category => category.kind === item.kind)?.label ?? item.kind,
          rows: item.rows,
          quantity: item.quantity
        })));
        this.loading.set(false);
      },
      error: () => { this.error.set('A raktári adatok nem tölthetők be. Ellenőrizd a jogosultságokat és a backend kapcsolatát.'); this.loading.set(false); }
    });
  }
  private refreshOrders(): void {
    if (!this.ordersReadable()) {
      this.ordersOverview.set(null);
      this.ordersError.set('Megrendelés-áttekintéshez CORTIS_ORDERS_READ jogosultság kell.');
      return;
    }
    this.ordersLoading.set(true); this.ordersError.set('');
    this.http.get<OrderOverview>('/api/v1/orders/summary').subscribe({
      next: data => { this.ordersOverview.set(data); this.ordersLoading.set(false); },
      error: () => {
        this.ordersOverview.set(null);
        this.ordersError.set('A megrendelési áttekintés nem tölthető be.');
        this.ordersLoading.set(false);
      }
    });
  }
}
