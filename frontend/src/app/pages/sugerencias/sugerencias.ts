import { DatePipe } from '@angular/common';
import { Component, effect, inject, OnInit, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { claseEstado, OPCIONES_TIPO, textoEstado, textoTipo } from '../../core/etiquetas';
import { erroresDeCampos, mensajeError } from '../../core/errores';
import { EstadoSugerencia, JuegoResumen, Sugerencia, TipoSugerencia } from '../../core/modelos';
import { AuthService } from '../../core/servicios/auth.service';
import { JuegosService } from '../../core/servicios/juegos.service';
import { SugerenciasService } from '../../core/servicios/sugerencias.service';

const POR_PAGINA = 5;

@Component({
  selector: 'app-sugerencias',
  imports: [DatePipe, FormsModule],
  templateUrl: './sugerencias.html',
  styleUrl: './sugerencias.css',
})
export class Sugerencias implements OnInit {
  private readonly sugerenciasService = inject(SugerenciasService);
  private readonly juegosService = inject(JuegosService);
  private readonly router = inject(Router);
  protected readonly auth = inject(AuthService);

  protected readonly textoEstado = textoEstado;
  protected readonly claseEstado = claseEstado;
  protected readonly textoTipo = textoTipo;
  protected readonly opcionesTipo = OPCIONES_TIPO;
  protected readonly filtros: { estado: EstadoSugerencia | null; texto: string }[] = [
    { estado: null, texto: 'Todas' },
    { estado: 'SUGERENCIA', texto: 'Sugerencia' },
    { estado: 'VOTOS', texto: 'Votos' },
    { estado: 'REVISION', texto: 'Revisión' },
    { estado: 'PUBLICADO', texto: 'Publicado' },
  ];

  // Lista
  protected readonly estado = signal<EstadoSugerencia | null>(null);
  protected readonly orden = signal<'votos' | 'recientes'>('votos');
  protected readonly buscar = signal('');
  protected readonly lista = signal<Sugerencia[]>([]);
  protected readonly total = signal(0);
  protected readonly pagina = signal(0);
  protected readonly hayMas = signal(false);
  protected readonly cargando = signal(false);
  protected readonly errorLista = signal<string | null>(null);
  protected readonly votando = signal<number | null>(null);

  // Formulario de nueva sugerencia (ngModel)
  protected juegos = signal<JuegoResumen[]>([]);
  protected form = { titulo: '', juegoId: null as number | null, tipo: 'NUEVA_GUIA' as TipoSugerencia, descripcion: '', normas: false };
  protected readonly enviando = signal(false);
  protected readonly errorForm = signal<string | null>(null);
  protected readonly erroresCampo = signal<Record<string, string>>({});
  protected readonly exito = signal<string | null>(null);

  constructor() {
    // Al cambiar filtro, orden o búsqueda se empieza desde la primera página
    effect(() => this.cargar(this.estado(), this.orden(), this.buscar(), 0));
  }

  ngOnInit(): void {
    this.juegosService.listar({ orden: 'nombre' }).subscribe(j => {
      this.juegos.set(j);
      this.form.juegoId = j.find(x => x.slug === 'dredge')?.id ?? j[0]?.id ?? null;
    });
  }

  private cargar(estado: EstadoSugerencia | null, orden: 'votos' | 'recientes', buscar: string, pagina: number): void {
    this.cargando.set(true);
    this.sugerenciasService.listar({ estados: estado ? [estado] : [], orden, buscar, pagina, tamano: POR_PAGINA })
      .subscribe({
        next: p => {
          this.lista.update(actual => pagina === 0 ? p.contenido : [...actual, ...p.contenido]);
          this.total.set(p.totalElementos);
          this.pagina.set(p.pagina);
          this.hayMas.set(!p.ultima);
          this.errorLista.set(null);
          this.cargando.set(false);
        },
        error: e => {
          this.errorLista.set(mensajeError(e));
          this.cargando.set(false);
        },
      });
  }

  protected cargarMas(): void {
    this.cargar(this.estado(), this.orden(), this.buscar(), this.pagina() + 1);
  }

  // ---------- Votos ----------

  protected abierta(s: Sugerencia): boolean {
    return s.estado !== 'PUBLICADO' && s.estado !== 'RECHAZADA';
  }

  protected alternarVoto(s: Sugerencia): void {
    if (!this.auth.conectado()) {
      this.router.navigate(['/login'], { queryParams: { volver: '/sugerencias' } });
      return;
    }
    if (!this.abierta(s) || this.votando()) return;

    this.votando.set(s.id);
    const peticion = s.votadaPorMi ? this.sugerenciasService.retirarVoto(s.id) : this.sugerenciasService.votar(s.id);
    peticion.subscribe({
      next: r => {
        this.lista.update(l => l.map(x => x.id === s.id
          ? { ...x, totalVotos: r.totalVotos, votadaPorMi: r.votadaPorMi, estado: r.estado } : x));
        this.votando.set(null);
      },
      error: e => {
        this.errorLista.set(mensajeError(e));
        this.votando.set(null);
      },
    });
  }

  // ---------- Nueva sugerencia ----------

  protected enviar(): void {
    if (!this.auth.conectado()) {
      this.router.navigate(['/login'], { queryParams: { volver: '/sugerencias' } });
      return;
    }
    this.errorForm.set(null);
    this.erroresCampo.set({});
    this.exito.set(null);
    if (!this.form.normas) {
      this.errorForm.set('Confirma que has leído las normas de la comunidad.');
      return;
    }

    this.enviando.set(true);
    this.sugerenciasService.crear({
      titulo: this.form.titulo,
      descripcion: this.form.descripcion,
      tipo: this.form.tipo,
      juegoId: this.form.juegoId,
    }).subscribe({
      next: nueva => {
        this.enviando.set(false);
        this.exito.set('¡Gracias! Tu propuesta ya está en la lista, en estado Sugerencia.');
        this.form = { ...this.form, titulo: '', descripcion: '', normas: false };
        // Se muestra arriba de la lista sin recargar
        if (this.estado() === null || this.estado() === 'SUGERENCIA') {
          this.lista.update(l => [nueva, ...l]);
          this.total.update(t => t + 1);
        }
      },
      error: e => {
        this.enviando.set(false);
        this.erroresCampo.set(erroresDeCampos(e));
        this.errorForm.set(mensajeError(e));
      },
    });
  }
}
