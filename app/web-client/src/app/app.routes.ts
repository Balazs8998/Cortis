import { Routes } from '@angular/router';

export const routes: Routes = [
  { path: 'business', loadComponent: () => import('./cortis/business-page').then(m => m.BusinessPage) },];
