import { NAVIGATION, hasAuthority } from './access';
import { STORAGE_CATEGORIES } from './workspace-data.service';

describe('CORTIS Phase 4A navigation and permissions', () => {
  it('provides the requested dashboard, orders, inventory and statistics routes', () => {
    const urls = NAVIGATION.map(entry => entry.path);
    expect(urls).toContain('/dashboard');
    expect(urls).toContain('/orders');
    expect(urls).toContain('/inventory');
    expect(urls).toContain('/statistics');
  });
  it('does not grant access to an anonymous account', () => {
    expect(hasAuthority(null, 'CORTIS_INVENTORY_WRITE')).toBe(false);
  });
  it('compares permissions exactly, not by a broad READ role', () => {
    expect(hasAuthority({userId: '1', userName: 'Béla', authenticated: true, authorities: ['READ']}, 'CORTIS_INVENTORY_READ')).toBe(false);
  });
  it('uses the fixed German business code prefixes', () => {
    expect(STORAGE_CATEGORIES.map(category => category.prefix)).toEqual([
      'W000001', 'H000001', 'I000001', 'M1', 'R000001', 'Z000001', 'E000001'
    ]);
  });
  it('does not pretend unsupported categories have a backend API', () => {
    expect(STORAGE_CATEGORIES.filter(category => !category.kind).map(category => category.key))
      .toEqual(['materials', 'accessories', 'parts']);
  });
});
