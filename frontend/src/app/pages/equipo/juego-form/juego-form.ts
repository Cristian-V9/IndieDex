import { Component, effect, inject, input, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { erroresDeCampos, mensajeError } from '../../../core/errores';
import { IMAGENES_DISPONIBLES } from '../../../core/imagenes';
import { JuegoDatos } from '../../../core/modelos';
import { GestionService } from '../../../core/servicios/gestion.service';
import { JuegosService } from '../../../core/servicios/juegos.service';

const VACIO: JuegoDatos = {
  titulo: '', slug: null, eslogan: null, descripcion: null, imagenPortada: null, imagenBanner: null,
  desarrollador: null, editor: null, fechaLanzamiento: null, generos: null, modoJuego: 'Un jugador',
  plataformas: null, estadoWiki: 'EN_PREPARACION',
};

/** /equipo/juegos/nuevo y /equipo/juegos/:slug/editar */
@Component({
  selector: 'app-juego-form',
  imports: [FormsModule, RouterLink],
  templateUrl: './juego-form.html',
  styleUrl: '../equipo.css',
})
export class JuegoForm {
  private readonly gestion = inject(GestionService);
  private readonly juegosService = inject(JuegosService);
  private readonly router = inject(Router);

  /** Presente solo al editar (viene de la ruta). */
  readonly slug = input<string | undefined>();

  protected readonly imagenes = IMAGENES_DISPONIBLES;
  protected datos: JuegoDatos = { ...VACIO };
  protected readonly idJuego = signal<number | null>(null);
  protected readonly guardando = signal(false);
  protected readonly error = signal<string | null>(null);
  protected readonly errores = signal<Record<string, string>>({});

  constructor() {
    effect(() => {
      const slug = this.slug();
      if (!slug) return;
      this.juegosService.obtener(slug).subscribe({
        next: j => {
          this.idJuego.set(j.id);
          this.datos = {
            titulo: j.titulo, slug: j.slug, eslogan: j.eslogan, descripcion: j.descripcion,
            imagenPortada: j.imagenPortada, imagenBanner: j.imagenBanner, desarrollador: j.desarrollador,
            editor: j.editor, fechaLanzamiento: j.fechaLanzamiento, generos: j.generos, modoJuego: j.modoJuego,
            plataformas: j.plataformas, estadoWiki: j.estadoWiki,
          };
        },
        error: e => this.error.set(mensajeError(e)),
      });
    });
  }

  protected guardar(): void {
    if (!this.datos.titulo.trim()) {
      this.errores.set({ titulo: 'El título es obligatorio' });
      return;
    }
    this.guardando.set(true);
    this.error.set(null);
    this.errores.set({});

    // Los campos vacíos se mandan como null; el slug vacío lo genera el backend desde el título
    const limpio = Object.fromEntries(
      Object.entries(this.datos).map(([k, v]) => [k, typeof v === 'string' && v.trim() === '' ? null : v]),
    ) as unknown as JuegoDatos;

    const id = this.idJuego();
    const peticion = id ? this.gestion.actualizarJuego(id, limpio) : this.gestion.crearJuego(limpio);
    peticion.subscribe({
      next: j => this.router.navigate(['/juegos', j.slug]),
      error: e => {
        this.errores.set(erroresDeCampos(e));
        this.error.set(mensajeError(e));
        this.guardando.set(false);
      },
    });
  }
}
