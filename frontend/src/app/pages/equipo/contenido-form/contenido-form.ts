import { Component, computed, effect, inject, input, OnInit, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { erroresDeCampos, mensajeError } from '../../../core/errores';
import { IMAGENES_DISPONIBLES } from '../../../core/imagenes';
import { JuegoResumen, Logro, Objeto, Personaje, TipoContenido } from '../../../core/modelos';
import { GestionService } from '../../../core/servicios/gestion.service';
import { JuegosService } from '../../../core/servicios/juegos.service';

/** Todos los campos posibles; cada tipo usa los suyos. */
interface Campos {
  juegoId: number | null;
  nombre: string;
  descripcion: string;      // logro, objeto
  comoConseguirlo: string;  // logro
  historia: string;         // personaje
  tipo: string;             // objeto
  rareza: string;           // objeto
  rol: string;              // personaje
  ubicacion: string;        // objeto, personaje
  imagen: string;           // objeto, personaje
  icono: string;            // logro
}

const NOMBRES: Record<TipoContenido, { singular: string; nuevo: string }> = {
  logros: { singular: 'logro', nuevo: 'Nuevo logro' },
  objetos: { singular: 'objeto', nuevo: 'Nuevo objeto' },
  personajes: { singular: 'personaje', nuevo: 'Nuevo personaje' },
};

/** /equipo/:tipo/nuevo?juego=slug y /equipo/:tipo/:id/editar  (tipo = logros | objetos | personajes) */
@Component({
  selector: 'app-contenido-form',
  imports: [FormsModule, RouterLink],
  templateUrl: './contenido-form.html',
  styleUrl: '../equipo.css',
})
export class ContenidoForm implements OnInit {
  private readonly gestion = inject(GestionService);
  private readonly juegosService = inject(JuegosService);
  private readonly router = inject(Router);

  readonly tipo = input.required<string>();
  readonly id = input<string | undefined>();
  readonly juego = input<string | undefined>();

  protected readonly imagenes = IMAGENES_DISPONIBLES;
  protected readonly clase = computed<TipoContenido>(() => {
    const t = this.tipo();
    return t === 'objetos' || t === 'personajes' ? t : 'logros';
  });
  protected readonly nombres = computed(() => NOMBRES[this.clase()]);

  protected readonly juegos = signal<JuegoResumen[]>([]);
  protected datos: Campos = {
    juegoId: null, nombre: '', descripcion: '', comoConseguirlo: '', historia: '', tipo: '',
    rareza: '', rol: '', ubicacion: '', imagen: '', icono: '',
  };
  protected readonly guardando = signal(false);
  protected readonly confirmarBorrado = signal(false);
  protected readonly error = signal<string | null>(null);
  protected readonly errores = signal<Record<string, string>>({});

  constructor() {
    effect(() => {
      const id = this.id();
      const tipo = this.clase();
      if (!id) return;
      this.gestion.obtenerContenido(tipo, Number(id)).subscribe({
        next: c => this.rellenar(c),
        error: e => this.error.set(mensajeError(e)),
      });
    });
  }

  ngOnInit(): void {
    if (!['logros', 'objetos', 'personajes'].includes(this.tipo())) {
      this.router.navigate(['/equipo/juegos']);
      return;
    }
    this.juegosService.listar({ orden: 'nombre' }).subscribe(j => {
      this.juegos.set(j);
      if (!this.id()) {
        this.datos.juegoId = (j.find(x => x.slug === this.juego()) ?? j[0])?.id ?? null;
      }
    });
  }

  private rellenar(c: Logro | Objeto | Personaje): void {
    const x = c as Partial<Logro & Objeto & Personaje>;
    this.datos = {
      juegoId: x.juegoId ?? null, nombre: x.nombre ?? '', descripcion: x.descripcion ?? '',
      comoConseguirlo: x.comoConseguirlo ?? '', historia: x.historia ?? '', tipo: x.tipo ?? '',
      rareza: x.rareza ?? '', rol: x.rol ?? '', ubicacion: x.ubicacion ?? '', imagen: x.imagen ?? '', icono: x.icono ?? '',
    };
  }

  /** Arma el cuerpo que espera el backend según el tipo (LogroRequest, ObjetoRequest o PersonajeRequest). */
  private cuerpo() {
    const d = this.datos;
    const v = (s: string) => s.trim() === '' ? null : s.trim();
    switch (this.clase()) {
      case 'logros':
        return { juegoId: d.juegoId, nombre: d.nombre.trim(), descripcion: v(d.descripcion), comoConseguirlo: v(d.comoConseguirlo), icono: v(d.icono) };
      case 'objetos':
        return { juegoId: d.juegoId, nombre: d.nombre.trim(), descripcion: v(d.descripcion), tipo: v(d.tipo), rareza: v(d.rareza), ubicacion: v(d.ubicacion), imagen: v(d.imagen) };
      case 'personajes':
        return { juegoId: d.juegoId, nombre: d.nombre.trim(), historia: v(d.historia), rol: v(d.rol), ubicacion: v(d.ubicacion), imagen: v(d.imagen) };
    }
  }

  private volverAlJuego(): void {
    const slug = this.juegos().find(j => j.id === this.datos.juegoId)?.slug;
    this.router.navigate(slug ? ['/juegos', slug] : ['/equipo/juegos'], { queryParams: slug ? { seccion: this.clase() } : {} });
  }

  protected guardar(): void {
    if (!this.datos.nombre.trim()) {
      this.errores.set({ nombre: 'El nombre es obligatorio' });
      return;
    }
    this.guardando.set(true);
    this.error.set(null);
    this.errores.set({});
    const id = this.id();
    const peticion = id
      ? this.gestion.actualizarContenido(this.clase(), Number(id), this.cuerpo())
      : this.gestion.crearContenido(this.clase(), this.cuerpo());
    peticion.subscribe({
      next: () => this.volverAlJuego(),
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
    this.gestion.borrarContenido(this.clase(), Number(this.id())).subscribe({
      next: () => this.volverAlJuego(),
      error: e => this.error.set(mensajeError(e)),
    });
  }
}
