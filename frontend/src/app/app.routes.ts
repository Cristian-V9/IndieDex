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
import { GestionJuegos } from './pages/equipo/gestion-juegos/gestion-juegos';
import { JuegoForm } from './pages/equipo/juego-form/juego-form';
import { ArticuloForm } from './pages/equipo/articulo-form/articulo-form';
import { ContenidoForm } from './pages/equipo/contenido-form/contenido-form';
import { Info } from './pages/info/info';
import { Donar } from './pages/donar/donar';

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
  { path: 'donar', component: Donar, title: 'Apoya IndieDex · IndieDex' },

  // ---------- Páginas del pie (el texto está en core/textos.ts) ----------
  { path: 'sobre', component: Info, data: { clave: 'sobre' } },
  { path: 'normas', component: Info, data: { clave: 'normas' } },
  { path: 'contacto', component: Info, data: { clave: 'contacto' } },
  { path: 'privacidad', component: Info, data: { clave: 'privacidad' } },
  { path: 'terminos', component: Info, data: { clave: 'terminos' } },
  { path: 'cookies', component: Info, data: { clave: 'cookies' } },

  // ---------- Gestión del contenido: solo EDITOR / ADMIN ----------
  { path: 'equipo', redirectTo: 'equipo/juegos', pathMatch: 'full' },
  { path: 'equipo/juegos', component: GestionJuegos, canActivate: [equipoGuard], title: 'Juegos y contenido · IndieDex' },
  { path: 'equipo/juegos/nuevo', component: JuegoForm, canActivate: [equipoGuard], title: 'Nuevo juego · IndieDex' },
  { path: 'equipo/juegos/:slug/editar', component: JuegoForm, canActivate: [equipoGuard], title: 'Editar juego · IndieDex' },
  { path: 'equipo/articulos/nuevo', component: ArticuloForm, canActivate: [equipoGuard], title: 'Nueva guía · IndieDex' },
  { path: 'equipo/articulos/:id/editar', component: ArticuloForm, canActivate: [equipoGuard], title: 'Editar guía · IndieDex' },
  // tipo = logros | objetos | personajes
  { path: 'equipo/:tipo/nuevo', component: ContenidoForm, canActivate: [equipoGuard], title: 'Nuevo contenido · IndieDex' },
  { path: 'equipo/:tipo/:id/editar', component: ContenidoForm, canActivate: [equipoGuard], title: 'Editar contenido · IndieDex' },

  { path: '**', redirectTo: '' },
];
