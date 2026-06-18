// ============================================================
// app.config.ts  (Angular 17+ standalone)
// Configuration globale de l'application Angular
// À copier/fusionner avec src/app/app.config.ts
// ============================================================

import { ApplicationConfig, provideZoneChangeDetection } from '@angular/core';
import { provideRouter }                                  from '@angular/router';
import { provideHttpClient, withInterceptors }            from '@angular/common/http';
import { authInterceptor }                                from './interceptors/auth.interceptor';
import { routes }                                         from './app.routes';   // vos routes

export const appConfig: ApplicationConfig = {
  providers: [
    provideZoneChangeDetection({ eventCoalescing: true }),
    provideRouter(routes),
    // Active l'interceptor JWT automatiquement sur toutes les requêtes HTTP
    provideHttpClient(withInterceptors([authInterceptor])),
  ]
};

// ============================================================
// app.routes.ts  – exemple de structure de routes
// ============================================================

// import { Routes } from '@angular/router';
// import { authGuard, roleGuard } from './core/guards/auth.guard';
//
// export const routes: Routes = [
//   { path: 'login',     loadComponent: () => import('./pages/login/login.component') },
//   { path: 'forbidden', loadComponent: () => import('./pages/forbidden/forbidden.component') },
//   {
//     path: '',
//     canActivate: [authGuard],
//     children: [
//       { path: 'dashboard',     loadComponent: () => import('./pages/dashboard/dashboard.component') },
//       { path: 'jobs',          loadComponent: () => import('./pages/jobs/jobs.component') },
//       { path: 'jobs/:id',      loadComponent: () => import('./pages/jobs/job-detail.component') },
//       { path: 'applications',  loadComponent: () => import('./pages/applications/applications.component') },
//       { path: 'referrals',     loadComponent: () => import('./pages/referrals/referrals.component') },
//       { path: 'notifications', loadComponent: () => import('./pages/notifications/notifications.component') },
//       { path: 'profile',       loadComponent: () => import('./pages/profile/profile.component') },
//       // HR + ADMIN only
//       {
//         path: 'hr',
//         canActivate: [roleGuard('HR', 'ADMIN')],
//         children: [
//           { path: 'metrics', loadComponent: () => import('./pages/hr/metrics.component') },
//           { path: 'audit',   loadComponent: () => import('./pages/hr/audit.component') },
//         ]
//       },
//       // ADMIN only
//       {
//         path: 'admin',
//         canActivate: [roleGuard('ADMIN')],
//         children: [
//           { path: 'users', loadComponent: () => import('./pages/admin/users.component') },
//         ]
//       },
//       { path: '', redirectTo: 'dashboard', pathMatch: 'full' },
//     ]
//   },
//   { path: '**', redirectTo: 'login' }
// ];

