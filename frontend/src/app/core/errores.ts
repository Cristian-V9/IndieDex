import { HttpErrorResponse } from '@angular/common/http';
import { ErrorApi } from './modelos';

/** Mensaje legible para mostrar al usuario a partir de un error de la API. */
export function mensajeError(error: unknown): string {
  if (error instanceof HttpErrorResponse) {
    if (error.status === 0 || error.status >= 500) {
      // El detalle técnico va solo a la consola del navegador (para el equipo), nunca a la pantalla
      console.warn('[IndieDex] Error al llamar a la API:', error.status, error.url, error.message);
      return 'No pudimos cargar el contenido en este momento. Inténtalo de nuevo en unos minutos.';
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
