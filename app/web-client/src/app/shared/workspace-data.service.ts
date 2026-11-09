import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import type { InventoryItem, Kind, Master } from '../cortis/business-api.service';

export interface InventoryMovement {
  id: string;
  previousQuantity: number;
  quantityDelta: number;
  resultingQuantity: number;
  reason: string;
  performedBy: string;
  performedAt: string;
}

export interface StorageCategory {
  key: string;
  label: string;
  german: string;
  prefix: string;
  kind: Kind;
  stocked: boolean;
}

export const STORAGE_CATEGORIES: readonly StorageCategory[] = [
  { key: 'tools', label: 'Szerszámok', german: 'Werkzeuge', prefix: 'W000001', kind: 'tools', stocked: true },
  { key: 'holders', label: 'Szerszámtartók', german: 'Werkzeughalter', prefix: 'H000001', kind: 'holders', stocked: true },
  { key: 'interfaces', label: 'Interfészek', german: 'Schnittstellen', prefix: 'I000001', kind: 'interfaces', stocked: true },
  { key: 'machines', label: 'Gépek', german: 'Maschinen', prefix: 'M1', kind: 'machines', stocked: false },
  { key: 'materials', label: 'Alapanyagok', german: 'Rohmaterial', prefix: 'R000001', kind: 'materials', stocked: true },
  { key: 'accessories', label: 'Kiegészítők', german: 'Zubehör', prefix: 'Z000001', kind: 'accessories', stocked: true },
  { key: 'parts', label: 'Gépalkatrészek', german: 'Ersatzteile', prefix: 'E000001', kind: 'parts', stocked: true }
];

export interface CatalogType { id: string; name: string; description: string | null; isInterface: boolean | null; }
export interface InventorySummary { kind: Kind; rows: number; quantity: number | null; }

@Injectable({ providedIn: 'root' })
export class WorkspaceDataService {
  private readonly http = inject(HttpClient);
  types(kind: Kind) {
    return this.http.get<CatalogType[]>(`/api/v1/catalog/${kind}/types`);
  }
  createType(kind: Kind, name: string) {
    return this.http.post<CatalogType>(`/api/v1/catalog/${kind}/types`, { name });
  }
  createMaster(kind: Kind, data: { typeId: string; manufacturerCode: string; manufacturer: string; description?: string }) {
    return this.http.post<Master>(`/api/v1/catalog/${kind}`, data);
  }
  createInventory(kind: Kind, data: { masterId: string; name: string; location: string; quantity?: number }) {
    return this.http.post<InventoryItem>(`/api/v1/inventory/${kind}`, data);
  }
  archive(kind: Kind, id: string) {
    return this.http.delete<void>(`/api/v1/inventory/${kind}/${id}`);
  }
  inventorySummary() {
    return this.http.get<InventorySummary[]>('/api/v1/inventory/summary');
  }
  catalog(kind: Kind, q = '', limit = 100, offset = 0) {
    return this.http.get<Master[]>(`/api/v1/catalog/${kind}`, { params: { q, limit, offset } });
  }
  inventory(kind: Kind, limit = 100, offset = 0) {
    return this.http.get<InventoryItem[]>(`/api/v1/inventory/${kind}`, { params: { limit, offset } });
  }
  movements(kind: Kind, id: string) {
    return this.http.get<InventoryMovement[]>(`/api/v1/inventory/${kind}/${id}/movements`);
  }
  adjust(kind: Kind, id: string, delta: number, reason: string) {
    return this.http.post<InventoryItem>(`/api/v1/inventory/${kind}/${id}/adjust`, { delta, reason });
  }
}
