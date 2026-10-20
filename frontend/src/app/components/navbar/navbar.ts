import { Component, inject, signal } from '@angular/core';
import { Router, RouterLink, RouterLinkActive } from '@angular/router';
import { Subject, debounceTime, distinctUntilChanged, of, switchMap, catchError } from 'rxjs';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { iniciales } from '../../core/etiquetas';
import { ResultadoBusqueda } from '../../core/modelos';
import { AuthService } from '../../core/servicios/auth.service';
import { GeneralService } from '../../core/servicios/general.service';

@Component({
  selector: 'app-navbar',
  imports: [RouterLink, RouterLinkActive],
  templateUrl: './navbar.html',
  styleUrl: './navbar.css',
})
export class Navbar {
  protected readonly auth = inject(AuthService);
  private readonly general = inject(GeneralService);
  private readonly router = inject(Router);

  protected readonly iniciales = iniciales;
  protected readonly texto = signal('');
  protected readonly resultados = signal<ResultadoBusqueda | null>(null);
  protected readonly abierto = signal(false);

  private readonly busqueda$ = new Subject<string>();

  constructor() {
    // Busca mientras se escribe (espera 300 ms entre teclas)
    this.busqueda$.pipe(
      debounceTime(300),
      distinctUntilChanged(),
      switchMap(q => q.trim().length < 2
        ? of(null)
        : this.general.buscar(q.trim()).pipe(catchError(() => of(null)))),
      takeUntilDestroyed(),
    ).subscribe(r => this.resultados.set(r));
  }

  protected escribir(valor: string): void {
    this.texto.set(valor);
    this.abierto.set(true);
    this.busqueda$.next(valor);
  }

  /** Enter: lleva al catálogo filtrado por el texto. */
  protected buscarEnCatalogo(): void {
    const q = this.texto().trim();
    this.cerrar();
    this.router.navigate(['/juegos'], { queryParams: q ? { buscar: q } : {} });
  }

  protected cerrar(): void {
    this.abierto.set(false);
  }

  /** Cierra el desplegable al salir del buscador (después del clic en un resultado). */
  protected alSalir(): void {
    setTimeout(() => this.cerrar(), 150);
  }

  /** Baja al bloque de donación de la página actual, o va al catálogo (que siempre lo tiene). */
  protected irADonar(evento: Event): void {
    evento.preventDefault();
    const bloque = document.getElementById('donar');
    if (bloque) {
      bloque.scrollIntoView({ behavior: 'smooth' });
    } else {
      this.router.navigate(['/juegos'], { fragment: 'donar' });
    }
  }

  protected hayResultados(r: ResultadoBusqueda): boolean {
    return r.juegos.length + r.articulos.length + r.objetos.length + r.personajes.length > 0;
  }
}
