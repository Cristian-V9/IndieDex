import { HttpErrorResponse, HttpInterceptorFn } from '@angular/common/http';
import { inject } from '@angular/core';
import { catchError, throwError } from 'rxjs';
import { API_URL } from './api.config';
import { AuthService } from './servicios/auth.service';

/**
 * Añade "Authorization: Bearer <token>" a cada petición al backend.
 * Si el backend responde 401 con un token puesto, la sesión caducó: se cierra.
 */
export const authInterceptor: HttpInterceptorFn = (req, next) => {
  const auth = inject(AuthService);
  const token = auth.token();
  const esApi = req.url.startsWith(API_URL);

  const peticion = token && esApi
    ? req.clone({ setHeaders: { Authorization: `Bearer ${token}` } })
    : req;

  return next(peticion).pipe(
    catchError((error: unknown) => {
      if (error instanceof HttpErrorResponse && error.status === 401 && token && esApi) {
        auth.cerrarSesion(false);
      }
      return throwError(() => error);
    }),
  );
};
