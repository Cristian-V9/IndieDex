import { DatePipe } from '@angular/common';
import { Component, inject, OnInit, signal } from '@angular/core';
import { Router, RouterLink } from '@angular/router';
import { claseEstado, textoEstado, textoWiki } from '../../core/etiquetas';
import { mensajeError } from '../../core/errores';
import { ArticuloResumen, Estadisticas, JuegoResumen, Sugerencia } from '../../core/modelos';
import { ArticulosService } from '../../core/servicios/articulos.service';
import { AuthService } from '../../core/servicios/auth.service';
import { GeneralService } from '../../core/servicios/general.service';
import { JuegosService } from '../../core/servicios/juegos.service';
import { SugerenciasService } from '../../core/servicios/sugerencias.service';

@Component({
  selector: 'app-inicio',
  imports: [RouterLink, DatePipe],
  templateUrl: './inicio.html',
  styleUrl: './inicio.css',
})
export class Inicio implements OnInit {
  private readonly general = inject(GeneralService);
  private readonly juegosService = inject(JuegosService);
  private readonly articulosService = inject(ArticulosService);
  private readonly sugerenciasService = inject(SugerenciasService);
  private readonly router = inject(Router);
  protected readonly auth = inject(AuthService);

  protected readonly textoWiki = textoWiki;
  protected readonly textoEstado = textoEstado;
  protected readonly claseEstado = claseEstado;

  protected readonly cifras = signal<Estadisticas | null>(null);
  protected readonly juegos = signal<JuegoResumen[]>([]);
  protected readonly recientes = signal<ArticuloResumen[]>([]);
  protected readonly sugerencias = signal<Sugerencia[]>([]);
  protected readonly error = signal<string | null>(null);

  ngOnInit(): void {
    this.general.estadisticas().subscribe({
      next: c => this.cifras.set(c),
      error: e => this.error.set(mensajeError(e)),
    });
    this.juegosService.listar({ orden: 'destacados' }).subscribe(j => this.juegos.set(j.slice(0, 3)));
    this.articulosService.recientes(3).subscribe(a => this.recientes.set(a));
    this.sugerenciasService.listar({ orden: 'votos', tamano: 3 }).subscribe(p => this.sugerencias.set(p.contenido));
  }

  protected buscar(texto: string): void {
    const q = texto.trim();
    this.router.navigate(['/juegos'], { queryParams: q ? { buscar: q } : {} });
  }
}
