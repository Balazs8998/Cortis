import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';

export type EngineeringKind = 'tools' | 'holders' | 'interfaces' | 'machines';
export interface EngineeringAsset {
  kind: EngineeringKind; code: string; inventoryId: string; masterId: string;
  name: string; manufacturerCode: string; manufacturer: string;
  location: string; quantity: number | null;
}
export interface EngineeringFeature {
  entityFeatureId: string; name: string; role: string; unit: string;
  dataType: 'number' | 'integer' | 'string' | 'text';
  valueNumber: number | null; valueText: string | null;
}
export interface MachineStation {
  id: string; positionIndex: number; label: string; stationType: string;
  maxToolDiameter: number | null; maxToolLength: number | null;
  maxHolderLength: number | null; active: boolean;
}
export interface StationCreate {
  positionIndex: number; label: string; stationType: string;
  maxToolDiameter: number | null; maxToolLength: number | null;
  maxHolderLength: number | null;
}
export interface StationMount {
  id: string; stationId: string; holderCode: string | null; interfaceCode: string | null;
  evidence: string; reference: string; reviewedBy: string;
}
export interface StationOption {
  stationId: string; position: number; stationLabel: string; stationType: string;
  holderCode: string | null; interfaceCode: string | null;
  reviewStatus: 'REVIEWED_PAIR' | 'INCOMPATIBLE' | 'UNKNOWN'; explanation: string;
  stockAvailable: boolean; stockDetails: string; sourceReference: string | null;
}
export interface EngineeringPlan {
  tool: EngineeringAsset; machine: EngineeringAsset; options: StationOption[]; notice: string;
}

@Injectable({providedIn:'root'})
export class EngineeringApiService {
  private readonly http = inject(HttpClient);
  private readonly root = '/api/v1/engineering';
  asset(kind: EngineeringKind, code: string) {
    return this.http.get<EngineeringAsset>(`${this.root}/assets/${kind}/${encodeURIComponent(code)}`);
  }
  features(kind: EngineeringKind, code: string) {
    return this.http.get<EngineeringFeature[]>(`${this.root}/features/${kind}/${encodeURIComponent(code)}`);
  }
  saveFeature(kind: EngineeringKind, code: string, entityFeatureId: string, valueNumber: number | null, valueText: string | null) {
    return this.http.put<EngineeringFeature[]>(`${this.root}/features/${kind}/${encodeURIComponent(code)}`,
      {entityFeatureId, valueNumber, valueText});
  }
  stations(machineCode: string) {
    return this.http.get<MachineStation[]>(`${this.root}/machines/${encodeURIComponent(machineCode)}/stations`);
  }
  createStation(machineCode: string, data: StationCreate) {
    return this.http.post<MachineStation>(`${this.root}/machines/${encodeURIComponent(machineCode)}/stations`, data);
  }
  mounts(stationId: string) {
    return this.http.get<StationMount[]>(`${this.root}/stations/${stationId}/mounts`);
  }
  approveMount(stationId: string, data: {holderCode: string; interfaceCode: string | null; evidence: string; reference: string}) {
    return this.http.post<StationMount>(`${this.root}/stations/${stationId}/mounts`, data);
  }
  plan(toolCode: string, machineCode: string) {
    return this.http.get<EngineeringPlan>(`${this.root}/plan`, {params: {toolCode, machineCode}});
  }
}
