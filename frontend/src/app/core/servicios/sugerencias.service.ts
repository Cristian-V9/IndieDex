import { HttpClient, HttpParams } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { API_URL } from '../api.config';
import {
  EstadoSugerencia, NuevaSugerencia, Pagina, ResultadoVoto, ResumenRevision, Sugerencia, TipoSugerencia,
} from '../modelos';

export interface FiltrosSugerencias {
  estados?: EstadoSugerencia[];
  tipo?: TipoSugerencia | '';
  buscar?: string;
  orden?: 'votos' | 'recientes';
  pagina?: number;
  tamano?: number;
}

@Injectable({ providedIn: 'root' })
export class SugerenciasService {
  private readonly http = inject(HttpClient);
  private readonly url = `${API_URL}/sugerencias`;

  listar(f: FiltrosSugerencias = {}): Observable<Pagina<Sugerencia>> {
    let params = new HttpParams()
      .set('orden', f.orden ?? 'votos')
      .set('pagina', f.pagina ?? 0)
      .set('tamano', f.tamano ?? 5);
    for (const estado of f.estados ?? []) params = params.append('estado', estado);
    if (f.tipo) params = params.set('tipo', f.tipo);
    if (f.buscar) params = params.set('buscar', f.buscar);
    return this.http.get<Pagina<Sugerencia>>(this.url, { params });
  }

  crear(datos: NuevaSugerencia): Observable<Sugerencia> {
    return this.http.post<Sugerencia>(this.url, datos);
  }

  votar(id: number): Observable<ResultadoVoto> {
    return this.http.post<ResultadoVoto>(`${this.url}/${id}/voto`, {});
  }

  retirarVoto(id: number): Observable<ResultadoVoto> {
    return this.http.delete<ResultadoVoto>(`${this.url}/${id}/voto`);
  }

  // ---------- Panel de revisión (EDITOR / ADMIN) ----------

  resumen(): Observable<ResumenRevision> {
    return this.http.get<ResumenRevision>(`${this.url}/resumen`);
  }

  aprobar(id: number, nota: string): Observable<Sugerencia> {
    return this.http.patch<Sugerencia>(`${this.url}/${id}/avanzar`, { nota });
  }

  rechazar(id: number, nota: string): Observable<Sugerencia> {
    return this.http.patch<Sugerencia>(`${this.url}/${id}/rechazar`, { nota });
  }
}
