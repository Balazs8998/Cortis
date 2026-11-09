import { Component, computed, inject, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { HttpErrorResponse } from '@angular/common/http';
import { BusinessApiService, CompatibilityResult } from '../../cortis/business-api.service';
import { AuthSessionService } from '../../auth/auth-session.service';
import { hasAuthority } from '../../shared/access';

const UUID_REGEX = /^[0-9a-f]{8}-[0-9a-f]{4}-[1-8][0-9a-f]{3}-[89ab][0-9a-f]{3}-[0-9a-f]{12}$/i;

@Component({
  selector: 'app-cortis-compatibility',
  standalone: true,
  imports: [ReactiveFormsModule],
  templateUrl: './compatibility-page.html',
  styleUrls: ['../../shared/workspace.css', './compatibility-page.css']
})
export class CompatibilityPage {
  private readonly api = inject(BusinessApiService);
  private readonly auth = inject(AuthSessionService);
  private readonly fb = inject(FormBuilder);
  readonly canReview = computed(() => hasAuthority(this.auth.user(), 'CORTIS_COMPATIBILITY_REVIEW'));
  readonly working = signal(false);
  readonly error = signal('');
  readonly result = signal<CompatibilityResult | null>(null);
  readonly pair = this.fb.nonNullable.group({
    toolId: ['', [Validators.required, Validators.pattern(UUID_REGEX)]],
    holderId: ['', [Validators.required, Validators.pattern(UUID_REGEX)]]
  });
  readonly reviewForm = this.fb.nonNullable.group({
    verdict: ['COMPATIBLE' as 'COMPATIBLE' | 'INCOMPATIBLE', [Validators.required]],
    evidence: ['', [Validators.required, Validators.minLength(12), Validators.maxLength(2000)]],
    reference: ['', [Validators.required, Validators.minLength(3), Validators.maxLength(500)]]
  });

  check(): void {
    if (this.pair.invalid || this.working()) return;
    const {toolId, holderId} = this.pair.getRawValue();
    this.working.set(true); this.error.set(''); this.result.set(null);
    this.api.check(toolId, holderId).subscribe({
      next: value => { this.result.set(value); this.working.set(false); },
      error: error => { this.fail(error); this.working.set(false); }
    });
  }
  review(): void {
    if (!this.canReview() || this.pair.invalid || this.reviewForm.invalid || this.working()) return;
    const pair = this.pair.getRawValue(); const review = this.reviewForm.getRawValue();
    this.working.set(true); this.error.set('');
    this.api.review(pair.toolId, pair.holderId, review.verdict, review.evidence, review.reference).subscribe({
      next: value => { this.result.set(value); this.working.set(false); },
      error: error => { this.fail(error); this.working.set(false); }
    });
  }
  private fail(error: unknown): void {
    const status = error instanceof HttpErrorResponse ? error.status : 0;
    this.error.set(status === 403 ? 'Nincs jogosultságod ehhez a kompatibilitási művelethez.' :
      status === 404 ? 'Nem található a megadott szerszám vagy befogó.' :
      `Kompatibilitás lekérdezése sikertelen (HTTP ${status}).`);
  }
}
