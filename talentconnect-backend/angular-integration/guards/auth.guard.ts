// ============================================================
// guards/auth.guard.ts
// Protège les routes qui nécessitent une authentification
// À copier dans src/app/core/guards/
//
// Usage dans les routes :
//   { path: 'dashboard', canActivate: [authGuard], component: DashboardComponent }
//   { path: 'admin',     canActivate: [authGuard, roleGuard('ADMIN')], ... }
// ============================================================

import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { AuthService } from '../services/auth.service';
import { UserRole } from '../models/talentconnect.models';

/** Guard de base : vérifie que l'utilisateur est connecté */
export const authGuard: CanActivateFn = () => {
  const auth   = inject(AuthService);
  const router = inject(Router);

  if (auth.isLoggedIn()) return true;

  router.navigate(['/login']);
  return false;
};

/** Factory guard : vérifie le rôle */
export const roleGuard = (...roles: UserRole[]): CanActivateFn => () => {
  const auth   = inject(AuthService);
  const router = inject(Router);

  if (!auth.isLoggedIn()) {
    router.navigate(['/login']);
    return false;
  }

  const role = auth.currentRole();
  if (role && roles.includes(role)) return true;

  router.navigate(['/forbidden']);
  return false;
};

