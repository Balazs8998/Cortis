import { Component, computed, inject, signal } from '@angular/core';
import { ReactiveFormsModule, FormBuilder, Validators } from '@angular/forms';
import { HttpErrorResponse } from '@angular/common/http';
import { forkJoin } from 'rxjs';
import { BusinessApiService, CompatibilityResult } from '../../cortis/business-api.service';
import { EngineeringApiService, EngineeringAsset, EngineeringFeature, EngineeringKind,
  EngineeringPlan, MachineStation, StationMount, StationCreate } from '../../shared/engineering-api.service';
import { AuthSessionService } from '../../auth/auth-session.service';
import { hasAuthority } from '../../shared/access';

const W_CODE = /^W\d{6}$/;
const M_CODE = /^M[1-9]\d*$/;
const H_CODE = /^H\d{6}$/;
const POSITIVE = /^\d+(\.\d{1,3})?$/;
const STATION_TYPES = [
  {value:'turning_tool_station', label:'Fix esztergaszerszám-állomás'},
  {value:'cross_driven_tool_station', label:'Radiális hajtott állomás'},
  {value:'front_axial_tool_station', label:'Homlokoldali axiális állomás'},
  {value:'backside_axial_tool_station', label:'Hátoldali axiális állomás'}
] as const;

@Component({
  selector:'app-cortis-compatibility', standalone:true, imports:[ReactiveFormsModule],
  templateUrl:'./compatibility-page.html',
  styleUrls:['../../shared/workspace.css','./compatibility-page.css']
})
export class CompatibilityPage {
  private readonly api = inject(EngineeringApiService);
  private readonly legacy = inject(BusinessApiService);
  private readonly fb = inject(FormBuilder);
  readonly auth = inject(AuthSessionService);
  readonly canRead = computed(() => hasAuthority(this.auth.user(), 'CORTIS_ENGINEERING_READ') && hasAuthority(this.auth.user(), 'CORTIS_INVENTORY_READ'));
  readonly canWrite = computed(() => hasAuthority(this.auth.user(), 'CORTIS_ENGINEERING_WRITE'));
  readonly canMountReview = computed(() => hasAuthority(this.auth.user(), 'CORTIS_ENGINEERING_REVIEW'));
  readonly canPairReview = computed(() => hasAuthority(this.auth.user(), 'CORTIS_COMPATIBILITY_REVIEW'));
  readonly busy = signal(false);
  readonly error = signal('');
  readonly notice = signal('');
  readonly planResult = signal<EngineeringPlan | null>(null);
  readonly stations = signal<MachineStation[]>([]);
  readonly mounts = signal<StationMount[]>([]);
  readonly features = signal<EngineeringFeature[]>([]);
  readonly selectedFeature = signal<EngineeringFeature | null>(null);
  readonly featureAsset = signal<EngineeringAsset | null>(null);
  readonly pairResult = signal<CompatibilityResult | null>(null);
  readonly stationTypes = STATION_TYPES;

  readonly planForm = this.fb.nonNullable.group({
    toolCode: ['', [Validators.required, Validators.pattern(W_CODE)]],
    machineCode: ['', [Validators.required, Validators.pattern(M_CODE)]]
  });
  readonly featureForm = this.fb.nonNullable.group({
    kind: ['tools' as EngineeringKind, Validators.required],
    code: ['', Validators.required],
    entityFeatureId: ['', Validators.required],
    value: ['', Validators.required]
  });
  readonly stationForm = this.fb.nonNullable.group({
    positionIndex: [1,[Validators.required, Validators.min(1),Validators.max(999)]],
    label: ['', [Validators.required,Validators.maxLength(100)]],
    stationType: ['turning_tool_station', Validators.required],
    maxToolDiameter: [''], maxToolLength: [''], maxHolderLength: ['']
  });
  readonly mountForm = this.fb.nonNullable.group({
    stationId: ['',Validators.required], holderCode: ['', [Validators.required,Validators.pattern(H_CODE)]],
    interfaceCode: ['', Validators.pattern(/^$|^I\d{6}$/)],
    evidence: ['', [Validators.required,Validators.minLength(12),Validators.maxLength(2000)]],
    reference: ['', [Validators.required,Validators.minLength(3),Validators.maxLength(500)]]
  });
  readonly pairForm = this.fb.nonNullable.group({
    holderCode: ['', [Validators.required,Validators.pattern(H_CODE)]],
    verdict: ['COMPATIBLE' as 'COMPATIBLE'|'INCOMPATIBLE', Validators.required],
    evidence: ['', [Validators.required, Validators.minLength(12),Validators.maxLength(2000)]],
    reference: ['', [Validators.required,Validators.minLength(3),Validators.maxLength(500)]]
  });

  private fail(err:unknown): void {
    const status=err instanceof HttpErrorResponse ? err.status : 0;
    this.error.set(status === 401 ? 'Lejárt a munkamenet.' :
      status === 403 ? 'Nincs megfelelő jogosultságod, vagy a CSRF token hiányzik.' :
      status === 404 ? 'A megadott kódhoz nem található aktív tétel.' :
      status === 409 ? 'Az adat már létezik vagy egy adatbázis-szabályt sért.' :
      status === 400 ? 'Érvénytelen mező vagy üzleti kód.' :
      status === 0 ? 'Nem érhető el a backend. Indítsd az Angulart proxyval.' :
      `Kérés sikertelen (HTTP ${status}).`);
    this.busy.set(false);
  }
  private start(): boolean {
    if(this.busy()) return false;
    this.busy.set(true); this.error.set(''); this.notice.set('');
    return true;
  }
  checkPlan(): void {
    if (this.planForm.invalid || !this.canRead() || !this.start()) return;
    const {toolCode,machineCode}=this.planForm.getRawValue();
    forkJoin({plan:this.api.plan(toolCode,machineCode), stations:this.api.stations(machineCode)}).subscribe({
      next:r=>{this.planResult.set(r.plan);this.stations.set(r.stations);this.mounts.set([]);this.busy.set(false);},
      error:e=>this.fail(e)
    });
  }
  loadFeatures(): void {
    if(!this.canRead() || this.featureForm.controls.code.invalid || !this.start()) return;
    const {kind,code}=this.featureForm.getRawValue();
    this.featureAsset.set(null);this.features.set([]);this.selectedFeature.set(null);
    forkJoin({asset:this.api.asset(kind,code), features:this.api.features(kind,code)}).subscribe({
      next:r=>{
        this.featureAsset.set(r.asset);this.features.set(r.features);
        this.featureForm.controls.entityFeatureId.setValue('');this.featureForm.controls.value.setValue('');
        this.busy.set(false);
      },error:e=>this.fail(e)
    });
  }
  selectFeature(id:string):void {
    this.featureForm.controls.entityFeatureId.setValue(id);
    const f=this.features().find(feature=>feature.entityFeatureId===id)??null;
    this.selectedFeature.set(f);
    this.featureForm.controls.value.setValue(f ? String(f.valueNumber ?? f.valueText ?? '') : '');
  }
  saveFeature():void {
    if(!this.canWrite() || this.featureForm.invalid || !this.selectedFeature() || !this.start()) return;
    const f=this.selectedFeature()!;
    const {kind,code,value}=this.featureForm.getRawValue();
    const numeric=f.dataType==='number'||f.dataType==='integer';
    if(numeric && (!POSITIVE.test(value) || Number(value) <= 0 ||
      (f.dataType==='integer' && !Number.isInteger(Number(value))))) {
      this.error.set('A megadott értéknek pozitív számnak kell lennie (legfeljebb 3 tizedes).');this.busy.set(false);return;
    }
    this.api.saveFeature(kind,code,f.entityFeatureId,numeric?Number(value):null,numeric?null:value).subscribe({
      next:features=>{this.features.set(features);this.selectFeature(f.entityFeatureId);this.notice.set('Feature érték elmentve.');this.busy.set(false);},
      error:e=>this.fail(e)
    });
  }
  private optionalNumber(value:string):number|null {
    if(!value.trim()) return null;
    const n=Number(value);
    return Number.isFinite(n)&&n>0&&n<=1e9&&POSITIVE.test(value)?n:NaN;
  }
  createStation():void {
    if(!this.canWrite() || this.stationForm.invalid || this.planForm.controls.machineCode.invalid || !this.start()) return;
    const v=this.stationForm.getRawValue();
    const data:StationCreate={positionIndex:v.positionIndex,label:v.label,stationType:v.stationType,
      maxToolDiameter:this.optionalNumber(v.maxToolDiameter),
      maxToolLength:this.optionalNumber(v.maxToolLength),
      maxHolderLength:this.optionalNumber(v.maxHolderLength)};
    if([data.maxToolDiameter,data.maxToolLength,data.maxHolderLength].some(n=>Number.isNaN(n))){
      this.busy.set(false);this.error.set('A gépállomás-korlátok pozitív számok legyenek.');return;
    }
    this.api.createStation(this.planForm.getRawValue().machineCode,data).subscribe({
      next:()=>{this.busy.set(false);this.notice.set('Gépállomás felvéve. Futtasd újra az ellenőrzést.');this.checkPlan();},
      error:e=>this.fail(e)
    });
  }
  loadMounts(stationId:string):void {
    this.mountForm.controls.stationId.setValue(stationId);
    this.api.mounts(stationId).subscribe({next:r=>this.mounts.set(r),error:e=>this.fail(e)});
  }
  approveMount():void {
    if(!this.canMountReview() || this.mountForm.invalid || !this.start())return;
    const v=this.mountForm.getRawValue();
    this.api.approveMount(v.stationId,{holderCode:v.holderCode,interfaceCode:v.interfaceCode||null,
      evidence:v.evidence,reference:v.reference}).subscribe({
      next:()=>{this.busy.set(false);this.notice.set('Dokumentált gépállomás–tartó kapcsolat rögzítve.');
        this.loadMounts(v.stationId);this.checkPlan();},error:e=>this.fail(e)
    });
  }
  checkPair():void {
    if(this.planForm.controls.toolCode.invalid || this.pairForm.controls.holderCode.invalid || !this.start())return;
    forkJoin({tool:this.api.asset('tools',this.planForm.getRawValue().toolCode),
      holder:this.api.asset('holders',this.pairForm.getRawValue().holderCode)}).subscribe({
      next:assets=>this.legacy.check(assets.tool.masterId,assets.holder.masterId).subscribe({
        next:r=>{this.pairResult.set(r);this.busy.set(false);},error:e=>this.fail(e)}),error:e=>this.fail(e)
    });
  }
  reviewPair():void {
    if(!this.canPairReview() || this.pairForm.invalid || this.planForm.controls.toolCode.invalid || !this.start())return;
    const v=this.pairForm.getRawValue();
    forkJoin({tool:this.api.asset('tools',this.planForm.getRawValue().toolCode),
      holder:this.api.asset('holders',v.holderCode)}).subscribe({
      next:assets=>this.legacy.review(assets.tool.masterId,assets.holder.masterId,
        v.verdict,v.evidence,v.reference).subscribe({
        next:r=>{this.pairResult.set(r);this.notice.set('Dokumentált szerszám–tartó döntés elmentve.');this.busy.set(false);},
        error:e=>this.fail(e)}),error:e=>this.fail(e)
    });
  }
}
