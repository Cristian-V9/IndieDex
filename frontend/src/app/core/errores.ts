import { HttpErrorResponse } from '@angular/common/http';
import { ErrorApi } from './modelos';

/** Mensaje legible para mostrar al usuario a partir de un error de la API. */
export function mensajeError(error: unknown): string {
  if (error instanceof HttpErrorResponse) {
    if (error.status === 0) {
      return 'No se pudo conectar con el servidor. ¿Está encendido el backend en el puerto 8080?';
    }
    const cuerpo = error.error as Partial<ErrorApi> | null;
    if (cuerpo?.campos) {
      return Object.values(cuerpo.campos)[0] ?? cuerpo.mensaje ?? 'Revisa los datos';
    }
    if (cuerpo?.mensaje) {
      return cuerpo.mensaje;
    }
  }
  return 'Ocurrió un error inesperado. Inténtalo de nuevo.';
}

/** Errores por campo (validaciones @Valid del backend). */
export function erroresDeCampos(error: unknown): Record<string, string> {
  if (error instanceof HttpErrorResponse) {
    return (error.error as Partial<ErrorApi> | null)?.campos ?? {};
  }
  return {};
}
