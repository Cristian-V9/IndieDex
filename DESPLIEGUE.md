# Desplegar IndieDex

IndieDex se publica como **un solo servicio**: la imagen Docker compila Angular, lo mete dentro de Spring Boot
y la página y la API quedan en la misma dirección (`https://tu-app.onrender.com` y `https://tu-app.onrender.com/api`).

| Pieza | Servicio | Plan |
|---|---|---|
| Aplicación (Spring Boot + Angular) | [Render](https://render.com) | Free: 512 MB, se duerme tras 15 min sin visitas |
| Base de datos (compatible con MySQL) | [TiDB Cloud Starter](https://tidbcloud.com) | Gratis, sin tarjeta |

> Planes revisados en noviembre de 2026. Aiven ya no ofrece MySQL gratis, por eso se usa TiDB Cloud.

---

## Antes de empezar

- El código debe estar en GitHub (rama `main`).
- Render necesita acceso al repositorio. Lo más simple es que lo despliegue **la cuenta dueña del repositorio**
  (Cristian-V9). Si el repositorio es público, otra cuenta también puede crearlo pegando la URL pública.

## 1. Crear la base de datos en TiDB Cloud

1. Entra a <https://tidbcloud.com> y crea una cuenta (sirve con Google o GitHub).
2. Crea un clúster **Starter** (gratis). Región: **AWS · N. Virginia (us-east-1)**, la más cercana a Render Virginia.
3. En el clúster pulsa **Connect**:
   - *Connect With*: **General** o **Java**.
   - Pulsa **Generate Password** y guárdala (solo se muestra una vez).
   - Copia el **HOST** (algo como `gateway01.us-east-1.prod.aws.tidbcloud.com`), el **PORT** (`4000`)
     y el **USERNAME** (algo como `2abc3XyZ.root`).
4. Arma la dirección `DB_URL` reemplazando `HOST`:

```
jdbc:mysql://HOST:4000/indiedex_db?sslMode=VERIFY_IDENTITY&enabledTLSProtocols=TLSv1.2,TLSv1.3&createDatabaseIfNotExist=true&serverTimezone=America/Bogota&characterEncoding=UTF-8
```

No hace falta crear tablas: Spring las crea al arrancar, junto con los datos de ejemplo.

## 2. Crear el servicio en Render

1. Entra a <https://render.com> y crea una cuenta **con GitHub**.
2. **New → Blueprint** y elige el repositorio `IndieDex`. Render lee el archivo `render.yaml`.
3. Te pedirá tres valores:

   | Variable | Valor |
   |---|---|
   | `DB_URL` | La dirección del paso 1.4 |
   | `DB_USER` | El USERNAME de TiDB (`xxxx.root`) |
   | `DB_PASSWORD` | La contraseña generada en TiDB |

   `JWT_SECRET` (la clave que firma los tokens) la genera Render sola.
4. Pulsa **Apply**. La primera construcción tarda unos 10 minutos (descarga Node, Maven y compila todo).
5. Cuando el estado diga **Live**, la dirección aparece arriba: `https://indiedex-xxxx.onrender.com`.

> Sin Blueprint también funciona: **New → Web Service**, elegir el repositorio, *Language: Docker*,
> *Instance Type: Free*, y agregar las mismas variables en *Environment*.

## 3. Comprobar que funciona

- `https://…onrender.com` → la página de inicio con Dredge y las cifras.
- `https://…onrender.com/swagger-ui.html` → la documentación de la API.
- Iniciar sesión con `admin` / `indiedex123` y probar Gestión, Revisión, votos y comentarios.
- **JWT:** en Postman o Swagger, `POST /api/auth/login` devuelve el token; pegarlo en *Authorize*
  (Swagger) o en la variable de la colección permite usar las rutas protegidas.
- Postman: importar la colección y cambiar la variable `baseUrl` por la dirección de Render.

## 4. Antes de la exposición

- **El plan gratis se duerme** tras 15 minutos sin visitas y tarda cerca de un minuto en despertar.
  Abre la página **2 o 3 minutos antes** de presentar o grabar el video.
- Las cuentas de prueba (`indiedex123`) son públicas en el README: está bien para la clase, pero
  no uses esas contraseñas en ningún otro lado.
- Cada `git push` a `main` vuelve a desplegar automáticamente.

## Problemas comunes

| Síntoma en los *Logs* de Render | Causa probable |
|---|---|
| `Communications link failure` / `Access denied` | `DB_URL`, `DB_USER` o `DB_PASSWORD` mal copiados |
| `Connections using insecure transport are prohibited` | Falta `sslMode=VERIFY_IDENTITY` en `DB_URL` |
| `OutOfMemoryError` o el servicio se reinicia | Memoria del plan gratis; revisar que `JAVA_OPTS` del Dockerfile no se haya cambiado |
| La construcción falla en `mvn` o `npm` | Ver el error exacto en *Logs → Build*; suele ser una dependencia o un error de compilación |

Si TiDB diera algún problema de compatibilidad, la alternativa es **Railway** (prueba con crédito de USD 5,
trae MySQL real): crear un proyecto con MySQL y con este repositorio, y usar sus variables de conexión
en `DB_URL`, `DB_USER` y `DB_PASSWORD`.

## Desarrollo local (sin cambios)

Nada de esto afecta al trabajo en tu computador: `mvn spring-boot:run` en `backend/` y `npm start` en
`frontend/` siguen funcionando igual, contra el MySQL local.
