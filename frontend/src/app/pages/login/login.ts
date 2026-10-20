import { Component } from '@angular/core';
import { Anuncio } from '../../components/anuncio/anuncio';
import { BloqueDonacion } from '../../components/bloque-donacion/bloque-donacion';

@Component({
  selector: 'app-login',
  imports: [Anuncio, BloqueDonacion],
  templateUrl: './login.html',
  styleUrl: './login.css',
})
export class Login {}
