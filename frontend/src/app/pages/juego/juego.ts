import { DatePipe } from '@angular/common';
import { Component, computed, effect, inject, input, signal } from '@angular/core';
import { Title } from '@angular/platform-browser';
import { RouterLink } from '@angular/router';
import { Anuncio } from '../../components/anuncio/anuncio';
import { BloqueDonacion } from '../../components/bloque-donacion/bloque-donacion';
import { textoWiki } from '../../core/etiquetas';
import { mensajeError } from '../../core/errores';
import { ArticuloResumen, JuegoDetalle, Logro, Objeto, Pagina, Personaje } from '../../core/modelos';
import { JuegosService } from '../../core/servicios/juegos.service';

type Seccion = 'guias' | 'logros' | 'objetos' | 'personajes';

@Component({
  selector: 'app-juego',
  imports: [RouterLink, DatePipe, Anuncio, BloqueDonacion],
  templateUrl: './juego.html',
  styleUrl: './juego.css',
})
export class Juego {
  private readonly juegosService = inject(JuegosService);
  private readonly titulo = inject(Title);

  /** :slug de la ruta /juegos/:slug */
  readonly slug = input.required<string>();
  /** ?seccion=logros|objetos|personajes (por defecto, guías) */
  readonly seccion = input<string | undefined>();

  protected readonly textoWiki = textoWiki;
  protected readonly pestanas: { id: Seccion; texto: string }[] = [
    { id: 'logros', texto: 'Logros' },
    { id: 'objetos', texto: 'Objetos' },
    { id: 'personajes', texto: 'Personajes' },
    { id: 'guias', texto: 'Guías' },
  ];

  protected readonly juego = signal<JuegoDetalle | null>(null);
  protected readonly error = signal<string | null>(null);
  protected readonly activa = computed<Seccion>(() => {
    const s = this.seccion();
    return s === 'logros' || s === 'objetos' || s === 'personajes' ? s : 'guias';
  });

  // Contenido de cada pestaña
  protected readonly guias = signal<Pagina<ArticuloResumen> | null>(null);
  protected readonly logros = signal<Logro[] | null>(null);
  protected readonly objetos = signal<Objeto[] | null>(null);
  protected readonly personajes = signal<Personaje[] | null>(null);

  protected readonly buscarGuias = signal('');
  protected readonly pagina = signal(0);
  protected readonly paginas = computed(() =>
    Array.from({ length: this.guias()?.totalPaginas ?? 0 }, (_, i) => i));

  constructor() {
    // Ficha del juego
    effect(() => {
      const slug = this.slug();
      this.juego.set(null);
      this.error.set(null);
      this.juegosService.obtener(slug).subscribe({
        next: j => {
          this.juego.set(j);
          this.titulo.setTitle(`${j.titulo} · IndieDex`);
        },
        error: e => this.error.set(mensajeError(e)),
      });
    });

    // Contenido de la pestaña activa
    effect(() => {
      const slug = this.slug();
      switch (this.activa()) {
        case 'guias':
          this.juegosService.guias(slug, this.buscarGuias(), this.pagina()).subscribe(p => this.guias.set(p));
          break;
        case 'logros':
          this.juegosService.logros(slug).subscribe(l => this.logros.set(l));
          break;
        case 'objetos':
          this.juegosService.objetos(slug).subscribe(o => this.objetos.set(o));
          break;
        case 'personajes':
          this.juegosService.personajes(slug).subscribe(p => this.personajes.set(p));
          break;
      }
    });
  }

  protected buscar(texto: string): void {
    this.pagina.set(0);
    this.buscarGuias.set(texto.trim());
  }

  protected irAPagina(evento: Event, pagina: number): void {
    evento.preventDefault();
    this.pagina.set(pagina);
  }

  /** Icono de la tarjeta según la categoría de la guía. */
  protected icono(categoria: string | null): string {
    switch ((categoria ?? '').toLowerCase()) {
      case 'principiantes': return 'brujula';
      case 'progresión': return 'barco';
      case 'historia': return 'gema';
      case 'pesca': return 'pez';
      default: return 'libro';
    }
  }
}
