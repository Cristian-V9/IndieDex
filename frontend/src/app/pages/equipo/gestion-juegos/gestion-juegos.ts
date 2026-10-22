import { Component, inject, OnInit, signal } from '@angular/core';
import { RouterLink, RouterLinkActive } from '@angular/router';
import { textoWiki } from '../../../core/etiquetas';
import { mensajeError } from '../../../core/errores';
import { JuegoResumen } from '../../../core/modelos';
import { AuthService } from '../../../core/servicios/auth.service';
import { GestionService } from '../../../core/servicios/gestion.service';
import { JuegosService } from '../../../core/servicios/juegos.service';

/** /equipo/juegos — lista de juegos para el equipo. */
@Component({
  selector: 'app-gestion-juegos',
  imports: [RouterLink, RouterLinkActive],
  templateUrl: './gestion-juegos.html',
  styleUrl: '../equipo.css',
})
export class GestionJuegos implements OnInit {
  private readonly juegosService = inject(JuegosService);
  private readonly gestion = inject(GestionService);
  protected readonly auth = inject(AuthService);

  protected readonly textoWiki = textoWiki;
  protected readonly juegos = signal<JuegoResumen[]>([]);
  protected readonly error = signal<string | null>(null);
  protected readonly aviso = signal<string | null>(null);
  protected readonly confirmarBorrado = signal<number | null>(null);

  ngOnInit(): void {
    this.cargar();
  }

  private cargar(): void {
    this.juegosService.listar({ orden: 'nombre' }).subscribe({
      next: j => this.juegos.set(j),
      error: e => this.error.set(mensajeError(e)),
    });
  }

  /** Primer clic pide confirmación; el segundo borra. Solo ADMIN. */
  protected borrar(juego: JuegoResumen): void {
    if (this.confirmarBorrado() !== juego.id) {
      this.confirmarBorrado.set(juego.id);
      return;
    }
    this.gestion.borrarJuego(juego.id).subscribe({
      next: () => {
        this.aviso.set(`«${juego.titulo}» y toda su wiki se borraron.`);
        this.confirmarBorrado.set(null);
        this.cargar();
      },
      error: e => this.error.set(mensajeError(e)),
    });
  }
}
