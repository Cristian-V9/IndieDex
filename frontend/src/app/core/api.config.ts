import { environment } from '../../environments/environment';

/**
 * Dirección del backend Spring Boot.
 * - En desarrollo (npm start): http://localhost:8080/api  -> environment.development.ts
 * - Desplegado (ng build):     /api (mismo servidor)      -> environment.ts
 */
export const API_URL = environment.apiUrl;
