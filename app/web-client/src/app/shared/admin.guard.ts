import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { AuthSessionService } from '../auth/auth-session.service';
import { hasAuthority } from './access';

export const adminGuard: CanActivateFn = () => {
  const auth = inject(AuthSessionService);
  const router = inject(Router);
  return hasAuthority(auth.user(), 'CORTIS_ADMIN') ? true : router.createUrlTree(['/dashboard']);
};
