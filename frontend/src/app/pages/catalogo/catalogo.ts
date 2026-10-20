import { Component } from '@angular/core';
import { Anuncio } from '../../components/anuncio/anuncio';
import { BloqueDonacion } from '../../components/bloque-donacion/bloque-donacion';

@Component({
  selector: 'app-catalogo',
  imports: [Anuncio, BloqueDonacion],
  templateUrl: './catalogo.html',
  styleUrl: './catalogo.css',
})
export class Catalogo {}
