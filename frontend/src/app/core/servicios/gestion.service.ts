import { HttpClient } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { API_URL } from '../api.config';
import {
  ArticuloDatos, ArticuloDetalle, EstadoArticulo, JuegoDatos, JuegoDetalle, Logro, LogroDatos, Objeto,
  ObjetoDatos, Personaje, PersonajeDatos, TipoContenido,
} from '../modelos';

/**
 * Operaciones de escritura del equipo (EDITOR / ADMIN).
 * El backend vuelve a comprobar el rol en cada petición con @PreAuthorize.
 */
@Injectable({ providedIn: 'root' })
export class GestionService {
  private readonly http = inject(HttpClient);

  // ---------- Juegos ----------
  crearJuego(datos: JuegoDatos): Observable<JuegoDetalle> {
    return this.http.post<JuegoDetalle>(`${API_URL}/juegos`, datos);
  }

  actualizarJuego(id: number, datos: JuegoDatos): Observable<JuegoDetalle> {
    return this.http.put<JuegoDetalle>(`${API_URL}/juegos/${id}`, datos);
  }

  /** Solo ADMIN. Borra también toda la wiki del juego. */
  borrarJuego(id: number): Observable<void> {
    return this.http.delete<void>(`${API_URL}/juegos/${id}`);
  }

  // ---------- Artículos ----------
  crearArticulo(datos: ArticuloDatos): Observable<ArticuloDetalle> {
    return this.http.post<ArticuloDetalle>(`${API_URL}/articulos`, datos);
  }

  actualizarArticulo(id: number, datos: ArticuloDatos): Observable<ArticuloDetalle> {
    return this.http.put<ArticuloDetalle>(`${API_URL}/articulos/${id}`, datos);
  }

  cambiarEstadoArticulo(id: number, estado: EstadoArticulo): Observable<ArticuloDetalle> {
    return this.http.patch<ArticuloDetalle>(`${API_URL}/articulos/${id}/estado`, { estado });
  }

  borrarArticulo(id: number): Observable<void> {
    return this.http.delete<void>(`${API_URL}/articulos/${id}`);
  }

  // ---------- Logros, objetos y personajes (misma forma de ruta) ----------
  obtenerContenido(tipo: TipoContenido, id: number): Observable<Logro | Objeto | Personaje> {
    return this.http.get<Logro | Objeto | Personaje>(`${API_URL}/${tipo}/${id}`);
  }

  crearContenido(tipo: TipoContenido, datos: LogroDatos | ObjetoDatos | PersonajeDatos): Observable<unknown> {
    return this.http.post(`${API_URL}/${tipo}`, datos);
  }

  actualizarContenido(tipo: TipoContenido, id: number, datos: LogroDatos | ObjetoDatos | PersonajeDatos): Observable<unknown> {
    return this.http.put(`${API_URL}/${tipo}/${id}`, datos);
  }

  borrarContenido(tipo: TipoContenido, id: number): Observable<void> {
    return this.http.delete<void>(`${API_URL}/${tipo}/${id}`);
  }
}
