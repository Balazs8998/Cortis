import type { CortisSessionUser } from '../auth/auth-session.service';

export function hasAuthority(user: CortisSessionUser | null, permission: string): boolean {
  return !!user?.authenticated && user.authorities.includes(permission);
}

export const NAVIGATION = [
  { path: '/dashboard', label: 'Főoldal', icon: '▦' },
  { path: '/orders', label: 'Megrendelések', icon: '≡' },
  { path: '/inventory', label: 'Raktár', icon: '▤' },
  { path: '/statistics', label: 'Statisztikák', icon: '▥' },
  { path: '/compatibility', label: 'Kompatibilitás', icon: '⇄' },
  { path: '/settings', label: 'Beállítások', icon: '⚙' }
] as const;
