import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { AuthService } from './servicios/auth.service';

/** Solo con sesión iniciada; si no, va al login y luego vuelve. */
export const sesionGuard: CanActivateFn = (_ruta, estado) => {
  const auth = inject(AuthService);
  return auth.conectado()
    ? true
    : inject(Router).createUrlTree(['/login'], { queryParams: { volver: estado.url } });
};

/** Solo EDITOR o ADMIN (panel de revisión). */
export const equipoGuard: CanActivateFn = (_ruta, estado) => {
  const auth = inject(AuthService);
  const router = inject(Router);
  if (!auth.conectado()) {
    return router.createUrlTree(['/login'], { queryParams: { volver: estado.url } });
  }
  return auth.esEquipo() ? true : router.createUrlTree(['/']);
};
