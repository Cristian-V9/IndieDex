import { Component, signal } from '@angular/core';
import { RouterLink } from '@angular/router';

/**
 * /donar — página de apoyo a IndieDex.
 * Es una demostración para la presentación: no hay pasarela de pago ni se pide ningún dato.
 */
@Component({
  selector: 'app-donar',
  imports: [RouterLink],
  templateUrl: './donar.html',
  styleUrl: './donar.css',
})
export class Donar {
  protected readonly montos = [5000, 10000, 20000];
  protected readonly monto = signal(10000);
  protected readonly agradecido = signal(false);

  protected formato(valor: number): string {
    return '$' + valor.toLocaleString('es-CO') + ' COP';
  }
}
