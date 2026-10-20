import { HttpClient, HttpParams } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { API_URL } from '../api.config';
import { ArticuloResumen, EstadoWiki, JuegoDetalle, JuegoResumen, Logro, Objeto, Pagina, Personaje } from '../modelos';

export interface FiltrosCatalogo {
  buscar?: string;
  genero?: string;
  estado?: EstadoWiki | '';
  orden?: 'destacados' | 'articulos' | 'nombre';
}

@Injectable({ providedIn: 'root' })
export class JuegosService {
  private readonly http = inject(HttpClient);
  private readonly url = `${API_URL}/juegos`;

  listar(filtros: FiltrosCatalogo = {}): Observable<JuegoResumen[]> {
    let params = new HttpParams();
    if (filtros.buscar) params = params.set('buscar', filtros.buscar);
    if (filtros.genero) params = params.set('genero', filtros.genero);
    if (filtros.estado) params = params.set('estado', filtros.estado);
    if (filtros.orden) params = params.set('orden', filtros.orden);
    return this.http.get<JuegoResumen[]>(this.url, { params });
  }

  obtener(slug: string): Observable<JuegoDetalle> {
    return this.http.get<JuegoDetalle>(`${this.url}/${slug}`);
  }

  guias(slug: string, buscar: string, pagina: number, tamano = 6): Observable<Pagina<ArticuloResumen>> {
    let params = new HttpParams().set('pagina', pagina).set('tamano', tamano);
    if (buscar) params = params.set('buscar', buscar);
    return this.http.get<Pagina<ArticuloResumen>>(`${this.url}/${slug}/articulos`, { params });
  }

  logros(slug: string): Observable<Logro[]> {
    return this.http.get<Logro[]>(`${this.url}/${slug}/logros`);
  }

  objetos(slug: string): Observable<Objeto[]> {
    return this.http.get<Objeto[]>(`${this.url}/${slug}/objetos`);
  }

  personajes(slug: string): Observable<Personaje[]> {
    return this.http.get<Personaje[]>(`${this.url}/${slug}/personajes`);
  }
}
