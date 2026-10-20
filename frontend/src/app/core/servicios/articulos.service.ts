import { HttpClient } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { API_URL } from '../api.config';
import { ArticuloDetalle, ArticuloResumen, Comentario } from '../modelos';

@Injectable({ providedIn: 'root' })
export class ArticulosService {
  private readonly http = inject(HttpClient);

  recientes(limite = 3): Observable<ArticuloResumen[]> {
    return this.http.get<ArticuloResumen[]>(`${API_URL}/articulos/recientes`, { params: { limite } });
  }

  obtener(id: number): Observable<ArticuloDetalle> {
    return this.http.get<ArticuloDetalle>(`${API_URL}/articulos/${id}`);
  }

  // ---------- Comentarios ----------

  comentarios(articuloId: number): Observable<Comentario[]> {
    return this.http.get<Comentario[]>(`${API_URL}/articulos/${articuloId}/comentarios`);
  }

  comentar(articuloId: number, contenido: string, padreId: number | null = null): Observable<Comentario> {
    return this.http.post<Comentario>(`${API_URL}/articulos/${articuloId}/comentarios`, { contenido, padreId });
  }

  borrarComentario(id: number): Observable<void> {
    return this.http.delete<void>(`${API_URL}/comentarios/${id}`);
  }
}
