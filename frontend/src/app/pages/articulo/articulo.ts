import { DatePipe } from '@angular/common';
import { afterNextRender, Component, computed, effect, ElementRef, inject, Injector, input, signal, viewChild } from '@angular/core';
import { Title } from '@angular/platform-browser';
import { RouterLink } from '@angular/router';
import { Anuncio } from '../../components/anuncio/anuncio';
import { BloqueDonacion } from '../../components/bloque-donacion/bloque-donacion';
import { iniciales } from '../../core/etiquetas';
import { mensajeError } from '../../core/errores';
import { ArticuloDetalle, Comentario } from '../../core/modelos';
import { ArticulosService } from '../../core/servicios/articulos.service';
import { AuthService } from '../../core/servicios/auth.service';

interface Seccion { id: string; texto: string; }

@Component({
  selector: 'app-articulo',
  imports: [RouterLink, DatePipe, Anuncio, BloqueDonacion],
  templateUrl: './articulo.html',
  styleUrl: './articulo.css',
})
export class Articulo {
  private readonly articulosService = inject(ArticulosService);
  private readonly titulo = inject(Title);
  private readonly injector = inject(Injector);
  protected readonly auth = inject(AuthService);

  /** :id de la ruta /articulo/:id */
  readonly id = input.required<string>();

  protected readonly iniciales = iniciales;
  protected readonly articulo = signal<ArticuloDetalle | null>(null);
  protected readonly error = signal<string | null>(null);
  protected readonly indice = signal<Seccion[]>([]);
  private readonly contenido = viewChild<ElementRef<HTMLElement>>('contenido');

  // Comentarios
  protected readonly comentarios = signal<Comentario[]>([]);
  protected readonly totalComentarios = computed(() =>
    this.comentarios().reduce((total, c) => total + 1 + c.respuestas.length, 0));
  protected readonly respondiendoA = signal<number | null>(null);
  protected readonly confirmarBorrado = signal<number | null>(null);
  protected readonly enviando = signal(false);
  protected readonly errorComentario = signal<string | null>(null);

  constructor() {
    effect(() => this.cargar(Number(this.id())));
  }

  private cargar(id: number): void {
    this.articulo.set(null);
    this.error.set(null);
    this.articulosService.obtener(id).subscribe({
      next: a => {
        this.articulo.set(a);
        this.titulo.setTitle(`${a.titulo} · IndieDex`);
        // Cuando Angular pinte el contenido, se arma el índice "En esta guía"
        afterNextRender(() => this.prepararIndice(), { injector: this.injector });
      },
      error: e => this.error.set(mensajeError(e)),
    });
    this.cargarComentarios(id);
  }

  private cargarComentarios(id: number): void {
    this.articulosService.comentarios(id).subscribe(c => this.comentarios.set(c));
  }

  /** Pone un id a cada <h2> del contenido para poder enlazarlo desde el índice. */
  private prepararIndice(): void {
    const raiz = this.contenido()?.nativeElement;
    if (!raiz) return;
    const secciones: Seccion[] = [];
    raiz.querySelectorAll('h2').forEach(h2 => {
      const texto = h2.textContent?.trim() ?? '';
      const id = texto.toLowerCase().normalize('NFD').replace(/[̀-ͯ]/g, '')
        .replace(/[^a-z0-9]+/g, '-').replace(/(^-|-$)/g, '');
      h2.id = id;
      secciones.push({ id, texto });
    });
    this.indice.set(secciones);
  }

  // ---------- Comentarios ----------

  protected comentar(campo: HTMLTextAreaElement, padreId: number | null = null): void {
    const texto = campo.value.trim();
    const articulo = this.articulo();
    if (!texto || !articulo) return;

    this.enviando.set(true);
    this.errorComentario.set(null);
    this.articulosService.comentar(articulo.id, texto, padreId).subscribe({
      next: () => {
        campo.value = '';
        this.respondiendoA.set(null);
        this.enviando.set(false);
        this.cargarComentarios(articulo.id);
      },
      error: e => {
        this.errorComentario.set(mensajeError(e));
        this.enviando.set(false);
      },
    });
  }

  protected puedeBorrar(c: Comentario): boolean {
    const yo = this.auth.usuario();
    return !!yo && (yo.id === c.autor.id || this.auth.esEquipo());
  }

  protected borrar(c: Comentario): void {
    if (this.confirmarBorrado() !== c.id) {
      this.confirmarBorrado.set(c.id); // primer clic: pedir confirmación
      return;
    }
    this.articulosService.borrarComentario(c.id).subscribe({
      next: () => {
        this.confirmarBorrado.set(null);
        this.cargarComentarios(Number(this.id()));
      },
      error: e => this.errorComentario.set(mensajeError(e)),
    });
  }

  protected textoEstado(estado: ArticuloDetalle['estado']): string {
    return estado === 'PUBLICADO' ? 'Publicado' : estado === 'EN_REVISION' ? 'En revisión' : 'Borrador';
  }
}
