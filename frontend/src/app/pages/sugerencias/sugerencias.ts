import { Component } from '@angular/core';
import { Anuncio } from '../../components/anuncio/anuncio';
import { BloqueDonacion } from '../../components/bloque-donacion/bloque-donacion';

@Component({
  selector: 'app-sugerencias',
  imports: [Anuncio, BloqueDonacion],
  templateUrl: './sugerencias.html',
  styleUrl: './sugerencias.css',
})
export class Sugerencias {}
