import { Component, computed, inject, OnInit, signal } from '@angular/core';
import { FormsModule, ReactiveFormsModule, FormBuilder, Validators } from '@angular/forms';
import { HttpErrorResponse } from '@angular/common/http';
import { forkJoin } from 'rxjs';
import { AuthSessionService } from '../../auth/auth-session.service';
import { WorkspaceDataService, STORAGE_CATEGORIES, StorageCategory, InventoryMovement } from '../../shared/workspace-data.service';
import { hasAuthority } from '../../shared/access';
import type { Master, InventoryItem, Kind } from '../../cortis/business-api.service';

@Component({
  selector: 'app-cortis-inventory',
  standalone: true,
  imports: [FormsModule, ReactiveFormsModule],
  templateUrl: './inventory-page.html',
  styleUrls: ['../../shared/workspace.css', './inventory-page.css']
})
export class InventoryPage implements OnInit {
  private readonly data = inject(WorkspaceDataService);
  private readonly fb = inject(FormBuilder);
  readonly auth = inject(AuthSessionService);
  readonly categories = STORAGE_CATEGORIES;
  readonly selectedCategory = signal<StorageCategory>(STORAGE_CATEGORIES[0]!);
  readonly masters = signal<Master[]>([]);
  readonly stock = signal<InventoryItem[]>([]);
  readonly movements = signal<InventoryMovement[]>([]);
  readonly selectedStock = signal<InventoryItem | null>(null);
  readonly search = signal('');
  readonly loading = signal(false);
  readonly saving = signal(false);
  readonly error = signal('');
  readonly success = signal('');
  readonly pageOffset = signal(0);
  readonly pageSize = 100;
  readonly action = signal<'IN' | 'OUT' | 'CORRECTION'>('IN');
  readonly quantityForm = this.fb.nonNullable.group({
    amount: [1, [Validators.required, Validators.min(0), Validators.pattern(/^\d+$/)]],
    reason: ['', [Validators.required, Validators.minLength(3), Validators.maxLength(500)]]
  });
  readonly canRead = computed(() => hasAuthority(this.auth.user(), 'CORTIS_INVENTORY_READ') && hasAuthority(this.auth.user(), 'CORTIS_CATALOG_READ'));
  readonly canWrite = computed(() => hasAuthority(this.auth.user(), 'CORTIS_INVENTORY_WRITE'));
  readonly visibleStock = computed(() => {
    const query = this.search().toLocaleLowerCase('hu-HU').trim();
    if (!query) return this.stock();
    return this.stock().filter(item => [item.code, item.name, item.location, item.masterCode].some(value => value?.toLocaleLowerCase('hu-HU').includes(query)));
  });

  ngOnInit(): void { this.refresh(); }
  choose(category: StorageCategory): void {
    this.selectedCategory.set(category);
    this.pageOffset.set(0);
    this.search.set('');
    this.selectedStock.set(null);
    this.movements.set([]);
    this.error.set(''); this.success.set('');
    this.refresh();
  }
  refresh(): void {
    const kind = this.selectedCategory().kind;
    if (!kind) return;
    if (!this.canRead()) { this.error.set('A raktár megtekintéséhez CORTIS_CATALOG_READ és CORTIS_INVENTORY_READ kell.'); return; }
    this.loading.set(true); this.error.set('');
    forkJoin({
      masters: this.data.catalog(kind, '', this.pageSize, this.pageOffset()),
      stock: this.data.inventory(kind, this.pageSize, this.pageOffset())
    }).subscribe({
      next: response => { this.masters.set(response.masters); this.stock.set(response.stock); this.loading.set(false); },
      error: (e: unknown) => { this.error.set(this.message(e)); this.loading.set(false); }
    });
  }
  previous(): void { this.pageOffset.update(value => Math.max(0, value - this.pageSize)); this.refresh(); }
  next(): void { if (this.stock().length < this.pageSize) return; this.pageOffset.update(value => value + this.pageSize); this.refresh(); }
  select(item: InventoryItem): void {
    this.selectedStock.set(item); this.movements.set([]); this.success.set('');
    const kind = this.selectedCategory().kind;
    if (kind && kind !== 'machines') {
      this.data.movements(kind, item.id).subscribe({next: rows => this.movements.set(rows), error: e => this.error.set(this.message(e))});
    }
  }
  setAction(value: 'IN' | 'OUT' | 'CORRECTION'): void { this.action.set(value); this.quantityForm.controls.amount.setValue(1); }
  saveMovement(): void {
    const item = this.selectedStock(); const kind = this.selectedCategory().kind;
    if (!item || !kind || kind === 'machines' || !this.canWrite() || this.quantityForm.invalid || this.saving()) return;
    const {amount, reason} = this.quantityForm.getRawValue();
    const current = item.quantity ?? 0;
    if (this.action() !== 'CORRECTION' && amount <= 0) { this.error.set('A bevételezési és kivételezési mennyiség legyen pozitív.'); return; }
    const delta = this.action() === 'IN' ? amount : this.action() === 'OUT' ? -amount : amount - current;
    if (!delta) { this.error.set('A készletkorrekció nem változtatná meg a mennyiséget.'); return; }
    if (current + delta < 0) { this.error.set('Nem lehet negatív készletet létrehozni.'); return; }
    this.saving.set(true); this.error.set(''); this.success.set('');
    this.data.adjust(kind, item.id, delta, reason).subscribe({
      next: updated => {
        this.selectedStock.set(updated);
        this.success.set(`Készletmódosítás rögzítve: ${updated.code}`);
        this.saving.set(false); this.refresh(); this.select(updated);
      },
      error: e => { this.saving.set(false); this.error.set(this.message(e)); }
    });
  }
  private message(error: unknown): string {
    const status = error instanceof HttpErrorResponse ? error.status : 0;
    if (status === 401) return 'A munkamenet lejárt. Jelentkezz be újra.';
    if (status === 403) return 'Nincs megfelelő jogosultság a kért művelethez.';
    if (status === 409) return 'Készletütközés vagy érvénytelen készletmennyiség.';
    if (status === 0) return 'Nem érhető el a backend vagy hibás a proxy.';
    return `Kérés sikertelen (HTTP ${status}).`;
  }
}
