import { adminGuard } from './shared/admin.guard';
import { authGuard } from './auth/auth.guard';
import { Routes } from '@angular/router';

export const routes: Routes = [
  { path: '', pathMatch: 'full', loadComponent: () => import('./auth/login-page').then(m => m.LoginPage) },
  { path: 'business', pathMatch: 'full', redirectTo: 'inventory' },
  {
    path: '', canActivate: [authGuard],
    loadComponent: () => import('./layout/app-shell').then(m => m.AppShell),
    children: [
      { path: 'dashboard', loadComponent: () => import('./features/dashboard/dashboard-page').then(m => m.DashboardPage) },
      { path: 'inventory', loadComponent: () => import('./features/inventory/inventory-page').then(m => m.InventoryPage) },
      { path: 'orders', loadComponent: () => import('./features/orders/orders-page').then(m => m.OrdersPage) },
      { path: 'statistics', loadComponent: () => import('./features/statistics/statistics-page').then(m => m.StatisticsPage) },
      { path: 'compatibility', loadComponent: () => import('./features/compatibility/compatibility-page').then(m => m.CompatibilityPage) },
      { path: 'settings', loadComponent: () => import('./features/settings/settings-page').then(m => m.SettingsPage) },
      { path: 'admin', canActivate: [adminGuard], loadComponent: () => import('./features/settings/admin-page').then(m => m.AdminPage) },
      { path: '**', redirectTo: 'dashboard' }
    ]
  },
];
