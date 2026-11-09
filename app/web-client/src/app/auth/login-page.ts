import { Component, inject, signal } from '@angular/core';
import { Router } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { AuthSessionService } from './auth-session.service';

/** Template and stylesheet are copied from the actual local CORTIS login during installation. */
@Component({
  selector: 'app-cortis-login',
  standalone: true,
  imports: [FormsModule],
  templateUrl: './login-page.html',
  styleUrl: './login-page.css'
})
export class LoginPage {
  readonly auth = inject(AuthSessionService);
  private readonly router = inject(Router);
  readonly userName = signal('');
  readonly password = signal('');
  readonly passwordVisible = signal(false);
  readonly showPassword = this.passwordVisible;
  rememberMe = false; // V1 feature is intentionally disabled in HTML.

  togglePassword(): void { this.passwordVisible.update(v => !v); }
  togglePasswordVisibility(): void { this.togglePassword(); }

  // A valid browser session should lead directly to the protected area.
  ngOnInit(): void {
    this.auth.loadMe().subscribe({
      next: () => void this.router.navigateByUrl('/business'),
      error: () => { /* Anonymous visitor: show the login form. */ }
    });
  }

  login(event?: Event): void {
    event?.preventDefault();
    if (this.auth.loading()) return;
    this.auth.signIn(this.userName(), this.password()).subscribe({
      next: () => {
        this.password.set('');
        void this.router.navigateByUrl('/business');
      },
      error: () => this.password.set('')
    });
  }
}
