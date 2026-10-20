import { Component } from '@angular/core';
import { Anuncio } from '../../components/anuncio/anuncio';
import { BloqueDonacion } from '../../components/bloque-donacion/bloque-donacion';

@Component({
  selector: 'app-perfil',
  imports: [Anuncio, BloqueDonacion],
  templateUrl: './perfil.html',
  styleUrl: './perfil.css',
})
export class Perfil {}
