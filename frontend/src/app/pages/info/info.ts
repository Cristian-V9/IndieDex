import { Component, computed, inject, input, effect } from '@angular/core';
import { Title } from '@angular/platform-browser';
import { RouterLink } from '@angular/router';
import { ClaveTexto, TEXTOS } from '../../core/textos';

/** Páginas del pie: /sobre, /normas, /contacto, /privacidad, /terminos y /cookies. */
@Component({
  selector: 'app-info',
  imports: [RouterLink],
  templateUrl: './info.html',
  styleUrl: './info.css',
})
export class Info {
  private readonly titulo = inject(Title);

  /** Llega desde data: { clave } de la ruta. */
  readonly clave = input.required<ClaveTexto>();
  protected readonly pagina = computed(() => TEXTOS[this.clave()]);

  constructor() {
    effect(() => this.titulo.setTitle(`${this.pagina().titulo} · IndieDex`));
  }
}
