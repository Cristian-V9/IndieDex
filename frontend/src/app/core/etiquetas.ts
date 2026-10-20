import { EstadoSugerencia, EstadoWiki, Rol, TipoSugerencia } from './modelos';

/** Textos y clases CSS para mostrar los valores de la API. */

const ESTADOS_SUGERENCIA: Record<EstadoSugerencia, string> = {
  SUGERENCIA: 'Sugerencia',
  VOTOS: 'Votos',
  REVISION: 'Revisión',
  PUBLICADO: 'Publicado',
  RECHAZADA: 'Rechazada',
};

const TIPOS_SUGERENCIA: Record<TipoSugerencia, string> = {
  NUEVA_WIKI: 'Nueva wiki',
  NUEVA_GUIA: 'Nueva guía',
  MEJORA: 'Mejora',
  CORRECCION: 'Corrección',
};

export const OPCIONES_TIPO: { valor: TipoSugerencia; texto: string }[] = [
  { valor: 'NUEVA_GUIA', texto: 'Nueva guía' },
  { valor: 'MEJORA', texto: 'Mejora de un artículo' },
  { valor: 'NUEVA_WIKI', texto: 'Nueva wiki' },
  { valor: 'CORRECCION', texto: 'Corrección' },
];

export function textoEstado(estado: EstadoSugerencia): string {
  return ESTADOS_SUGERENCIA[estado];
}

/** Clase de la etiqueta de color según el estado. */
export function claseEstado(estado: EstadoSugerencia): string {
  switch (estado) {
    case 'SUGERENCIA': return 'etiqueta--sugerencia';
    case 'VOTOS': return 'etiqueta--votos';
    case 'REVISION': return 'etiqueta--revision';
    case 'PUBLICADO': return 'etiqueta--publicado';
    case 'RECHAZADA': return 'etiqueta--rechazada';
  }
}

export function textoTipo(tipo: TipoSugerencia): string {
  return TIPOS_SUGERENCIA[tipo];
}

export function textoWiki(estado: EstadoWiki): string {
  return estado === 'ABIERTA' ? 'Wiki abierta' : 'Próximamente';
}

export function textoRol(rol: Rol): string {
  return rol === 'ADMIN' ? 'Administración' : rol === 'EDITOR' ? 'Equipo editor' : 'Colaborador';
}

/** "MarineroLunar" -> "ML", "admin" -> "AD" */
export function iniciales(nombre: string): string {
  const mayusculas = nombre.match(/[A-ZÁÉÍÓÚÑ]/g);
  if (mayusculas && mayusculas.length >= 2) {
    return (mayusculas[0] + mayusculas[1]).toUpperCase();
  }
  return nombre.slice(0, 2).toUpperCase();
}
