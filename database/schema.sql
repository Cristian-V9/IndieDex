-- =====================================================================
--  IndieDex - Esquema de la base de datos (MySQL 8)
--
--  No es obligatorio ejecutarlo: Spring Boot (ddl-auto=update) crea las
--  tablas solo al arrancar. Este script sirve para documentar el modelo
--  y para crear la base a mano si se prefiere.
--
--  Uso:  mysql -u root -p < database/schema.sql
-- =====================================================================

CREATE DATABASE IF NOT EXISTS indiedex_db
  CHARACTER SET utf8mb4
  COLLATE utf8mb4_unicode_ci;

USE indiedex_db;

-- ---------------------------------------------------------------------
-- Usuarios de la comunidad y del equipo
-- ---------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS usuarios (
  id              BIGINT       NOT NULL AUTO_INCREMENT,
  nombre_usuario  VARCHAR(30)  NOT NULL,
  email           VARCHAR(120) NOT NULL,
  password        VARCHAR(100) NOT NULL COMMENT 'Hash BCrypt, nunca texto plano',
  rol             ENUM('USER','EDITOR','ADMIN') NOT NULL DEFAULT 'USER',
  biografia       VARCHAR(280),
  fecha_registro  DATETIME(6)  NOT NULL,
  PRIMARY KEY (id),
  UNIQUE KEY uk_usuarios_nombre (nombre_usuario),
  UNIQUE KEY uk_usuarios_email (email)
) ENGINE=InnoDB;

-- ---------------------------------------------------------------------
-- Juegos del catálogo (cada uno es una wiki)
-- ---------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS juegos (
  id                BIGINT       NOT NULL AUTO_INCREMENT,
  titulo            VARCHAR(120) NOT NULL,
  slug              VARCHAR(120) NOT NULL COMMENT 'Nombre en la URL: /juegos/dredge',
  eslogan           VARCHAR(255),
  descripcion       TEXT,
  imagen_portada    VARCHAR(255) COMMENT 'Ruta dentro del frontend, ej: images/dredge.jpg',
  imagen_banner     VARCHAR(255),
  desarrollador     VARCHAR(120),
  editor            VARCHAR(120),
  fecha_lanzamiento DATE,
  generos           VARCHAR(150),
  modo_juego        VARCHAR(80),
  plataformas       VARCHAR(150),
  estado_wiki       ENUM('ABIERTA','EN_PREPARACION') NOT NULL DEFAULT 'EN_PREPARACION',
  fecha_creacion    DATETIME(6)  NOT NULL,
  PRIMARY KEY (id),
  UNIQUE KEY uk_juegos_slug (slug)
) ENGINE=InnoDB;

-- ---------------------------------------------------------------------
-- Artículos / guías de cada juego
-- ---------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS articulos (
  id                  BIGINT       NOT NULL AUTO_INCREMENT,
  titulo              VARCHAR(150) NOT NULL,
  resumen             VARCHAR(300),
  contenido           TEXT         NOT NULL COMMENT 'HTML del artículo',
  categoria           VARCHAR(50),
  imagen              VARCHAR(255),
  imagen_descripcion  VARCHAR(255),
  minutos_lectura     INT,
  estado              ENUM('BORRADOR','EN_REVISION','PUBLICADO') NOT NULL DEFAULT 'BORRADOR',
  juego_id            BIGINT       NOT NULL,
  autor_id            BIGINT       NOT NULL,
  fecha_creacion      DATETIME(6)  NOT NULL,
  fecha_actualizacion DATETIME(6)  NOT NULL,
  PRIMARY KEY (id),
  KEY idx_articulos_juego_estado (juego_id, estado),
  CONSTRAINT fk_articulos_juego FOREIGN KEY (juego_id) REFERENCES juegos (id),
  CONSTRAINT fk_articulos_autor FOREIGN KEY (autor_id) REFERENCES usuarios (id)
) ENGINE=InnoDB;

-- ---------------------------------------------------------------------
-- Pestañas de la wiki: logros, objetos y personajes
-- ---------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS logros (
  id               BIGINT       NOT NULL AUTO_INCREMENT,
  nombre           VARCHAR(120) NOT NULL,
  descripcion      TEXT,
  como_conseguirlo TEXT,
  icono            VARCHAR(255),
  juego_id         BIGINT       NOT NULL,
  PRIMARY KEY (id),
  CONSTRAINT fk_logros_juego FOREIGN KEY (juego_id) REFERENCES juegos (id)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS objetos (
  id          BIGINT       NOT NULL AUTO_INCREMENT,
  nombre      VARCHAR(120) NOT NULL,
  descripcion TEXT,
  tipo        VARCHAR(50),
  rareza      VARCHAR(30),
  ubicacion   VARCHAR(150),
  imagen      VARCHAR(255),
  juego_id    BIGINT       NOT NULL,
  PRIMARY KEY (id),
  CONSTRAINT fk_objetos_juego FOREIGN KEY (juego_id) REFERENCES juegos (id)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS personajes (
  id        BIGINT       NOT NULL AUTO_INCREMENT,
  nombre    VARCHAR(120) NOT NULL,
  historia  TEXT,
  rol       VARCHAR(60),
  ubicacion VARCHAR(150),
  imagen    VARCHAR(255),
  juego_id  BIGINT       NOT NULL,
  PRIMARY KEY (id),
  CONSTRAINT fk_personajes_juego FOREIGN KEY (juego_id) REFERENCES juegos (id)
) ENGINE=InnoDB;

-- ---------------------------------------------------------------------
-- Comentarios en artículos (padre_id = respuesta a otro comentario)
-- ---------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS comentarios (
  id             BIGINT      NOT NULL AUTO_INCREMENT,
  contenido      TEXT        NOT NULL,
  autor_id       BIGINT      NOT NULL,
  articulo_id    BIGINT      NOT NULL,
  padre_id       BIGINT,
  fecha_creacion DATETIME(6) NOT NULL,
  PRIMARY KEY (id),
  CONSTRAINT fk_comentarios_autor    FOREIGN KEY (autor_id)    REFERENCES usuarios (id),
  CONSTRAINT fk_comentarios_articulo FOREIGN KEY (articulo_id) REFERENCES articulos (id),
  CONSTRAINT fk_comentarios_padre    FOREIGN KEY (padre_id)    REFERENCES comentarios (id)
) ENGINE=InnoDB;

-- ---------------------------------------------------------------------
-- Sugerencias de la comunidad
-- Flujo: SUGERENCIA -> VOTOS -> REVISION -> PUBLICADO  (o RECHAZADA)
-- ---------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS sugerencias (
  id             BIGINT       NOT NULL AUTO_INCREMENT,
  titulo         VARCHAR(150) NOT NULL,
  descripcion    TEXT         NOT NULL,
  tipo           ENUM('NUEVA_WIKI','NUEVA_GUIA','MEJORA','CORRECCION') NOT NULL,
  estado         ENUM('SUGERENCIA','VOTOS','REVISION','PUBLICADO','RECHAZADA') NOT NULL DEFAULT 'SUGERENCIA',
  juego_id       BIGINT       COMMENT 'Opcional: una nueva wiki puede no tener juego aún',
  autor_id       BIGINT       NOT NULL,
  total_votos    INT          NOT NULL DEFAULT 0 COMMENT 'Contador sincronizado con la tabla votos',
  nota_revision  VARCHAR(500),
  revisor_id     BIGINT,
  fecha_revision DATETIME(6),
  fecha_creacion DATETIME(6)  NOT NULL,
  PRIMARY KEY (id),
  KEY idx_sugerencias_estado (estado),
  CONSTRAINT fk_sugerencias_juego   FOREIGN KEY (juego_id)   REFERENCES juegos (id),
  CONSTRAINT fk_sugerencias_autor   FOREIGN KEY (autor_id)   REFERENCES usuarios (id),
  CONSTRAINT fk_sugerencias_revisor FOREIGN KEY (revisor_id) REFERENCES usuarios (id)
) ENGINE=InnoDB;

-- ---------------------------------------------------------------------
-- Votos: un usuario vota una sola vez cada sugerencia
-- ---------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS votos (
  id            BIGINT      NOT NULL AUTO_INCREMENT,
  usuario_id    BIGINT      NOT NULL,
  sugerencia_id BIGINT      NOT NULL,
  fecha         DATETIME(6) NOT NULL,
  PRIMARY KEY (id),
  UNIQUE KEY uk_voto_usuario_sugerencia (usuario_id, sugerencia_id),
  CONSTRAINT fk_votos_usuario    FOREIGN KEY (usuario_id)    REFERENCES usuarios (id),
  CONSTRAINT fk_votos_sugerencia FOREIGN KEY (sugerencia_id) REFERENCES sugerencias (id)
) ENGINE=InnoDB;
