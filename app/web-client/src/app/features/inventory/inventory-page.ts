import { Component, computed, inject, OnInit, signal } from '@angular/core';
import { FormsModule, ReactiveFormsModule, FormBuilder, Validators } from '@angular/forms';
import { HttpErrorResponse } from '@angular/common/http';
import { forkJoin } from 'rxjs';
import { AuthSessionService } from '../../auth/auth-session.service';
import { WorkspaceDataService, STORAGE_CATEGORIES, StorageCategory, InventoryMovement, CatalogType } from '../../shared/workspace-data.service';
import { hasAuthority } from '../../shared/access';
import type { Master, InventoryItem } from '../../cortis/business-api.service';

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
  private requestGeneration = 0;
  readonly auth = inject(AuthSessionService);
  readonly categories = STORAGE_CATEGORIES;
  readonly selectedCategory = signal<StorageCategory>(STORAGE_CATEGORIES[0]!);
  readonly masters = signal<Master[]>([]);
  readonly catalogTypes = signal<CatalogType[]>([]);
  readonly stock = signal<InventoryItem[]>([]);
  readonly movements = signal<InventoryMovement[]>([]);
  readonly selectedStock = signal<InventoryItem | null>(null);
  readonly search = signal('');
  readonly loading = signal(false);
  readonly saving = signal(false);
  readonly confirmingArchive = signal(false);
  readonly error = signal('');
  readonly success = signal('');
  readonly pageOffset = signal(0);
  readonly pageSize = 100;
  readonly action = signal<'IN' | 'OUT' | 'CORRECTION'>('IN');
  readonly canRead = computed(() => hasAuthority(this.auth.user(), 'CORTIS_INVENTORY_READ') && hasAuthority(this.auth.user(), 'CORTIS_CATALOG_READ'));
  readonly canWrite = computed(() => hasAuthority(this.auth.user(), 'CORTIS_INVENTORY_WRITE'));
  readonly canCatalogWrite = computed(() => hasAuthority(this.auth.user(), 'CORTIS_CATALOG_WRITE'));
  readonly canArchive = computed(() => hasAuthority(this.auth.user(), 'CORTIS_INVENTORY_DELETE'));

  readonly quantityForm = this.fb.nonNullable.group({
    amount: [1, [Validators.required, Validators.min(0), Validators.pattern(/^\d+$/)]],
    reason: ['', [Validators.required, Validators.minLength(3), Validators.maxLength(500)]]
  });
  readonly typeForm = this.fb.nonNullable.group({
    name: ['', [Validators.required, Validators.maxLength(150)]]
  });
  readonly masterForm = this.fb.nonNullable.group({
    typeId: ['', Validators.required],
    manufacturerCode: ['', [Validators.required, Validators.maxLength(200)]],
    manufacturer: ['', [Validators.required, Validators.maxLength(200)]],
    description: ['', Validators.maxLength(2000)]
  });
  readonly newStockForm = this.fb.nonNullable.group({
    masterId: ['', Validators.required],
    name: ['', [Validators.required, Validators.maxLength(250)]],
    location: ['pending', [Validators.required, Validators.maxLength(250)]],
    quantity: [0, [Validators.required, Validators.min(0), Validators.pattern(/^\d+$/)]]
  });
  readonly visibleStock = computed(() => {
    const query = this.search().toLocaleLowerCase('hu-HU').trim();
    if (!query) return this.stock();
    return this.stock().filter(item => [item.code, item.name, item.location, item.masterCode]
      .some(value => value?.toLocaleLowerCase('hu-HU').includes(query)));
  });

  ngOnInit(): void { this.refresh(); }
  choose(category: StorageCategory): void {
    if (this.saving()) return;
    this.typeForm.reset();
    this.masterForm.reset();
    this.newStockForm.reset();
    this.newStockForm.controls.location.setValue('pending');
    this.selectedCategory.set(category);
    this.pageOffset.set(0);
    this.search.set('');
    this.selectedStock.set(null);
    this.confirmingArchive.set(false);
    this.movements.set([]);
    this.error.set(''); this.success.set('');
    this.refresh();
  }
  refresh(): void {
    const generation = ++this.requestGeneration;
    const kind = this.selectedCategory().kind;
    if (!this.canRead()) { this.error.set('A raktárhoz CORTIS_CATALOG_READ és CORTIS_INVENTORY_READ szükséges.'); return; }
    this.loading.set(true); this.error.set('');
    forkJoin({
      types: this.data.types(kind),
      masters: this.data.catalog(kind, '', this.pageSize, this.pageOffset()),
      stock: this.data.inventory(kind, this.pageSize, this.pageOffset())
    }).subscribe({
      next: response => {
        if (generation !== this.requestGeneration) return;
        this.catalogTypes.set(response.types); this.masters.set(response.masters);
        this.stock.set(response.stock); this.loading.set(false);
      },
      error: (e: unknown) => {
        if (generation !== this.requestGeneration) return;
        this.error.set(this.message(e)); this.loading.set(false);
      }
    });
  }
  previous(): void { this.pageOffset.update(value => Math.max(0, value - this.pageSize)); this.refresh(); }
  next(): void { if (this.stock().length < this.pageSize) return; this.pageOffset.update(value => value + this.pageSize); this.refresh(); }
  select(item: InventoryItem): void {
    this.selectedStock.set(item); this.movements.set([]); this.confirmingArchive.set(false);
    const kind = this.selectedCategory().kind;
    if (this.selectedCategory().stocked) {
      this.data.movements(kind, item.id).subscribe({
        next: rows => { if (this.selectedStock()?.id === item.id) this.movements.set(rows); },
        error: e => this.error.set(this.message(e))
      });
    }
  }
  setAction(value: 'IN' | 'OUT' | 'CORRECTION'): void {
    this.action.set(value); this.quantityForm.controls.amount.setValue(1);
  }
  saveMovement(): void {
    const item = this.selectedStock();
    const kind = this.selectedCategory().kind;
    if (!item || !this.selectedCategory().stocked || !this.canWrite() || this.quantityForm.invalid || this.saving()) return;
    const {amount, reason} = this.quantityForm.getRawValue();
    const current = item.quantity ?? 0;
    if (this.action() !== 'CORRECTION' && amount <= 0) { this.error.set('Pozitív mennyiség szükséges.'); return; }
    const delta = this.action() === 'IN' ? amount : this.action() === 'OUT' ? -amount : amount - current;
    if (!delta) { this.error.set('A készlet mennyisége nem változna.'); return; }
    if (current + delta < 0) { this.error.set('Negatív készlet nem engedélyezett.'); return; }
    this.saving.set(true); this.error.set(''); this.success.set('');
    this.data.adjust(kind, item.id, delta, reason).subscribe({
      next: updated => {
        this.selectedStock.set(updated);
        this.success.set(`Készletmozgás rögzítve: ${updated.code}`);
        this.saving.set(false); this.refresh(); this.select(updated);
      },
      error: e => { this.saving.set(false); this.error.set(this.message(e)); }
    });
  }
  createType(): void {
    if (!this.canCatalogWrite() || this.typeForm.invalid || this.saving()) return;
    this.saving.set(true); this.error.set('');
    this.data.createType(this.selectedCategory().kind, this.typeForm.getRawValue().name).subscribe({
      next: created => {
        this.catalogTypes.update(types => [...types, created]);
        this.masterForm.controls.typeId.setValue(created.id);
        this.typeForm.reset(); this.saving.set(false);
        this.success.set('Típus létrehozva.');
      }, error: e => { this.saving.set(false); this.error.set(this.message(e)); }
    });
  }
  createMaster(): void {
    if (!this.canCatalogWrite() || this.masterForm.invalid || this.saving()) return;
    this.saving.set(true); this.error.set('');
    this.data.createMaster(this.selectedCategory().kind, this.masterForm.getRawValue()).subscribe({
      next: created => {
        this.masters.update(rows => [...rows, created]);
        this.newStockForm.controls.masterId.setValue(created.id);
        this.masterForm.reset(); this.saving.set(false);
        this.success.set('Törzsadat létrehozva. Most felvehetsz egy raktári tételt.');
      }, error: e => { this.saving.set(false); this.error.set(this.message(e)); }
    });
  }
  createInventory(): void {
    if (!this.canWrite() || this.newStockForm.invalid || this.saving()) return;
    this.saving.set(true); this.error.set('');
    const raw = this.newStockForm.getRawValue();
    const data = this.selectedCategory().stocked ? raw : {
      masterId: raw.masterId, name: raw.name, location: raw.location
    };
    this.data.createInventory(this.selectedCategory().kind, data).subscribe({
      next: created => {
        this.saving.set(false); this.newStockForm.reset();
        this.newStockForm.controls.location.setValue('pending');
        this.success.set(`Tétel létrehozva: ${created.code} (a backend osztotta ki).`);
        this.refresh(); this.select(created);
      }, error: e => { this.saving.set(false); this.error.set(this.message(e)); }
    });
  }
  archiveSelected(): void {
    const item = this.selectedStock();
    if (!item || !this.canArchive() || !this.confirmingArchive() || this.saving()) return;
    this.saving.set(true); this.error.set('');
    this.data.archive(this.selectedCategory().kind, item.id).subscribe({
      next: () => {
        this.saving.set(false); this.confirmingArchive.set(false);
        this.selectedStock.set(null); this.movements.set([]);
        this.success.set(`Archiválva: ${item.code}. A mozgásnapló megmaradt.`);
        this.refresh();
      }, error: e => { this.saving.set(false); this.error.set(this.message(e)); }
    });
  }
  private message(error: unknown): string {
    const status = error instanceof HttpErrorResponse ? error.status : 0;
    if (status === 401) return 'A munkamenet lejárt. Jelentkezz be újra.';
    if (status === 403) return 'A művelethez nincs megfelelő jogosultság.';
    if (status === 409) return 'Ütközés: már létező adat, nem üres készlet vagy érvénytelen mennyiség.';
    if (status === 400) return 'Hibás vagy hiányos adatok. Ellenőrizd a mezőket.';
    if (status === 0) return 'A backend nem érhető el. Ellenőrizd az Angular proxyt.';
    return `Kérés sikertelen (HTTP ${status}).`;
  }
}
