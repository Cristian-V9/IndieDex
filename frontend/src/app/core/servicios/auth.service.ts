import { HttpClient } from '@angular/common/http';
import { computed, inject, Injectable, signal } from '@angular/core';
import { Router } from '@angular/router';
import { Observable, tap } from 'rxjs';
import { API_URL } from '../api.config';
import { Sesion, Usuario } from '../modelos';

const CLAVE = 'indiedex.sesion';

/**
 * Guarda la sesión (token JWT + usuario).
 * "Mantener mi sesión iniciada" usa localStorage; si no, sessionStorage (se borra al cerrar el navegador).
 */
@Injectable({ providedIn: 'root' })
export class AuthService {
  private readonly http = inject(HttpClient);
  private readonly router = inject(Router);

  private readonly sesion = signal<Sesion | null>(this.leerSesionGuardada());

  readonly usuario = computed(() => this.sesion()?.usuario ?? null);
  readonly token = computed(() => this.sesion()?.token ?? null);
  readonly conectado = computed(() => this.sesion() !== null);
  readonly esEquipo = computed(() => {
    const rol = this.usuario()?.rol;
    return rol === 'EDITOR' || rol === 'ADMIN';
  });

  login(usuario: string, password: string, recordar: boolean): Observable<Sesion> {
    return this.http.post<Sesion>(`${API_URL}/auth/login`, { usuario, password })
      .pipe(tap(s => this.guardar(s, recordar)));
  }

  registrar(nombreUsuario: string, email: string, password: string): Observable<Sesion> {
    return this.http.post<Sesion>(`${API_URL}/auth/registro`, { nombreUsuario, email, password })
      .pipe(tap(s => this.guardar(s, false)));
  }

  /** Actualiza los datos del usuario guardados (por ejemplo, tras editar la biografía). */
  actualizarUsuario(usuario: Usuario): void {
    const actual = this.sesion();
    if (actual) {
      const nueva = { ...actual, usuario };
      this.sesion.set(nueva);
      this.almacen()?.setItem(CLAVE, JSON.stringify(nueva));
    }
  }

  cerrarSesion(irAlInicio = true): void {
    this.sesion.set(null);
    try {
      localStorage.removeItem(CLAVE);
      sessionStorage.removeItem(CLAVE);
    } catch { /* almacenamiento no disponible */ }
    if (irAlInicio) {
      this.router.navigateByUrl('/');
    }
  }

  private guardar(sesion: Sesion, recordar: boolean): void {
    this.sesion.set(sesion);
    try {
      localStorage.removeItem(CLAVE);
      sessionStorage.removeItem(CLAVE);
      (recordar ? localStorage : sessionStorage).setItem(CLAVE, JSON.stringify(sesion));
    } catch { /* almacenamiento no disponible: la sesión dura mientras la pestaña esté abierta */ }
  }

  private almacen(): Storage | null {
    try {
      return localStorage.getItem(CLAVE) ? localStorage : sessionStorage;
    } catch {
      return null;
    }
  }

  private leerSesionGuardada(): Sesion | null {
    try {
      const texto = localStorage.getItem(CLAVE) ?? sessionStorage.getItem(CLAVE);
      if (!texto) return null;
      const sesion = JSON.parse(texto) as Sesion;
      // Si el token ya expiró, se descarta
      return new Date(sesion.expiraEn).getTime() > Date.now() ? sesion : null;
    } catch {
      return null;
    }
  }
}
