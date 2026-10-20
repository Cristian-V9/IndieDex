import { Component } from '@angular/core';
import { Anuncio } from '../../components/anuncio/anuncio';
import { BloqueDonacion } from '../../components/bloque-donacion/bloque-donacion';

@Component({
  selector: 'app-registro',
  imports: [Anuncio, BloqueDonacion],
  templateUrl: './registro.html',
  styleUrl: './registro.css',
})
export class Registro {}
