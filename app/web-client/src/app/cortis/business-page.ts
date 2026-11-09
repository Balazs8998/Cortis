import { Component, inject, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { HttpErrorResponse } from '@angular/common/http';
import { BusinessApiService, CompatibilityResult, InventoryItem, Kind, Master } from './business-api.service';

@Component({
  selector: 'app-cortis-business',
  standalone: true,
  imports: [FormsModule],
  template: `
  <main class="cortis">
    <header><h1>CORTIS · V1</h1><span>Catalog / Inventory / Engineering evidence</span></header>
    <section class="panel auth">
      <label>Username <input type="text" [(ngModel)]="username" autocomplete="username"></label>
      <label>Password <input type="password" [(ngModel)]="password" autocomplete="current-password"></label>
      <button type="button" (click)="login()">Sign in</button>
      <button type="button" (click)="logout()">Sign out</button>
      <span>Session cookies · CSRF</span>
    </section>
    @if (error()) { <p class="error" role="alert">{{ error() }}</p> }
    <section class="panel">
      <h2>Asset catalogs and inventory</h2>
      <nav>
        @for (kind of api.kinds; track kind) {
          <button type="button" [class.selected]="selected() === kind" (click)="setKind(kind)">{{ kind }}</button>
        }
        <button type="button" (click)="refresh()">Refresh</button>
      </nav>
      <div class="columns">
        <section><h3>Master data</h3>
          <table><thead><tr><th>Code</th><th>Manufacturer</th><th>Type</th></tr></thead><tbody>
            @for (item of masters(); track item.id) {
              <tr><td>{{ item.manufacturerCode }}</td><td>{{ item.manufacturer }}</td><td>{{ item.typeName }}</td></tr>
            }
          </tbody></table>
        </section>
        <section><h3>Stock / machines</h3>
          <table><thead><tr><th>Code</th><th>Name</th><th>Qty</th><th>Location</th></tr></thead><tbody>
            @for (item of stock(); track item.id) {
              <tr (click)="selectedStock.set(item)" [class.selected]="selectedStock()?.id === item.id">
                <td>{{ item.code }}</td><td>{{ item.name }}</td><td>{{ item.quantity ?? '—' }}</td><td>{{ item.location }}</td>
              </tr>
            }
          </tbody></table>
        </section>
      </div>
      @if (selectedStock() && selected() !== 'machines') {
        <div class="adjust"><h3>Adjust selected stock · {{ selectedStock()?.code }}</h3>
          <label>Delta <input type="number" [(ngModel)]="delta" step="1"></label>
          <label>Reason <input type="text" [(ngModel)]="reason" maxlength="500"></label>
          <button type="button" (click)="adjust()">Save adjustment</button>
        </div>
      }
    </section>
    <section class="panel"><h2>Tool ⇄ Holder compatibility</h2>
      <p>Only approved, evidenced reviews yield COMPATIBLE / INCOMPATIBLE. Otherwise UNKNOWN.</p>
      <label>Tool master ID <input type="text" [(ngModel)]="toolId" placeholder="UUID"></label>
      <label>Holder master ID <input type="text" [(ngModel)]="holderId" placeholder="UUID"></label>
      <button type="button" (click)="check()">Check</button>
      @if (compatibility(); as result) {
        <div class="result"><strong>{{ result.status }}</strong>
          <p>{{ result.evidence }}</p><p>{{ result.reference }}</p></div>
      }
      <details><summary>Record engineering review (requires permission)</summary>
        <label>Verdict <select [(ngModel)]="verdict"><option value="COMPATIBLE">COMPATIBLE</option>
          <option value="INCOMPATIBLE">INCOMPATIBLE</option></select></label>
        <label>Evidence <textarea [(ngModel)]="evidence" rows="3" minlength="12"></textarea></label>
        <label>Reference <input [(ngModel)]="reference" placeholder="Drawing / manufacturer catalog"></label>
        <button type="button" (click)="review()">Record review</button>
      </details>
    </section>
  </main>`,
  styles: [`
    :host {display:block;min-height:100vh;background:#111a26;color:#e1e9f3;font-family:system-ui, sans-serif}
    .cortis {max-width:1260px;margin:auto;padding:2rem}
    header {display:flex;align-items:baseline;gap:1.5rem;flex-wrap:wrap}
    h1 {letter-spacing:.15em} h2{font-size:1.15rem}h3{font-size:1rem}
    .panel{background:#1c2939;border:1px solid #33475c;border-radius:12px;padding:1.25rem;margin:1.2rem 0}
    .auth,.adjust,nav{display:flex;gap:.75rem;align-items:end;flex-wrap:wrap}
    label{display:inline-flex;flex-direction:column;gap:.4rem;margin:.4rem .6rem .4rem 0}
    input,select,textarea{background:#101b29;color:#e1e9f3;padding:.55rem;border:1px solid #5a7186;border-radius:5px;max-width:22rem}
    button{border:1px solid #66809c;border-radius:6px;padding:.6rem .9rem;background:#263c53;color:#fff;cursor:pointer}
    button:hover,.selected{background:#315e85!important} .columns{display:grid;grid-template-columns:1fr 1fr;gap:1.4rem}
    table{border-collapse:collapse;width:100%}th,td{text-align:left;padding:.7rem;border-bottom:1px solid #3c5065}
    tbody tr{cursor:pointer} .error{background:#532b31;padding:1rem;border-radius:5px}.result{padding:1rem;border-left:4px solid #6997c1}
    details{margin-top:1.5rem} details label{display:flex;max-width:28rem}
    @media(max-width:800px){.columns{grid-template-columns:1fr}.cortis{padding:1rem}}
  `]
})
export class BusinessPage {
  readonly api = inject(BusinessApiService);
  readonly selected = signal<Kind>('tools');
  readonly masters = signal<Master[]>([]);
  readonly stock = signal<InventoryItem[]>([]);
  readonly selectedStock = signal<InventoryItem | null>(null);
  readonly compatibility = signal<CompatibilityResult | null>(null);
  readonly error = signal('');
  username = '';
  password = '';
  delta = 1;
  reason = '';
  toolId = '';
  holderId = '';
  verdict: 'COMPATIBLE' | 'INCOMPATIBLE' = 'COMPATIBLE';
  evidence = '';
  reference = '';

  private fail(error: unknown) {
    const status = error instanceof HttpErrorResponse ? error.status : 0;
    this.error.set(status === 401 ? 'Not signed in' : status === 403 ? 'Insufficient permissions / CSRF token' :
      `Request failed (${status || 'network'}). Check backend and dev proxy.`);
  }
  login() {
    this.error.set('');
    this.api.login(this.username, this.password).subscribe({
      next: () => { this.password = ''; this.refresh(); },
      error: e => { this.password = ''; this.fail(e); }
    });
  }
  logout() {
    this.api.logout().subscribe({
      next: () => { this.masters.set([]); this.stock.set([]); this.compatibility.set(null); },
      error: e => this.fail(e)
    });
  }
  setKind(kind: Kind) { this.selected.set(kind); this.selectedStock.set(null); this.refresh(); }
  refresh() {
    this.error.set('');
    this.api.catalog(this.selected()).subscribe({next: value => this.masters.set(value), error: e => this.fail(e)});
    this.api.inventory(this.selected()).subscribe({next: value => this.stock.set(value), error: e => this.fail(e)});
  }
  adjust() {
    const item = this.selectedStock();
    if (!item || !Number.isInteger(this.delta) || !this.reason.trim()) return;
    this.api.adjust(this.selected(), item.id, this.delta, this.reason).subscribe({
      next: () => { this.selectedStock.set(null); this.refresh(); }, error: e => this.fail(e)
    });
  }
  check() {
    this.error.set(''); this.compatibility.set(null);
    this.api.check(this.toolId, this.holderId).subscribe({next: v => this.compatibility.set(v), error: e => this.fail(e)});
  }
  review() {
    this.api.review(this.toolId, this.holderId, this.verdict, this.evidence, this.reference).subscribe({
      next: v => this.compatibility.set(v), error: e => this.fail(e)
    });
  }
}
