/**
 * Tipos de datos que devuelve la API de IndieDex.
 * Reflejan los DTOs del backend (paquete com.indiedex.dto).
 */

export type Rol = 'USER' | 'EDITOR' | 'ADMIN';
export type EstadoWiki = 'ABIERTA' | 'EN_PREPARACION';
export type EstadoArticulo = 'BORRADOR' | 'EN_REVISION' | 'PUBLICADO';
export type TipoSugerencia = 'NUEVA_WIKI' | 'NUEVA_GUIA' | 'MEJORA' | 'CORRECCION';
export type EstadoSugerencia = 'SUGERENCIA' | 'VOTOS' | 'REVISION' | 'PUBLICADO' | 'RECHAZADA';

export interface Pagina<T> {
  contenido: T[];
  pagina: number;
  tamano: number;
  totalElementos: number;
  totalPaginas: number;
  ultima: boolean;
}

export interface ErrorApi {
  estado: number;
  error: string;
  mensaje: string;
  ruta: string;
  fecha: string;
  campos?: Record<string, string>;
}

// ---------- Usuarios ----------
export interface UsuarioResumen {
  id: number;
  nombreUsuario: string;
  rol: Rol;
}

export interface Usuario {
  id: number;
  nombreUsuario: string;
  email: string;
  rol: Rol;
  biografia: string | null;
  fechaRegistro: string;
}

export interface Sesion {
  token: string;
  tipo: string;
  expiraEn: string;
  usuario: Usuario;
}

export interface Perfil {
  usuario: Usuario;
  totalSugerencias: number;
  sugerenciasPublicadas: number;
  totalVotos: number;
  totalComentarios: number;
}

// ---------- Juegos ----------
export interface JuegoResumen {
  id: number;
  titulo: string;
  slug: string;
  desarrollador: string | null;
  imagenPortada: string | null;
  generos: string | null;
  estadoWiki: EstadoWiki;
  totalArticulos: number;
}

export interface JuegoDetalle extends JuegoResumen {
  eslogan: string | null;
  descripcion: string | null;
  imagenBanner: string | null;
  editor: string | null;
  fechaLanzamiento: string | null;
  modoJuego: string | null;
  plataformas: string | null;
  totalColaboradores: number;
  totalLogros: number;
  totalObjetos: number;
  totalPersonajes: number;
}

// ---------- Artículos ----------
export interface ArticuloResumen {
  id: number;
  titulo: string;
  resumen: string | null;
  categoria: string | null;
  imagen: string | null;
  minutosLectura: number | null;
  estado: EstadoArticulo;
  juegoTitulo: string;
  juegoSlug: string;
  autor: UsuarioResumen;
  fechaActualizacion: string;
}

export interface ArticuloDetalle extends ArticuloResumen {
  contenido: string;
  imagenDescripcion: string | null;
  juegoId: number;
  fechaCreacion: string;
  totalComentarios: number;
}

// ---------- Logros, objetos, personajes ----------
export interface Logro {
  id: number;
  nombre: string;
  descripcion: string | null;
  comoConseguirlo: string | null;
  icono: string | null;
}

export interface Objeto {
  id: number;
  nombre: string;
  descripcion: string | null;
  tipo: string | null;
  rareza: string | null;
  ubicacion: string | null;
  imagen: string | null;
}

export interface Personaje {
  id: number;
  nombre: string;
  historia: string | null;
  rol: string | null;
  ubicacion: string | null;
  imagen: string | null;
}

// ---------- Comentarios ----------
export interface Comentario {
  id: number;
  contenido: string;
  autor: UsuarioResumen;
  fechaCreacion: string;
  padreId: number | null;
  respuestas: Comentario[];
}

export interface ComentarioPerfil {
  id: number;
  contenido: string;
  fechaCreacion: string;
  articuloId: number;
  articuloTitulo: string;
}

// ---------- Sugerencias ----------
export interface Sugerencia {
  id: number;
  titulo: string;
  descripcion: string;
  tipo: TipoSugerencia;
  estado: EstadoSugerencia;
  juegoId: number | null;
  juegoTitulo: string | null;
  juegoSlug: string | null;
  autor: UsuarioResumen;
  totalVotos: number;
  votadaPorMi: boolean;
  fechaCreacion: string;
  notaRevision: string | null;
  revisor: UsuarioResumen | null;
  fechaRevision: string | null;
}

export interface NuevaSugerencia {
  titulo: string;
  descripcion: string;
  tipo: TipoSugerencia;
  juegoId: number | null;
}

export interface ResultadoVoto {
  sugerenciaId: number;
  totalVotos: number;
  votadaPorMi: boolean;
  estado: EstadoSugerencia;
}

export interface ResumenRevision {
  pendientes: number;
  enRevision: number;
  publicadasEsteMes: number;
  rechazadas: number;
}

// ---------- General ----------
export interface Estadisticas {
  totalArticulos: number;
  totalColaboradores: number;
  wikisAbiertas: number;
  totalJuegos: number;
  sugerenciasAbiertas: number;
}

export interface ResultadoBusqueda {
  juegos: JuegoResumen[];
  articulos: ArticuloResumen[];
  objetos: (Objeto & { juegoSlug: string })[];
  personajes: (Personaje & { juegoSlug: string })[];
}
