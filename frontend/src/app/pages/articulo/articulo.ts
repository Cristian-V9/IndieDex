import { Component } from '@angular/core';
import { Anuncio } from '../../components/anuncio/anuncio';
import { BloqueDonacion } from '../../components/bloque-donacion/bloque-donacion';

@Component({
  selector: 'app-articulo',
  imports: [Anuncio, BloqueDonacion],
  templateUrl: './articulo.html',
  styleUrl: './articulo.css',
})
export class Articulo {}
