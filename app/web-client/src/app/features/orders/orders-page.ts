import { Component, inject, signal } from '@angular/core';
import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { FormsModule } from '@angular/forms';
import { AuthSessionService } from '../../auth/auth-session.service';
import { hasAuthority } from '../../shared/access';

interface OrderView {
  id: string;
  orderCode: string;
  customer: string;
  orderDate: string;
  deliveryDate: string;
  status: 'NEW' | 'IN_PROGRESS' | 'READY' | 'SHIPPED' | 'CANCELLED';
  itemCount: number;
  note: string | null;
}
type Scope = 'all' | 'thisMonth' | 'overdue';

@Component({
  selector: 'app-cortis-orders',
  standalone: true,
  imports: [FormsModule],
  templateUrl: './orders-page.html',
  styleUrls: ['../../shared/workspace.css', './orders-page.css']
})
export class OrdersPage {
  private readonly http = inject(HttpClient);
  readonly auth = inject(AuthSessionService);
  readonly query = signal('');
  readonly scope = signal<Scope>('thisMonth');
  readonly records = signal<OrderView[]>([]);
  readonly loading = signal(false);
  readonly error = signal('');
  readonly checked = signal(false);
  readonly available = signal(false);
  readonly canRead = () => hasAuthority(this.auth.user(), 'CORTIS_ORDERS_READ');

  setScope(next: Scope): void { this.scope.set(next); this.search(); }
  search(): void {
    if (!this.canRead()) {
      this.error.set('A megrendelésekhez CORTIS_ORDERS_READ jogosultság szükséges, amelyet még külön be kell állítani.');
      return;
    }
    this.error.set(''); this.loading.set(true); this.checked.set(true);
    // Contract for the future orders API. Do not simulate an order when endpoint is absent.
    this.http.get<OrderView[]>('/api/v1/orders', {
      params: { q: this.query().trim(), scope: this.scope(), limit: 50 }
    }).subscribe({
      next: data => { this.records.set(data); this.available.set(true); this.loading.set(false); },
      error: (error: unknown) => {
        this.records.set([]); this.loading.set(false);
        const status = error instanceof HttpErrorResponse ? error.status : 0;
        if (status === 404) {
          this.available.set(false);
          this.error.set('A megrendelési backend még nem része a Phase 3A-nak. A keresés és a havi kiszállítások a következő API-csomag után működhetnek.');
        } else if (status === 403) {
          this.error.set('Nincs CORTIS_ORDERS_READ jogosultság.');
        } else {
          this.error.set(`Megrendeléslekérdezés sikertelen (HTTP ${status}).`);
        }
      }
    });
  }
}
