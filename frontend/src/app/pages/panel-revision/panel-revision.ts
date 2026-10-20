import { DatePipe } from '@angular/common';
import { Component, computed, effect, inject, signal } from '@angular/core';
import { RouterLink } from '@angular/router';
import { claseEstado, textoEstado, textoTipo } from '../../core/etiquetas';
import { mensajeError } from '../../core/errores';
import { EstadoSugerencia, Pagina, ResumenRevision, Sugerencia, TipoSugerencia } from '../../core/modelos';
import { SugerenciasService } from '../../core/servicios/sugerencias.service';

const PENDIENTES: EstadoSugerencia[] = ['SUGERENCIA', 'VOTOS', 'REVISION'];

/** Panel del equipo (EDITOR / ADMIN): aprobar o rechazar propuestas con una nota. */
@Component({
  selector: 'app-panel-revision',
  imports: [RouterLink, DatePipe],
  templateUrl: './panel-revision.html',
  styleUrl: './panel-revision.css',
})
export class PanelRevision {
  private readonly sugerenciasService = inject(SugerenciasService);

  protected readonly textoEstado = textoEstado;
  protected readonly claseEstado = claseEstado;
  protected readonly textoTipo = textoTipo;
  protected readonly tipos: { valor: TipoSugerencia | ''; texto: string }[] = [
    { valor: '', texto: 'Todos los tipos' },
    { valor: 'NUEVA_WIKI', texto: 'Nueva wiki' },
    { valor: 'NUEVA_GUIA', texto: 'Nueva guía' },
    { valor: 'MEJORA', texto: 'Mejora' },
    { valor: 'CORRECCION', texto: 'Corrección' },
  ];

  protected readonly resumen = signal<ResumenRevision | null>(null);
  protected readonly pagina = signal<Pagina<Sugerencia> | null>(null);
  protected readonly numeroPagina = signal(0);
  protected readonly tipo = signal<TipoSugerencia | ''>('');
  protected readonly buscar = signal('');
  protected readonly sincronizado = signal<Date | null>(null);
  protected readonly error = signal<string | null>(null);
  protected readonly aviso = signal<string | null>(null);

  /** Fila con el formulario de nota abierto y la acción elegida. */
  protected readonly decidiendo = signal<{ id: number; accion: 'aprobar' | 'rechazar' } | null>(null);
  protected readonly procesando = signal(false);

  protected readonly siguienteEstado = computed(() => {
    const d = this.decidiendo();
    const s = this.pagina()?.contenido.find(x => x.id === d?.id);
    if (!s) return '';
    return s.estado === 'SUGERENCIA' ? 'Votos' : s.estado === 'VOTOS' ? 'Revisión' : 'Publicado';
  });

  constructor() {
    effect(() => this.cargar(this.tipo(), this.buscar(), this.numeroPagina()));
    this.cargarResumen();
  }

  private cargar(tipo: TipoSugerencia | '', buscar: string, pagina: number): void {
    this.sugerenciasService.listar({ estados: PENDIENTES, tipo, buscar, orden: 'votos', pagina, tamano: 5 }).subscribe({
      next: p => {
        this.pagina.set(p);
        this.sincronizado.set(new Date());
        this.error.set(null);
      },
      error: e => this.error.set(mensajeError(e)),
    });
  }

  private cargarResumen(): void {
    this.sugerenciasService.resumen().subscribe(r => this.resumen.set(r));
  }

  protected filtrarTipo(valor: string): void {
    this.numeroPagina.set(0);
    this.tipo.set(valor as TipoSugerencia | '');
  }

  protected filtrarTexto(valor: string): void {
    this.numeroPagina.set(0);
    this.buscar.set(valor.trim());
  }

  protected abrir(s: Sugerencia, accion: 'aprobar' | 'rechazar'): void {
    const actual = this.decidiendo();
    this.decidiendo.set(actual?.id === s.id && actual.accion === accion ? null : { id: s.id, accion });
    this.aviso.set(null);
    this.error.set(null);
  }

  protected confirmar(s: Sugerencia, nota: string): void {
    const d = this.decidiendo();
    if (!d) return;
    if (d.accion === 'rechazar' && !nota.trim()) {
      this.error.set('Escribe una nota para explicarle al autor por qué se rechaza.');
      return;
    }
    this.procesando.set(true);
    const peticion = d.accion === 'aprobar'
      ? this.sugerenciasService.aprobar(s.id, nota.trim())
      : this.sugerenciasService.rechazar(s.id, nota.trim());

    peticion.subscribe({
      next: r => {
        this.aviso.set(`«${r.titulo}» ahora está en ${textoEstado(r.estado)}.`);
        this.decidiendo.set(null);
        this.procesando.set(false);
        this.cargar(this.tipo(), this.buscar(), this.numeroPagina());
        this.cargarResumen();
      },
      error: e => {
        this.error.set(mensajeError(e));
        this.procesando.set(false);
      },
    });
  }

  protected cambiarPagina(delta: number): void {
    this.numeroPagina.update(n => Math.max(0, n + delta));
    this.decidiendo.set(null);
  }
}
