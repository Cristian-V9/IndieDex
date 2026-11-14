import { Component, inject, input, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { VentanaTexto } from '../../components/ventana-texto/ventana-texto';
import { erroresDeCampos, mensajeError } from '../../core/errores';
import { AuthService } from '../../core/servicios/auth.service';

@Component({
  selector: 'app-registro',
  imports: [RouterLink, FormsModule, VentanaTexto],
  templateUrl: './registro.html',
  styleUrl: './registro.css',
})
export class Registro {
  private readonly auth = inject(AuthService);
  private readonly router = inject(Router);

  readonly volver = input<string | undefined>();

  protected datos = { nombreUsuario: '', email: '', password: '', confirmar: '', acepto: false };
  protected readonly verClave = signal(false);
  protected readonly enviando = signal(false);
  protected readonly error = signal<string | null>(null);
  protected readonly errores = signal<Record<string, string>>({});

  protected crear(): void {
    const locales = this.validar();
    this.errores.set(locales);
    this.error.set(null);
    if (Object.keys(locales).length) return;

    this.enviando.set(true);
    const { nombreUsuario, email, password } = this.datos;
    this.auth.registrar(nombreUsuario.trim(), email.trim(), password).subscribe({
      next: () => {
        const destino = this.volver();
        this.router.navigateByUrl(destino && destino.startsWith('/') && !destino.startsWith('//') ? destino : '/perfil');
      },
      error: e => {
        // Errores del backend: campo inválido (400) o usuario/correo ya registrado (409)
        this.errores.set(erroresDeCampos(e));
        this.error.set(mensajeError(e));
        this.enviando.set(false);
      },
    });
  }

  /** Validación rápida en el navegador; el backend vuelve a validar todo. */
  private validar(): Record<string, string> {
    const e: Record<string, string> = {};
    const nombre = this.datos.nombreUsuario.trim();
    if (nombre.length < 3 || nombre.length > 24) e['nombreUsuario'] = 'Entre 3 y 24 caracteres.';
    else if (!/^[A-Za-z0-9_.-]+$/.test(nombre)) e['nombreUsuario'] = 'Solo letras, números, punto, guion y guion bajo.';
    if (!/^\S+@\S+\.\S+$/.test(this.datos.email.trim())) e['email'] = 'Escribe un correo válido.';
    if (this.datos.password.length < 8) e['password'] = 'Al menos 8 caracteres.';
    if (this.datos.confirmar !== this.datos.password) e['confirmar'] = 'Las contraseñas no coinciden.';
    if (!this.datos.acepto) e['acepto'] = 'Debes aceptar los términos para crear la cuenta.';
    return e;
  }
}
