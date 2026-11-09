import { Component, computed, inject, signal } from '@angular/core';
import { Router, RouterLink, RouterLinkActive, RouterOutlet, NavigationEnd } from '@angular/router';
import { filter } from 'rxjs';
import { AuthSessionService } from '../auth/auth-session.service';
import { NAVIGATION, hasAuthority } from '../shared/access';

@Component({
  selector: 'app-cortis-shell',
  standalone: true,
  imports: [RouterOutlet, RouterLink, RouterLinkActive],
  templateUrl: './app-shell.html',
  styleUrl: './app-shell.css'
})
export class AppShell {
  readonly auth = inject(AuthSessionService);
  private readonly router = inject(Router);
  readonly navigation = NAVIGATION;
  readonly collapsed = signal(false);
  readonly mobileMenu = signal(false);
  readonly currentUrl = signal(this.router.url);
  readonly currentTitle = computed(() => NAVIGATION.find(entry => this.currentUrl().startsWith(entry.path))?.label ?? 'CORTIS');
  readonly isAdmin = computed(() => hasAuthority(this.auth.user(), 'CORTIS_ADMIN'));
  readonly logoutError = signal('');

  constructor() {
    this.router.events.pipe(filter((event): event is NavigationEnd => event instanceof NavigationEnd)).subscribe(event => {
      this.currentUrl.set(event.urlAfterRedirects);
      this.mobileMenu.set(false);
    });
  }

  signOut(): void {
    if (this.auth.loading()) return;
    this.logoutError.set('');
    this.auth.signOut().subscribe({
      next: () => void this.router.navigateByUrl('/'),
      error: () => this.logoutError.set(this.auth.error() || 'A kijelentkezés sikertelen.')
    });
  }
}
