import { authGuard } from './auth/auth.guard';
import { Routes } from '@angular/router';

export const routes: Routes = [
  { path: '', pathMatch: 'full', loadComponent: () => import('./auth/login-page').then(m => m.LoginPage) },
  { path: 'business', loadComponent: () => import('./cortis/business-page').then(m => m.BusinessPage), canActivate: [authGuard] },];
