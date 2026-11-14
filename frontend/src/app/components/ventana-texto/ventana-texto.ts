import { Component, computed, ElementRef, input, viewChild } from '@angular/core';
import { ClaveTexto, TEXTOS } from '../../core/textos';

/**
 * Ventana flotante (modal) con un texto informativo: normas, términos, privacidad...
 * Uso: <app-ventana-texto #normas clave="normas" />  y  (click)="normas.abrir()"
 * Usa el elemento <dialog> nativo: se cierra con Esc, con la X o haciendo clic fuera.
 */
@Component({
  selector: 'app-ventana-texto',
  templateUrl: './ventana-texto.html',
  styleUrl: './ventana-texto.css',
})
export class VentanaTexto {
  readonly clave = input.required<ClaveTexto>();
  protected readonly pagina = computed(() => TEXTOS[this.clave()]);
  private readonly dialogo = viewChild.required<ElementRef<HTMLDialogElement>>('dialogo');

  abrir(): void {
    this.dialogo().nativeElement.showModal();
  }

  cerrar(): void {
    this.dialogo().nativeElement.close();
  }

  /** Clic en el fondo oscuro (fuera de la caja) cierra la ventana. */
  protected clicFondo(evento: MouseEvent): void {
    if (evento.target === this.dialogo().nativeElement) {
      this.cerrar();
    }
  }
}
