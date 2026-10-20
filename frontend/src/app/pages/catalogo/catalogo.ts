import { Component, computed, effect, inject, input, signal } from '@angular/core';
import { RouterLink } from '@angular/router';
import { Subscription } from 'rxjs';
import { Anuncio } from '../../components/anuncio/anuncio';
import { BloqueDonacion } from '../../components/bloque-donacion/bloque-donacion';
import { textoWiki } from '../../core/etiquetas';
import { mensajeError } from '../../core/errores';
import { EstadoWiki, JuegoResumen } from '../../core/modelos';
import { JuegosService } from '../../core/servicios/juegos.service';

type Orden = 'destacados' | 'articulos' | 'nombre';

@Component({
  selector: 'app-catalogo',
  imports: [RouterLink, Anuncio, BloqueDonacion],
  templateUrl: './catalogo.html',
  styleUrl: './catalogo.css',
})
export class Catalogo {
  private readonly juegosService = inject(JuegosService);

  /** ?buscar=... en la URL (desde el buscador de la navbar o del inicio). */
  readonly buscar = input<string | undefined>();

  protected readonly generos = ['Aventura', 'Metroidvania', 'Simulación', 'Plataformas', 'Roguelike', 'Cooperativo'];
  protected readonly textoWiki = textoWiki;

  protected readonly texto = signal('');
  protected readonly genero = signal('');
  protected readonly estado = signal<EstadoWiki | ''>('');
  protected readonly orden = signal<Orden>('destacados');

  protected readonly juegos = signal<JuegoResumen[]>([]);
  protected readonly cargando = signal(true);
  protected readonly error = signal<string | null>(null);

  /** El filtro de disponibilidad se aplica aquí para poder mostrar los contadores. */
  protected readonly visibles = computed(() =>
    this.estado() ? this.juegos().filter(j => j.estadoWiki === this.estado()) : this.juegos());
  protected readonly abiertas = computed(() => this.juegos().filter(j => j.estadoWiki === 'ABIERTA').length);
  protected readonly enPreparacion = computed(() => this.juegos().length - this.abiertas());

  private peticion?: Subscription;

  constructor() {
    effect(() => this.texto.set(this.buscar() ?? ''));
    // Cada vez que cambia un filtro, se vuelve a pedir el catálogo al backend
    effect(() => this.cargar(this.texto(), this.genero(), this.orden()));
  }

  private cargar(buscar: string, genero: string, orden: Orden): void {
    this.peticion?.unsubscribe();
    this.cargando.set(true);
    this.peticion = this.juegosService.listar({ buscar, genero, orden }).subscribe({
      next: j => {
        this.juegos.set(j);
        this.error.set(null);
        this.cargando.set(false);
      },
      error: e => {
        this.error.set(mensajeError(e));
        this.cargando.set(false);
      },
    });
  }

  protected alternarEstado(estado: EstadoWiki): void {
    this.estado.update(actual => actual === estado ? '' : estado);
  }

  protected limpiar(evento: Event): void {
    evento.preventDefault();
    this.texto.set('');
    this.genero.set('');
    this.estado.set('');
    this.orden.set('destacados');
  }
}
