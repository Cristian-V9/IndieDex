package com.indiedex.config;

import com.indiedex.model.*;
import com.indiedex.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Carga datos de ejemplo la primera vez que arranca la aplicación (si no hay usuarios).
 * Se desactiva con indiedex.datos-ejemplo=false.
 *
 * Todas las cuentas de prueba usan la contraseña: indiedex123
 *   admin          -> ADMIN
 *   FaroNorte      -> EDITOR
 *   MarineroLunar  -> EDITOR
 *   LunaVerde, NieblaBaja, SemillaAzul, Pescadora88 -> USER
 */
@Slf4j
@Component
@RequiredArgsConstructor
@ConditionalOnProperty(name = "indiedex.datos-ejemplo", havingValue = "true", matchIfMissing = true)
public class DatosEjemplo implements CommandLineRunner {

    public static final String CLAVE_PRUEBA = "indiedex123";

    private final UsuarioRepository usuarioRepository;
    private final JuegoRepository juegoRepository;
    private final ArticuloRepository articuloRepository;
    private final LogroRepository logroRepository;
    private final ObjetoRepository objetoRepository;
    private final PersonajeRepository personajeRepository;
    private final ComentarioRepository comentarioRepository;
    private final SugerenciaRepository sugerenciaRepository;
    private final VotoRepository votoRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void run(String... args) {
        if (usuarioRepository.count() > 0) {
            return; // La base ya tiene datos
        }
        log.info("Base de datos vacía: cargando datos de ejemplo de IndieDex...");

        // ============================ USUARIOS ============================
        Usuario admin = usuario("admin", "admin@indiedex.com", Rol.ADMIN,
                "Cuidando el faro de IndieDex.", "2026-09-01T09:00");
        Usuario faroNorte = usuario("FaroNorte", "faronorte@indiedex.com", Rol.EDITOR,
                "Editor del equipo. Reviso guías y propuestas con café en mano.", "2026-09-02T10:00");
        Usuario marinero = usuario("MarineroLunar", "marinero@indiedex.com", Rol.EDITOR,
                "Pescando historias y compartiendo rutas. Dredge me trajo; la comunidad hizo que me quedara.",
                "2026-09-10T20:30");
        Usuario lunaVerde = usuario("LunaVerde", "lunaverde@correo.com", Rol.USER,
                "Exploradora de metroidvanias.", "2026-09-15T18:00");
        Usuario nieblaBaja = usuario("NieblaBaja", "nieblabaja@correo.com", Rol.USER,
                "Siempre vuelvo al puerto antes de las 18:00.", "2026-09-18T21:00");
        Usuario semillaAzul = usuario("SemillaAzul", "semillaazul@correo.com", Rol.USER,
                "Granjera a tiempo completo en Stardew Valley.", "2026-09-20T12:00");
        Usuario pescadora = usuario("Pescadora88", "pescadora88@correo.com", Rol.USER,
                null, "2026-09-25T16:45");

        // ============================ JUEGOS ============================
        Juego dredge = juegoRepository.save(Juego.builder()
                .titulo("Dredge").slug("dredge")
                .eslogan("Aguas tranquilas. Secretos profundos. Todo lo que necesitas para navegar este inquietante archipiélago.")
                .descripcion("Un juego de pesca y aventura con un toque de misterio. Recorre un archipiélago remoto, "
                        + "vende tus capturas y descubre qué se esconde bajo la superficie.")
                .imagenPortada("images/dredge.jpg").imagenBanner("images/Banner.jpg")
                .desarrollador("Black Salt Games").editor("Team17")
                .fechaLanzamiento(LocalDate.of(2023, 3, 30))
                .generos("Aventura, pesca, misterio").modoJuego("Un jugador")
                .plataformas("PC · PS4 / PS5 · Xbox · Switch")
                .estadoWiki(EstadoWiki.ABIERTA)
                .fechaCreacion(fecha("2026-09-01T09:30"))
                .build());

        Juego hollowKnight = juego("Hollow Knight", "hollow-knight", "Team Cherry", "images/hollow-knight.jpg",
                "Metroidvania, Aventura", LocalDate.of(2017, 2, 24), "Un jugador", "PC · PS4 · Xbox One · Switch",
                "Desciende a las profundidades de un reino de insectos en ruinas.");
        Juego stardew = juego("Stardew Valley", "stardew-valley", "ConcernedApe", "images/stardew-valley.jpg",
                "Simulación, Cooperativo", LocalDate.of(2016, 2, 26), "Un jugador · Cooperativo",
                "PC · PlayStation · Xbox · Switch · Móvil",
                "Hereda la vieja granja de tu abuelo y empieza una nueva vida en el valle.");
        Juego celeste = juego("Celeste", "celeste", "Maddy Makes Games", "images/celeste.jpg",
                "Plataformas, Aventura", LocalDate.of(2018, 1, 25), "Un jugador", "PC · PS4 · Xbox One · Switch",
                "Ayuda a Madeline a sobrevivir a sus demonios internos en su camino a la cima de la montaña.");
        juego("Hades", "hades", "Supergiant Games", "images/hades.jpg",
                "Roguelike, Acción", LocalDate.of(2020, 9, 17), "Un jugador", "PC · PlayStation · Xbox · Switch",
                "Desafía al dios de los muertos y abre tu camino fuera del Inframundo.");
        juego("Dead Cells", "dead-cells", "Motion Twin", "images/dead-cells.jpg",
                "Roguelike, Metroidvania", LocalDate.of(2018, 8, 7), "Un jugador",
                "PC · PlayStation · Xbox · Switch · Móvil",
                "Explora un castillo en constante cambio, muere, aprende y vuelve a intentarlo.");

        // ============================ ARTÍCULOS (Dredge) ============================
        Articulo primeraNoche = articulo(dredge, marinero, "Tu primera noche en el mar",
                "Aprende a pescar, cuidar tu barco y volver a casa antes de que la oscuridad cambie las reglas.",
                "Principiantes", 8, "images/articulo-primera-noche.jpg",
                "El puerto de Médula Mayor será tu refugio durante las primeras jornadas.",
                CONTENIDO_PRIMERA_NOCHE, EstadoArticulo.PUBLICADO, "2026-10-01T10:00", "2026-10-05T12:00");

        Articulo mejoraBarco = articulo(dredge, marinero, "Mejora tu barco paso a paso",
                "Motores, cañas y espacio: qué conviene comprar primero.",
                "Progresión", 7, "images/Banner.jpg",
                "Cada mejora del astillero amplía lo que puedes hacer en una jornada.",
                CONTENIDO_MEJORA_BARCO, EstadoArticulo.PUBLICADO, "2026-10-02T10:00", "2026-10-05T12:10");

        articulo(dredge, faroNorte, "Las cinco reliquias del coleccionista",
                "Recorre el archipiélago y sigue el hilo de la historia.",
                "Historia", 6, "images/articulo-reliquias.jpg",
                "Un barco navegando entre la niebla en busca de reliquias.",
                CONTENIDO_RELIQUIAS, EstadoArticulo.PUBLICADO, "2026-10-03T10:00", "2026-10-05T12:20");

        articulo(dredge, marinero, "Pesca de aberraciones",
                "Horarios, zonas y equipo para tus capturas más extrañas.",
                "Pesca", 10, "images/articulo-pesca.jpg",
                "Un pequeño barco pesquero junto a la costa, frente a un faro entre la niebla.",
                CONTENIDO_ABERRACIONES, EstadoArticulo.PUBLICADO, "2026-10-04T10:00", "2026-10-05T12:30");

        articulo(dredge, faroNorte, "Conoce a la mercader ambulante",
                "Repara tu barco, vende tu pesca y descubre sus puestos en las islas.",
                "Personajes", 4, "images/articulo-mercader.jpg",
                "Un muelle con una linterna encendida.",
                CONTENIDO_MERCADER, EstadoArticulo.PUBLICADO, "2026-10-04T11:00", "2026-10-05T12:40");

        // Borrador: solo lo ve el equipo (EDITOR/ADMIN)
        articulo(dredge, faroNorte, "Regiones del archipiélago (borrador)",
                "Un recorrido por cada región y lo que puedes encontrar en ella.",
                "Exploración", 9, "images/hero-faro.jpg", "Un faro iluminado sobre una isla rocosa.",
                "<p class=\"texto__intro\">Borrador en preparación. El equipo está reuniendo la información de cada región.</p>",
                EstadoArticulo.BORRADOR, "2026-10-08T09:00", "2026-10-08T09:00");

        // ============================ LOGROS / OBJETOS / PERSONAJES ============================
        logro(dredge, "Primera captura", "Pesca tu primer pez en el archipiélago.",
                "Completa el minijuego de pesca en cualquier banco cercano a Médula Mayor.");
        logro(dredge, "De vuelta a casa", "Regresa al puerto antes de que caiga la noche.",
                "Atraca en Médula Mayor antes de las 18:00 en tu primera jornada.");
        logro(dredge, "Barco a punto", "Compra tu primera mejora en el astillero.",
                "Reúne los materiales y el dinero necesarios y visita el astillero.");
        logro(dredge, "Algo no va bien", "Pesca tu primera aberración.",
                "Las aberraciones aparecen en bancos de peces alterados. Consulta la guía de aberraciones.");
        logro(dredge, "Cartógrafo", "Visita todas las regiones del archipiélago.",
                "Explora el mapa completo. Mejora el motor para llegar más lejos.");
        logro(dredge, "Coleccionista", "Entrega todas las reliquias.",
                "Contiene destripes de la historia principal: consulta la guía de reliquias.");

        objeto(dredge, "Caña costera", "Caña inicial para peces de aguas poco profundas.", "Caña", "Común",
                "Equipo inicial");
        objeto(dredge, "Motor básico", "El motor con el que empiezas. Suficiente para las primeras jornadas.",
                "Motor", "Común", "Equipo inicial");
        objeto(dredge, "Lámpara de cubierta", "Ilumina el camino de noche. No sustituye una ruta segura.",
                "Luz", "Poco común", "Astillero de Médula Mayor");
        objeto(dredge, "Red de arrastre", "Captura peces de forma pasiva mientras navegas.", "Red", "Poco común",
                "Astillero");
        objeto(dredge, "Equipo de dragado", "Permite recuperar materiales y restos del fondo.", "Draga", "Común",
                "Astillero");
        objeto(dredge, "Reliquia antigua", "Objeto misterioso que interesa mucho al Coleccionista.", "Reliquia",
                "Única", "Repartidas por el archipiélago");

        personaje(dredge, "El alcalde", "Te recibe al llegar y te ofrece trabajo como pescador del pueblo.",
                "Autoridad del pueblo", "Médula Mayor");
        personaje(dredge, "El Coleccionista", "Un personaje enigmático que te encarga recuperar reliquias.",
                "Misterioso", "Médula Mayor");
        personaje(dredge, "La mercader ambulante", "Aparece en distintos muelles con objetos poco comunes.",
                "Comerciante", "Varios muelles del archipiélago");
        personaje(dredge, "El pescadero", "Compra tus capturas. Vende antes de que pierdan frescura.",
                "Comerciante", "Médula Mayor");
        personaje(dredge, "El astillero", "Repara el barco y vende las mejoras de equipo.",
                "Artesano", "Médula Mayor");

        // ============================ COMENTARIOS ============================
        comentario(primeraNoche, lunaVerde, null,
                "¡Esta guía me salvó la primera partida! Me quedaba pescando hasta demasiado tarde. "
                        + "Volver con margen cambia todo.", "2026-10-06T18:42");
        Comentario consejoNiebla = comentario(primeraNoche, nieblaBaja, null,
                "Otro consejo: vende la pesca antes de descansar para que no pierda frescura. "
                        + "Es fácil olvidarse al llegar al puerto.", "2026-10-05T21:15");
        comentario(primeraNoche, marinero, consejoNiebla,
                "Buen apunte, NieblaBaja. Lo añadimos al apartado del regreso. Gracias por ayudar a mejorar la guía.",
                "2026-10-05T22:03");
        comentario(mejoraBarco, marinero, null,
                "Yo prioricé el motor antes que las luces. Tener margen para volver al puerto me ayudó mucho.",
                "2026-10-03T16:20");
        comentario(mejoraBarco, pescadora, null,
                "¿Vale la pena la red de arrastre al principio? Ocupa bastante espacio.", "2026-10-07T09:12");

        // ============================ SUGERENCIAS Y VOTOS ============================
        // El total de votos coincide con las filas de la tabla votos.
        sugerencia("Abrir la wiki de Hollow Knight",
                "Un espacio para mapas, amuletos, jefes y rutas del reino de Hallownest.",
                TipoSugerencia.NUEVA_WIKI, EstadoSugerencia.VOTOS, hollowKnight, lunaVerde, "2026-10-04T10:15",
                null, null, null,
                List.of(lunaVerde, nieblaBaja, semillaAzul, pescadora, marinero, faroNorte));

        sugerencia("Ampliar la guía de aberraciones con todas las especies",
                "Reunir todas las especies alteradas de Dredge con sus zonas y condiciones.",
                TipoSugerencia.MEJORA, EstadoSugerencia.REVISION, dredge, marinero, "2026-10-03T19:40",
                faroNorte, "2026-10-08T11:00", "Aprobada para revisión: estamos verificando zonas y horarios.",
                List.of(lunaVerde, nieblaBaja, semillaAzul, pescadora, faroNorte));

        sugerencia("Abrir la wiki de Stardew Valley",
                "Cultivos, amistades y consejos para jugar en cooperativo, todo en español.",
                TipoSugerencia.NUEVA_WIKI, EstadoSugerencia.SUGERENCIA, stardew, semillaAzul, "2026-10-02T08:30",
                null, null, null,
                List.of(semillaAzul, lunaVerde, nieblaBaja, pescadora));

        sugerencia("Mapa de muelles seguros",
                "Una referencia rápida para descansar y reparar el barco en cada región.",
                TipoSugerencia.MEJORA, EstadoSugerencia.SUGERENCIA, dredge, nieblaBaja, "2026-10-01T22:10",
                null, null, null,
                List.of(nieblaBaja, marinero, lunaVerde));

        sugerencia("Tabla de motores y consumo de espacio",
                "Comparar cada motor según velocidad y casillas que ocupa en la bodega.",
                TipoSugerencia.NUEVA_GUIA, EstadoSugerencia.SUGERENCIA, dredge, marinero, "2026-10-01T15:00",
                null, null, null,
                List.of(pescadora, semillaAzul));

        sugerencia("Guía de las primeras mejoras del barco",
                "Un orden recomendado de mejoras y los materiales necesarios.",
                TipoSugerencia.NUEVA_GUIA, EstadoSugerencia.PUBLICADO, dredge, faroNorte, "2026-09-30T17:20",
                marinero, "2026-10-05T12:10", "¡Publicada! Ya puedes consultarla en la wiki de Dredge.",
                List.of(lunaVerde, nieblaBaja, semillaAzul, pescadora, marinero));

        sugerencia("Corregir la ubicación de un muelle en el mapa",
                "Uno de los muelles del mapa de la guía aparece en la isla equivocada.",
                TipoSugerencia.CORRECCION, EstadoSugerencia.PUBLICADO, dredge, marinero, "2026-09-28T13:05",
                faroNorte, "2026-09-29T10:00", "Corregido. ¡Gracias por avisar!",
                List.of(nieblaBaja));

        sugerencia("Guía de fresas de Celeste",
                "Dónde están todas las fresas de cada capítulo y cómo conseguir las más difíciles.",
                TipoSugerencia.NUEVA_GUIA, EstadoSugerencia.SUGERENCIA, celeste, lunaVerde, "2026-10-07T20:00",
                null, null, null,
                List.of(lunaVerde));

        sugerencia("Abrir una wiki de Elden Ring",
                "Me encantaría tener una guía en español de Elden Ring.",
                TipoSugerencia.NUEVA_WIKI, EstadoSugerencia.RECHAZADA, null, pescadora, "2026-10-06T14:00",
                faroNorte, "2026-10-07T09:00",
                "IndieDex se centra en juegos independientes y Elden Ring no entra en el catálogo. "
                        + "¡Gracias por la idea! Te animamos a proponer un indie.",
                List.of());

        log.info("Datos de ejemplo cargados. Usuarios de prueba con contraseña '{}': admin, FaroNorte, "
                + "MarineroLunar, LunaVerde, NieblaBaja, SemillaAzul, Pescadora88", CLAVE_PRUEBA);
    }

    // ======================= Métodos de ayuda =======================

    private static LocalDateTime fecha(String isoLocal) {
        return LocalDateTime.parse(isoLocal);
    }

    private Usuario usuario(String nombre, String email, Rol rol, String bio, String registro) {
        return usuarioRepository.save(Usuario.builder()
                .nombreUsuario(nombre).email(email).rol(rol).biografia(bio)
                .password(passwordEncoder.encode(CLAVE_PRUEBA))
                .fechaRegistro(fecha(registro))
                .build());
    }

    private Juego juego(String titulo, String slug, String desarrollador, String portada, String generos,
                        LocalDate lanzamiento, String modo, String plataformas, String eslogan) {
        return juegoRepository.save(Juego.builder()
                .titulo(titulo).slug(slug).desarrollador(desarrollador).editor(desarrollador)
                .imagenPortada(portada).imagenBanner(portada)
                .generos(generos).fechaLanzamiento(lanzamiento).modoJuego(modo).plataformas(plataformas)
                .eslogan(eslogan).descripcion(eslogan)
                .estadoWiki(EstadoWiki.EN_PREPARACION)
                .fechaCreacion(fecha("2026-09-01T09:45"))
                .build());
    }

    private Articulo articulo(Juego juego, Usuario autor, String titulo, String resumen, String categoria,
                              int minutos, String imagen, String imagenDescripcion, String contenido,
                              EstadoArticulo estado, String creado, String actualizado) {
        return articuloRepository.save(Articulo.builder()
                .juego(juego).autor(autor).titulo(titulo).resumen(resumen).categoria(categoria)
                .minutosLectura(minutos).imagen(imagen).imagenDescripcion(imagenDescripcion)
                .contenido(contenido).estado(estado)
                .fechaCreacion(fecha(creado)).fechaActualizacion(fecha(actualizado))
                .build());
    }

    private void logro(Juego juego, String nombre, String descripcion, String comoConseguirlo) {
        logroRepository.save(Logro.builder().juego(juego).nombre(nombre).descripcion(descripcion)
                .comoConseguirlo(comoConseguirlo).build());
    }

    private void objeto(Juego juego, String nombre, String descripcion, String tipo, String rareza,
                        String ubicacion) {
        objetoRepository.save(Objeto.builder().juego(juego).nombre(nombre).descripcion(descripcion)
                .tipo(tipo).rareza(rareza).ubicacion(ubicacion).build());
    }

    private void personaje(Juego juego, String nombre, String historia, String rol, String ubicacion) {
        personajeRepository.save(Personaje.builder().juego(juego).nombre(nombre).historia(historia)
                .rol(rol).ubicacion(ubicacion).build());
    }

    private Comentario comentario(Articulo articulo, Usuario autor, Comentario padre, String texto, String cuando) {
        return comentarioRepository.save(Comentario.builder()
                .articulo(articulo).autor(autor).padre(padre).contenido(texto).fechaCreacion(fecha(cuando))
                .build());
    }

    private void sugerencia(String titulo, String descripcion, TipoSugerencia tipo, EstadoSugerencia estado,
                            Juego juego, Usuario autor, String creada, Usuario revisor, String revisada,
                            String nota, List<Usuario> votantes) {
        Sugerencia s = sugerenciaRepository.save(Sugerencia.builder()
                .titulo(titulo).descripcion(descripcion).tipo(tipo).estado(estado).juego(juego).autor(autor)
                .fechaCreacion(fecha(creada)).revisor(revisor)
                .fechaRevision(revisada == null ? null : fecha(revisada)).notaRevision(nota)
                .totalVotos(votantes.size())
                .build());
        LocalDateTime cuando = fecha(creada);
        for (Usuario votante : votantes) {
            cuando = cuando.plusHours(3);
            votoRepository.save(Voto.builder().usuario(votante).sugerencia(s).fecha(cuando).build());
        }
    }

    // ======================= Contenido de las guías (HTML) =======================

    /** Texto escrito por el equipo en la maqueta del frontend. */
    private static final String CONTENIDO_PRIMERA_NOCHE = """
            <p class="texto__intro">
              En Dredge, cada salida al mar es una pequeña decisión entre la curiosidad y la prudencia.
              Durante las primeras jornadas no necesitas ir lejos: aprende cómo avanza el tiempo,
              reconoce la costa y reserva siempre unas horas para volver. Esta guía te acompaña desde
              el primer muelle hasta tu primera mejora.
            </p>
            <h2>01 / Antes de zarpar</h2>
            <p>
              Al llegar a Médula Mayor, habla con el alcalde y revisa el equipo de tu barco. La caña inicial
              permite capturar peces costeros; no sirve para todos los puntos de pesca que verás en el mapa.
              Consulta el tipo de agua de cada especie antes de instalar una nueva caña.
            </p>
            <p>
              El tiempo avanza al navegar, pescar o dragar, pero puedes detenerte para consultar el mapa y
              organizar la bodega. Aprovecha esas pausas: colocar bien cada captura evita que un pez grande
              ocupe el espacio de varios pequeños.
            </p>
            <aside class="consejo">
              <p class="consejo__titulo">Consejo del puerto</p>
              <p>
                Fija el regreso para antes de las 18:00. Es una referencia prudente, no una obligación: la
                distancia al puerto y la velocidad del barco determinan tu margen real.
              </p>
            </aside>
            <h2>02 / Pesca cerca de casa</h2>
            <p>
              Busca los bancos de peces en las aguas que rodean las Médulas. Los círculos y las aves sobre la
              superficie te ayudan a localizarlos. Acércate, comprueba que tienes el equipo adecuado y completa
              el minijuego de pesca. Una captura bien ejecutada consume menos tiempo.
            </p>
            <p>
              No intentes llenar la bodega a cualquier precio. Deja espacio para capturas valiosas y vigila la
              hora después de cada banco. Los puntos agotados se recuperan con el tiempo: explorar otra zona
              suele ser mejor que esperar en el mismo lugar.
            </p>
            <figure class="figura figura--interna">
              <div class="figura__imagen">
                <img src="images/articulo-pesca.jpg"
                  alt="Un pequeño barco pesquero junto a la costa, frente a un faro entre la niebla">
              </div>
              <figcaption>Pesca junto a la costa mientras aprendes a reconocer los puntos de referencia.</figcaption>
            </figure>
            <h2>03 / El regreso al puerto</h2>
            <p>
              Usa el faro como referencia y evita navegar en línea recta hacia una costa que no conoces: puede
              haber rocas en el camino. Por la noche aumenta el peligro y la falta de descanso eleva el pánico.
              Si el trayecto se complica, busca un muelle cercano para descansar.
            </p>
            <p>
              Al atracar, vende la pesca al pescadero antes de dormir. Las capturas pierden frescura con el paso
              del tiempo. Después, visita el astillero para reparar cualquier daño: una casilla dañada de la
              bodega puede inutilizar el equipo instalado en ella.
            </p>
            <h2>04 / Tus primeras mejoras</h2>
            <p>
              Invierte según tu forma de jugar. Estas tres prioridades te permiten pescar más variedad, regresar
              con margen y aprovechar mejor cada jornada.
            </p>
            <dl class="mejoras">
              <div>
                <dt>Motor</dt>
                <dd>Más velocidad significa más tiempo para explorar y un regreso menos apurado.</dd>
              </div>
              <div>
                <dt>Caña versátil</dt>
                <dd>Amplía los tipos de agua en los que puedes pescar antes de buscar nuevas especies.</dd>
              </div>
              <div>
                <dt>Luz</dt>
                <dd>Mejora la visibilidad nocturna, pero no sustituye el descanso ni una ruta segura.</dd>
              </div>
            </dl>
            <p class="texto__fuentes">
              Fuentes: experiencia de la comunidad en Dredge, versión base; descripciones del equipo dentro del
              juego. Última revisión: 5 de octubre de 2026.
            </p>
            """;

    private static final String CONTENIDO_MEJORA_BARCO = """
            <p class="texto__intro">
              Tu barco es tu casa, tu herramienta y tu escudo. Cada mejora cambia lo que puedes hacer en una
              jornada, así que conviene elegir con calma qué comprar primero.
            </p>
            <h2>01 / El motor primero</h2>
            <p>
              Un motor más rápido te da tiempo: llegas antes a los bancos de peces y vuelves al puerto con
              margen. Si dudas entre varias mejoras, esta suele ser la que más se nota al principio.
            </p>
            <h2>02 / Cañas para cada tipo de agua</h2>
            <p>
              No todas las cañas sirven para todas las especies. Revisa el tipo de agua que indica cada pez y
              combina cañas para no dejar bancos sin aprovechar.
            </p>
            <h2>03 / Espacio en la bodega</h2>
            <p>
              Ampliar el casco te da más casillas para el equipo y las capturas. Recuerda que el equipo también
              ocupa espacio: planifica antes de instalar algo nuevo.
            </p>
            <aside class="consejo">
              <p class="consejo__titulo">Consejo del puerto</p>
              <p>Guarda algunos materiales del dragado: el astillero los pide para casi todas las mejoras.</p>
            </aside>
            """;

    private static final String CONTENIDO_RELIQUIAS = """
            <p class="texto__intro">
              El Coleccionista tiene un encargo para ti: recuperar reliquias perdidas en el archipiélago.
              Esta guía resume cómo prepararte para buscarlas sin revelar la historia.
            </p>
            <h2>01 / Antes de empezar</h2>
            <p>
              Cada reliquia está en una región distinta. Mejora el motor y lleva equipo de dragado: algunas
              zonas quedan lejos del puerto y otras exigen recuperar objetos del fondo.
            </p>
            <h2>02 / Consejos para la búsqueda</h2>
            <p>
              Habla con los habitantes de cada región: sus historias dan pistas. Planifica las travesías
              largas de día y descansa en los muelles que encuentres en el camino.
            </p>
            <aside class="consejo">
              <p class="consejo__titulo">Sin destripes</p>
              <p>Esta guía no revela qué ocurre al entregar las reliquias. ¡Descúbrelo tú!</p>
            </aside>
            """;

    private static final String CONTENIDO_ABERRACIONES = """
            <p class="texto__intro">
              Algunos peces del archipiélago no son lo que parecen. Las aberraciones son versiones alteradas
              de especies comunes y suelen valer más que sus parientes normales.
            </p>
            <h2>01 / Dónde buscarlas</h2>
            <p>
              Aparecen en bancos de peces con un aspecto distinto al habitual. Fíjate en la superficie del agua
              y en las alertas que muestra el juego al acercarte.
            </p>
            <h2>02 / El equipo adecuado</h2>
            <p>
              Necesitas la misma caña que para la especie original. Si no puedes pescar un banco, revisa el
              tipo de agua y vuelve con el equipo correcto.
            </p>
            <h2>03 / De noche</h2>
            <p>
              La noche trae más sorpresas, pero también más peligro. Si sales a buscarlas tarde, ten claro el
              camino de vuelta y no te alejes de los muelles.
            </p>
            """;

    private static final String CONTENIDO_MERCADER = """
            <p class="texto__intro">
              Entre muelle y muelle te cruzarás con una mercader que viaja por el archipiélago con objetos
              difíciles de encontrar en el pueblo.
            </p>
            <h2>01 / Dónde encontrarla</h2>
            <p>
              Cambia de ubicación según la región que estés explorando. Si ves su puesto, acércate: su
              inventario no es el mismo que el de las tiendas de Médula Mayor.
            </p>
            <h2>02 / Qué ofrece</h2>
            <p>
              Equipo y objetos poco comunes. Revisa siempre el espacio de tu bodega antes de comprar.
            </p>
            """;
}
