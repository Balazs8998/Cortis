import { Component, OnInit, inject, signal } from '@angular/core';
import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { FormsModule } from '@angular/forms';
import { AuthSessionService } from '../../auth/auth-session.service';
import { hasAuthority } from '../../shared/access';

type OrderStatus = 'NEW' | 'IN_PROGRESS' | 'READY' | 'SHIPPED' | 'CANCELLED';
type Scope = 'all' | 'thisMonth' | 'overdue';
interface OrderView {
  id: string;
  orderCode: string;
  customer: string;
  orderDate: string;
  deliveryDate: string;
  status: OrderStatus;
  itemCount: number;
  note: string | null;
}
interface OrderLine {
  id: string;
  position: number;
  itemCode: string | null;
  description: string;
  quantity: number;
  unit: string;
}
interface OrderDetail { order: OrderView; lines: OrderLine[]; }
interface DraftLine { itemCode: string; description: string; quantity: number; unit: string; }
interface DraftOrder { customer: string; deliveryDate: string; note: string; lines: DraftLine[]; }

function freshDraft(): DraftOrder {
  return { customer: '', deliveryDate: '', note: '', lines: [
    { itemCode: '', description: '', quantity: 1, unit: 'PCS' }
  ] };
}
function orderError(error: unknown): string {
  const status = error instanceof HttpErrorResponse ? error.status : 0;
  switch (status) {
    case 0: return 'A szerver nem érhető el. Ellenőrizd a proxyt és a Spring Bootot.';
    case 400: return 'Érvénytelen adat vagy kérés. Ellenőrizd a mezőket és a dátumot.';
    case 401: return 'A munkamenet lejárt. Jelentkezz be újra.';
    case 403: return 'Nincs megfelelő megrendelési jogosultság, vagy CSRF-hiba történt.';
    case 404: return 'A megrendelési API vagy a keresett rekord nem található. Ellenőrizd a Phase 4B backend telepítését.';
    case 409: return 'Ütközés: a megrendelés állapota nem módosítható így.';
    default: return `A megrendelési művelet sikertelen (HTTP ${status}).`;
  }
}

@Component({
  selector: 'app-cortis-orders',
  standalone: true,
  imports: [FormsModule],
  templateUrl: './orders-page.html',
  styleUrls: ['../../shared/workspace.css', './orders-page.css']
})
export class OrdersPage implements OnInit {
  private readonly http = inject(HttpClient);
  readonly auth = inject(AuthSessionService);
  readonly query = signal('');
  readonly scope = signal<Scope>('thisMonth');
  readonly records = signal<OrderView[]>([]);
  readonly loading = signal(false);
  readonly error = signal('');
  readonly checked = signal(false);
  readonly available = signal(false);
  readonly page = signal(0);
  readonly pageSize = 50;
  readonly details = signal<OrderDetail | null>(null);
  readonly detailsLoading = signal(false);
  readonly creating = signal(false);
  readonly changing = signal(false);
  readonly success = signal('');
  newOrder: DraftOrder = freshDraft();
  readonly statusDraft: Record<string, OrderStatus> = {};

  readonly canRead = () => hasAuthority(this.auth.user(), 'CORTIS_ORDERS_READ');
  readonly canWrite = () => hasAuthority(this.auth.user(), 'CORTIS_ORDERS_WRITE');

  ngOnInit(): void { this.search(); }

  setScope(next: Scope): void {
    this.scope.set(next); this.page.set(0); this.search();
  }
  search(resetPage = false): void {
    if (resetPage) this.page.set(0);
    if (!this.canRead()) {
      this.records.set([]);
      this.error.set('Nincs CORTIS_ORDERS_READ jogosultság a megrendelések megtekintéséhez.');
      return;
    }
    this.error.set(''); this.success.set('');
    this.loading.set(true); this.checked.set(true);
    this.http.get<OrderView[]>('/api/v1/orders', {
      params: { q: this.query().trim(), scope: this.scope(), limit: this.pageSize, offset: this.page() * this.pageSize }
    }).subscribe({
      next: data => {
        this.records.set(data); this.available.set(true); this.loading.set(false);
      },
      error: error => {
        this.records.set([]); this.loading.set(false); this.available.set(false);
        this.error.set(orderError(error));
      }
    });
  }
  next(): void {
    if (this.loading() || this.records().length < this.pageSize) return;
    this.page.update(current => current + 1);
    this.search();
  }
  previous(): void {
    if (this.loading() || this.page() === 0) return;
    this.page.update(current => current - 1);
    this.search();
  }
  openDetails(order: OrderView): void {
    this.detailsLoading.set(true); this.error.set('');
    this.http.get<OrderDetail>(`/api/v1/orders/${order.id}`).subscribe({
      next: detail => { this.details.set(detail); this.detailsLoading.set(false); },
      error: error => { this.detailsLoading.set(false); this.error.set(orderError(error)); }
    });
  }
  closeDetails(): void { this.details.set(null); }
  addLine(): void {
    if (this.newOrder.lines.length >= 100) return;
    this.newOrder.lines.push({ itemCode: '', description: '', quantity: 1, unit: 'PCS' });
  }
  removeLine(index: number): void {
    if (this.newOrder.lines.length > 1) this.newOrder.lines.splice(index, 1);
  }
  createOrder(): void {
    if (!this.canWrite() || this.creating()) return;
    this.error.set(''); this.success.set(''); this.creating.set(true);
    const body = {
      customer: this.newOrder.customer.trim(),
      deliveryDate: this.newOrder.deliveryDate,
      note: this.newOrder.note.trim() || null,
      lines: this.newOrder.lines.map(line => ({
        itemCode: line.itemCode.trim() || null,
        description: line.description.trim(), quantity: line.quantity, unit: line.unit.toUpperCase()
      }))
    };
    this.http.post<OrderView>('/api/v1/orders', body).subscribe({
      next: created => {
        this.creating.set(false); this.newOrder = freshDraft();
        this.scope.set('all'); this.query.set(created.orderCode); this.page.set(0);
        this.search(); this.success.set(`Megrendelés létrehozva: ${created.orderCode}`);
      },
      error: error => { this.creating.set(false); this.error.set(orderError(error)); }
    });
  }
  transitionOptions(status: OrderStatus): OrderStatus[] {
    switch (status) {
      case 'NEW': return ['IN_PROGRESS', 'CANCELLED'];
      case 'IN_PROGRESS': return ['READY', 'CANCELLED'];
      case 'READY': return ['SHIPPED', 'IN_PROGRESS', 'CANCELLED'];
      case 'SHIPPED': case 'CANCELLED': return [];
    }
  }
  changeStatus(order: OrderView): void {
    if (!this.canWrite() || this.changing()) return;
    const next = this.statusDraft[order.id] ?? this.transitionOptions(order.status)[0];
    if (!next) return;
    this.changing.set(true); this.error.set(''); this.success.set('');
    this.http.patch<OrderView>(`/api/v1/orders/${order.id}/status`, { status: next }).subscribe({
      next: updated => {
        this.changing.set(false);
        delete this.statusDraft[order.id];
        if (this.details()?.order.id === order.id) this.openDetails(updated);
        this.search(); this.success.set(`${updated.orderCode}: ${updated.status}`);
      },
      error: error => { this.changing.set(false); this.error.set(orderError(error)); }
    });
  }
}
