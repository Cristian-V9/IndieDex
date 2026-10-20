import { HttpClient } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { API_URL } from '../api.config';
import { Estadisticas, ResultadoBusqueda } from '../modelos';

@Injectable({ providedIn: 'root' })
export class GeneralService {
  private readonly http = inject(HttpClient);

  estadisticas(): Observable<Estadisticas> {
    return this.http.get<Estadisticas>(`${API_URL}/estadisticas`);
  }

  buscar(q: string): Observable<ResultadoBusqueda> {
    return this.http.get<ResultadoBusqueda>(`${API_URL}/buscar`, { params: { q } });
  }
}
