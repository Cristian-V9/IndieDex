import { Component } from '@angular/core';
import { Anuncio } from '../../components/anuncio/anuncio';
import { BloqueDonacion } from '../../components/bloque-donacion/bloque-donacion';

@Component({
  selector: 'app-juego',
  imports: [Anuncio, BloqueDonacion],
  templateUrl: './juego.html',
  styleUrl: './juego.css',
})
export class Juego {}
