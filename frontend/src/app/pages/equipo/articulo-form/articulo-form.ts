import { Component, effect, ElementRef, inject, input, OnInit, signal, viewChild } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { erroresDeCampos, mensajeError } from '../../../core/errores';
import { IMAGENES_DISPONIBLES } from '../../../core/imagenes';
import { ArticuloDatos, JuegoResumen } from '../../../core/modelos';
import { ArticulosService } from '../../../core/servicios/articulos.service';
import { GestionService } from '../../../core/servicios/gestion.service';
import { JuegosService } from '../../../core/servicios/juegos.service';

/** Bloques que se insertan con la barra del editor (usan las clases de estilo de las guías). */
const BLOQUES: Record<string, string> = {
  intro: '<p class="texto__intro">\n  Párrafo de introducción de la guía.\n</p>\n',
  subtitulo: '<h2>01 / Nueva sección</h2>\n',
  parrafo: '<p>\n  Escribe aquí el texto.\n</p>\n',
  consejo: '<aside class="consejo">\n  <p class="consejo__titulo">Consejo del puerto</p>\n  <p>Un consejo corto y útil.</p>\n</aside>\n',
  imagen: '<figure class="figura figura--interna">\n  <div class="figura__imagen">\n    <img src="images/articulo-pesca.jpg" alt="Describe la imagen">\n  </div>\n  <figcaption>Pie de foto.</figcaption>\n</figure>\n',
  lista: '<dl class="mejoras">\n  <div>\n    <dt>Nombre</dt>\n    <dd>Descripción.</dd>\n  </div>\n</dl>\n',
  fuentes: '<p class="texto__fuentes">\n  Fuentes: …\n</p>\n',
};

/** /equipo/articulos/nuevo?juego=slug y /equipo/articulos/:id/editar */
@Component({
  selector: 'app-articulo-form',
  imports: [FormsModule, RouterLink],
  templateUrl: './articulo-form.html',
  styleUrl: '../equipo.css',
})
export class ArticuloForm implements OnInit {
  private readonly gestion = inject(GestionService);
  private readonly juegosService = inject(JuegosService);
  private readonly articulosService = inject(ArticulosService);
  private readonly router = inject(Router);

  /** :id al editar. */
  readonly id = input<string | undefined>();
  /** ?juego=slug al crear desde la página de un juego. */
  readonly juego = input<string | undefined>();

  private readonly editor = viewChild<ElementRef<HTMLTextAreaElement>>('editor');

  protected readonly imagenes = IMAGENES_DISPONIBLES;
  protected readonly juegos = signal<JuegoResumen[]>([]);
  protected datos: ArticuloDatos = {
    juegoId: null, titulo: '', resumen: null, contenido: BLOQUES['intro'] + '\n' + BLOQUES['subtitulo'] + BLOQUES['parrafo'],
    categoria: null, imagen: null, imagenDescripcion: null, minutosLectura: 5, estado: 'BORRADOR',
  };
  protected readonly cargado = signal(false);
  protected readonly guardando = signal(false);
  protected readonly confirmarBorrado = signal(false);
  protected readonly error = signal<string | null>(null);
  protected readonly errores = signal<Record<string, string>>({});

  constructor() {
    effect(() => {
      const id = this.id();
      if (!id) return;
      this.articulosService.obtener(Number(id)).subscribe({
        next: a => {
          this.datos = {
            juegoId: a.juegoId, titulo: a.titulo, resumen: a.resumen, contenido: a.contenido,
            categoria: a.categoria, imagen: a.imagen, imagenDescripcion: a.imagenDescripcion,
            minutosLectura: a.minutosLectura, estado: a.estado,
          };
          this.cargado.set(true);
        },
        error: e => this.error.set(mensajeError(e)),
      });
    });
  }

  ngOnInit(): void {
    this.juegosService.listar({ orden: 'nombre' }).subscribe(j => {
      this.juegos.set(j);
      if (!this.id()) {
        const elegido = j.find(x => x.slug === this.juego()) ?? j[0];
        this.datos.juegoId = elegido?.id ?? null;
      }
    });
  }

  /** Inserta un bloque de HTML donde está el cursor del editor. */
  protected insertar(bloque: string): void {
    const area = this.editor()?.nativeElement;
    const html = BLOQUES[bloque];
    if (!area) {
      this.datos.contenido += html;
      return;
    }
    const inicio = area.selectionStart ?? this.datos.contenido.length;
    const fin = area.selectionEnd ?? inicio;
    this.datos.contenido = this.datos.contenido.slice(0, inicio) + html + this.datos.contenido.slice(fin);
    setTimeout(() => {
      area.focus();
      area.setSelectionRange(inicio + html.length, inicio + html.length);
    });
  }

  protected guardar(): void {
    const locales: Record<string, string> = {};
    if (!this.datos.titulo.trim()) locales['titulo'] = 'El título es obligatorio';
    if (!this.datos.contenido.trim()) locales['contenido'] = 'El contenido es obligatorio';
    if (!this.datos.juegoId) locales['juegoId'] = 'Elige el juego';
    this.errores.set(locales);
    if (Object.keys(locales).length) return;

    this.guardando.set(true);
    this.error.set(null);
    const id = this.id();
    const peticion = id
      ? this.gestion.actualizarArticulo(Number(id), this.datos)
      : this.gestion.crearArticulo(this.datos);
    peticion.subscribe({
      next: a => this.router.navigate(['/articulo', a.id]),
      error: e => {
        this.errores.set(erroresDeCampos(e));
        this.error.set(mensajeError(e));
        this.guardando.set(false);
      },
    });
  }

  protected borrar(): void {
    if (!this.confirmarBorrado()) {
      this.confirmarBorrado.set(true);
      return;
    }
    const slug = this.juegos().find(j => j.id === this.datos.juegoId)?.slug;
    this.gestion.borrarArticulo(Number(this.id())).subscribe({
      next: () => this.router.navigate(slug ? ['/juegos', slug] : ['/equipo/juegos']),
      error: e => this.error.set(mensajeError(e)),
    });
  }
}
