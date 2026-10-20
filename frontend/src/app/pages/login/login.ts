import { Component, inject, input, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { mensajeError } from '../../core/errores';
import { AuthService } from '../../core/servicios/auth.service';

@Component({
  selector: 'app-login',
  imports: [RouterLink, FormsModule],
  templateUrl: './login.html',
  styleUrl: './login.css',
})
export class Login {
  private readonly auth = inject(AuthService);
  private readonly router = inject(Router);

  /** ?volver=/ruta: a dónde ir después de entrar. */
  readonly volver = input<string | undefined>();

  protected usuario = '';
  protected password = '';
  protected recordar = false;
  protected readonly verClave = signal(false);
  protected readonly enviando = signal(false);
  protected readonly error = signal<string | null>(null);

  protected entrar(): void {
    if (!this.usuario.trim() || !this.password) {
      this.error.set('Escribe tu correo o usuario y tu contraseña.');
      return;
    }
    this.enviando.set(true);
    this.error.set(null);
    this.auth.login(this.usuario, this.password, this.recordar).subscribe({
      next: () => this.router.navigateByUrl(this.destinoSeguro()),
      error: e => {
        this.error.set(mensajeError(e));
        this.enviando.set(false);
      },
    });
  }

  /** Solo rutas internas (evita redirecciones a otros sitios). */
  private destinoSeguro(): string {
    const destino = this.volver();
    return destino && destino.startsWith('/') && !destino.startsWith('//') ? destino : '/perfil';
  }
}
