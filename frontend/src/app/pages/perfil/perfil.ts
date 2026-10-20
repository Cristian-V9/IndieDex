import { DatePipe } from '@angular/common';
import { Component, computed, inject, OnInit, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { claseEstado, iniciales, textoEstado, textoRol } from '../../core/etiquetas';
import { mensajeError } from '../../core/errores';
import { ComentarioPerfil, Perfil as PerfilDatos, Sugerencia } from '../../core/modelos';
import { AuthService } from '../../core/servicios/auth.service';
import { SugerenciasService } from '../../core/servicios/sugerencias.service';
import { UsuariosService } from '../../core/servicios/usuarios.service';

type Pestana = 'actividad' | 'sugerencias' | 'votos' | 'comentarios';

@Component({
  selector: 'app-perfil',
  imports: [RouterLink, DatePipe, FormsModule],
  templateUrl: './perfil.html',
  styleUrl: './perfil.css',
})
export class Perfil implements OnInit {
  private readonly usuarios = inject(UsuariosService);
  private readonly sugerenciasService = inject(SugerenciasService);
  protected readonly auth = inject(AuthService);

  protected readonly iniciales = iniciales;
  protected readonly textoRol = textoRol;
  protected readonly textoEstado = textoEstado;
  protected readonly claseEstado = claseEstado;
  protected readonly pestanas: { id: Pestana; texto: string }[] = [
    { id: 'actividad', texto: 'Mi actividad' },
    { id: 'sugerencias', texto: 'Sugerencias' },
    { id: 'votos', texto: 'Votos' },
    { id: 'comentarios', texto: 'Comentarios' },
  ];

  protected readonly pestana = signal<Pestana>('actividad');
  protected readonly perfil = signal<PerfilDatos | null>(null);
  protected readonly sugerencias = signal<Sugerencia[]>([]);
  protected readonly votos = signal<Sugerencia[]>([]);
  protected readonly comentarios = signal<ComentarioPerfil[]>([]);
  protected readonly error = signal<string | null>(null);

  protected readonly editando = signal(false);
  protected biografia = '';

  /** En "Mi actividad" se muestran 3 de cada; en cada pestaña, todos los cargados. */
  protected readonly limite = computed(() => this.pestana() === 'actividad' ? 3 : 20);

  /** El juego que más aparece en sus sugerencias. */
  protected readonly wikiFavorita = computed(() => {
    const cuenta = new Map<string, { titulo: string; slug: string; n: number }>();
    for (const s of this.sugerencias()) {
      if (!s.juegoSlug || !s.juegoTitulo) continue;
      const actual = cuenta.get(s.juegoSlug) ?? { titulo: s.juegoTitulo, slug: s.juegoSlug, n: 0 };
      actual.n++;
      cuenta.set(s.juegoSlug, actual);
    }
    return [...cuenta.values()].sort((a, b) => b.n - a.n)[0] ?? { titulo: 'Dredge', slug: 'dredge', n: 0 };
  });

  ngOnInit(): void {
    this.cargar();
  }

  private cargar(): void {
    this.usuarios.perfil().subscribe({
      next: p => this.perfil.set(p),
      error: e => this.error.set(mensajeError(e)),
    });
    this.usuarios.misSugerencias(20).subscribe(p => this.sugerencias.set(p.contenido));
    this.usuarios.misVotos(20).subscribe(p => this.votos.set(p.contenido));
    this.usuarios.misComentarios(20).subscribe(p => this.comentarios.set(p.contenido));
  }

  protected ver(p: Pestana): boolean {
    return this.pestana() === 'actividad' || this.pestana() === p;
  }

  protected empezarEdicion(): void {
    this.biografia = this.perfil()?.usuario.biografia ?? '';
    this.editando.set(true);
  }

  protected guardarBiografia(): void {
    this.usuarios.actualizarBiografia(this.biografia).subscribe({
      next: u => {
        this.perfil.update(p => p ? { ...p, usuario: u } : p);
        this.auth.actualizarUsuario(u);
        this.editando.set(false);
      },
      error: e => this.error.set(mensajeError(e)),
    });
  }

  protected retirarVoto(s: Sugerencia): void {
    this.sugerenciasService.retirarVoto(s.id).subscribe({
      next: () => {
        this.votos.update(v => v.filter(x => x.id !== s.id));
        this.perfil.update(p => p ? { ...p, totalVotos: p.totalVotos - 1 } : p);
      },
      error: e => this.error.set(mensajeError(e)),
    });
  }

  protected cerrada(s: Sugerencia): boolean {
    return s.estado === 'PUBLICADO' || s.estado === 'RECHAZADA';
  }
}
