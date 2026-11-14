/**
 * Configuración de PRODUCCIÓN (ng build).
 * La aplicación desplegada se sirve desde el mismo Spring Boot que la API,
 * así que basta con la ruta relativa /api (mismo dominio, sin CORS).
 */
export const environment = {
  production: true,
  apiUrl: '/api',
};
