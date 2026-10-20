import { Routes } from '@angular/router';
import { equipoGuard, sesionGuard } from './core/guards';
import { Inicio } from './pages/inicio/inicio';
import { Catalogo } from './pages/catalogo/catalogo';
import { Juego } from './pages/juego/juego';
import { Articulo } from './pages/articulo/articulo';
import { Sugerencias } from './pages/sugerencias/sugerencias';
import { Login } from './pages/login/login';
import { Registro } from './pages/registro/registro';
import { Perfil } from './pages/perfil/perfil';
import { PanelRevision } from './pages/panel-revision/panel-revision';

export const routes: Routes = [
  { path: '', component: Inicio, title: 'IndieDex · Wiki de juegos indie' },
  { path: 'juegos', component: Catalogo, title: 'Catálogo · IndieDex' },
  { path: 'juegos/:slug', component: Juego, title: 'Juego · IndieDex' },
  { path: 'articulo/:id', component: Articulo, title: 'Artículo · IndieDex' },
  { path: 'sugerencias', component: Sugerencias, title: 'Sugerencias · IndieDex' },
  { path: 'login', component: Login, title: 'Iniciar sesión · IndieDex' },
  { path: 'registro', component: Registro, title: 'Crear cuenta · IndieDex' },
  { path: 'perfil', component: Perfil, canActivate: [sesionGuard], title: 'Mi perfil · IndieDex' },
  { path: 'revision', component: PanelRevision, canActivate: [equipoGuard], title: 'Panel de revisión · IndieDex' },
  { path: '**', redirectTo: '' },
];
