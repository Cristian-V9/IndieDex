import { HttpClient } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { API_URL } from '../api.config';
import { ComentarioPerfil, Pagina, Perfil, Sugerencia, Usuario } from '../modelos';

@Injectable({ providedIn: 'root' })
export class UsuariosService {
  private readonly http = inject(HttpClient);
  private readonly url = `${API_URL}/usuarios/me`;

  perfil(): Observable<Perfil> {
    return this.http.get<Perfil>(this.url);
  }

  actualizarBiografia(biografia: string): Observable<Usuario> {
    return this.http.put<Usuario>(this.url, { biografia });
  }

  misSugerencias(tamano = 5): Observable<Pagina<Sugerencia>> {
    return this.http.get<Pagina<Sugerencia>>(`${this.url}/sugerencias`, { params: { tamano } });
  }

  misVotos(tamano = 5): Observable<Pagina<Sugerencia>> {
    return this.http.get<Pagina<Sugerencia>>(`${this.url}/votos`, { params: { tamano } });
  }

  misComentarios(tamano = 5): Observable<Pagina<ComentarioPerfil>> {
    return this.http.get<Pagina<ComentarioPerfil>>(`${this.url}/comentarios`, { params: { tamano } });
  }
}
