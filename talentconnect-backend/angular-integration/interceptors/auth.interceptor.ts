// ============================================================
// interceptors/auth.interceptor.ts
// Injecte automatiquement le token JWT dans chaque requête HTTP
// À copier dans src/app/core/interceptors/
//
// Enregistrement dans app.config.ts (Angular 17+) :
//   provideHttpClient(withInterceptors([authInterceptor]))
// ============================================================

import { HttpInterceptorFn, HttpErrorResponse } from '@angular/common/http';
import { inject } from '@angular/core';
import { Router } from '@angular/router';
import { catchError, throwError } from 'rxjs';

export const authInterceptor: HttpInterceptorFn = (req, next) => {
  const router = inject(Router);
  const token  = localStorage.getItem('tc_token');

  // Cloner la requête et ajouter l'en-tête Authorization si token présent
  const authReq = token
    ? req.clone({ setHeaders: { Authorization: `Bearer ${token}` } })
    : req;

  return next(authReq).pipe(
    catchError((error: HttpErrorResponse) => {
      if (error.status === 401) {
        // Token expiré ou invalide → déconnexion
        localStorage.removeItem('tc_token');
        localStorage.removeItem('tc_user');
        router.navigate(['/login']);
      }
      return throwError(() => error);
    })
  );
};

