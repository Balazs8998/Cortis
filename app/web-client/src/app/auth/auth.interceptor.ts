import { inject } from '@angular/core';
import { HttpErrorResponse, HttpInterceptorFn } from '@angular/common/http';
import { Router } from '@angular/router';
import { catchError, throwError } from 'rxjs';
import { AuthSessionService } from './auth-session.service';

/** Only API calls outside the auth handshake trigger a redirect on expired sessions. */
export const authInterceptor: HttpInterceptorFn = (request, next) => {
  const auth = inject(AuthSessionService);
  const router = inject(Router);
  return next(request).pipe(
    catchError(error => {
      if (error instanceof HttpErrorResponse && error.status === 401 &&
          request.url.startsWith('/api/') && !request.url.startsWith('/api/auth/')) {
        auth.forgetLocalSession();
        void router.navigateByUrl('/');
      }
      return throwError(() => error);
    })
  );
};
