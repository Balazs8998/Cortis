import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { switchMap } from 'rxjs';

export type Kind = 'tools' | 'holders' | 'interfaces' | 'machines';
export interface Master {
  id: string;
  typeId: string;
  typeName: string;
  manufacturerCode: string;
  manufacturer: string;
  description: string | null;
}
export interface InventoryItem {
  id: string;
  masterId: string;
  masterCode: string;
  code: string;
  name: string;
  location: string;
  quantity: number | null;
}
export interface CompatibilityResult {
  toolId: string;
  holderId: string;
  status: 'COMPATIBLE' | 'INCOMPATIBLE' | 'UNKNOWN';
  decisionId: string | null;
  evidence: string | null;
  reference: string | null;
  reviewedAt: string | null;
}
@Injectable({ providedIn: 'root' })
export class BusinessApiService {
  private readonly http = inject(HttpClient);
  readonly kinds: Kind[] = ['tools', 'holders', 'interfaces', 'machines'];
  csrf() { return this.http.get<{ token: string }>('/api/auth/csrf'); }
  login(userName: string, password: string) {
    return this.csrf().pipe(
      switchMap(() => this.http.post('/api/auth/login', { userName, password })),
      switchMap(() => this.csrf()) // login rotates CSRF; obtain a fresh token for later POST requests
    );
  }
  logout() { return this.csrf().pipe(switchMap(() => this.http.post('/api/auth/logout', {}))); }
  catalog(kind: Kind) { return this.http.get<Master[]>(`/api/v1/catalog/${kind}`); }
  inventory(kind: Kind) { return this.http.get<InventoryItem[]>(`/api/v1/inventory/${kind}`); }
  check(toolId: string, holderId: string) {
    return this.http.get<CompatibilityResult>('/api/v1/compatibility/tool-holder', {
      params: { toolId, holderId }
    });
  }
  adjust(kind: Kind, id: string, delta: number, reason: string) {
    return this.http.post<InventoryItem>(`/api/v1/inventory/${kind}/${id}/adjust`, { delta, reason });
  }
  review(toolId: string, holderId: string, verdict: 'COMPATIBLE' | 'INCOMPATIBLE', evidence: string, reference: string) {
    return this.http.post<CompatibilityResult>('/api/v1/compatibility/tool-holder/reviews', {
      toolId, holderId, verdict, evidence, reference
    });
  }
}
