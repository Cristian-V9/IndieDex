/**
 * Textos de las páginas informativas del pie de página.
 * Se usan en las páginas (/normas, /terminos...) y en las ventanas flotantes
 * de los formularios (normas en Sugerencias, términos y privacidad en el Registro).
 */

export interface SeccionTexto {
  titulo: string;
  parrafos: string[];
}

export interface PaginaTexto {
  eyebrow: string;
  titulo: string;
  intro: string;
  secciones: SeccionTexto[];
  actualizado: string;
}

export type ClaveTexto = 'sobre' | 'normas' | 'contacto' | 'privacidad' | 'terminos' | 'cookies';

export const TEXTOS: Record<ClaveTexto, PaginaTexto> = {
  sobre: {
    eyebrow: 'Comunidad',
    titulo: 'Sobre IndieDex',
    intro: 'IndieDex es una wiki colaborativa, en español, dedicada solo a juegos independientes. '
      + 'Pequeños juegos, grandes historias compartidas.',
    actualizado: 'noviembre de 2026',
    secciones: [
      {
        titulo: 'Por qué existe',
        parrafos: [
          'Los juegos indie suelen tener poca documentación en español: guías incompletas, foros dispersos o '
          + 'wikis en otros idiomas. IndieDex reúne ese conocimiento en un solo lugar, cuidado por la comunidad.',
          'Empezamos con Dredge y crecemos según lo que la comunidad pide y vota.',
        ],
      },
      {
        titulo: 'Cómo funciona',
        parrafos: [
          'El equipo editor escribe y revisa las guías de cada juego. La comunidad propone mejoras, '
          + 'correcciones o nuevas wikis, y vota las propuestas que quiere ver primero.',
          'Cada propuesta recorre cuatro pasos: Sugerencia, Votos, Revisión y Publicado. '
          + 'Si el equipo la rechaza, siempre deja una nota explicando por qué.',
        ],
      },
      {
        titulo: 'Quiénes somos',
        parrafos: [
          'IndieDex es un proyecto académico de desarrollo Back-End (Spring Boot, MySQL y Angular) creado por '
          + 'Zoe Gabriel Balagay Aponte, Juandiego Rosas Reyes y Cristian Alexander Vargas Fajardo.',
          'No estamos afiliados a los estudios ni a las editoras de los juegos: sus nombres, imágenes y marcas '
          + 'pertenecen a sus creadores.',
        ],
      },
    ],
  },

  normas: {
    eyebrow: 'Comunidad',
    titulo: 'Normas de la comunidad',
    intro: 'IndieDex funciona porque la gente comparte con cuidado. Estas normas aplican a propuestas, '
      + 'votos, comentarios y a tu nombre de usuario.',
    actualizado: 'noviembre de 2026',
    secciones: [
      {
        titulo: '1. Respeto ante todo',
        parrafos: [
          'Trata a los demás con amabilidad, sobre todo a quienes acaban de llegar. No se permiten insultos, '
          + 'acoso, discriminación ni mensajes de odio.',
        ],
      },
      {
        titulo: '2. Avisa los destripes',
        parrafos: [
          'Si tu comentario o propuesta revela partes de la historia, escribe «[Destripe]» al inicio. '
          + 'Las guías de la wiki evitan destripes salvo en secciones marcadas.',
        ],
      },
      {
        titulo: '3. Comparte información útil y verificable',
        parrafos: [
          'Propón una idea concreta por propuesta y explica por qué sería útil. Incluye fuentes cuando las '
          + 'tengas y no copies textos de otras wikis o sitios sin citarlos.',
          'Antes de proponer algo, busca si ya existe: es mejor votar una propuesta que duplicarla.',
        ],
      },
      {
        titulo: '4. Solo juegos independientes',
        parrafos: [
          'IndieDex se centra en juegos indie. Las propuestas de juegos de grandes estudios se rechazan con una nota.',
        ],
      },
      {
        titulo: '5. Sin publicidad ni enlaces engañosos',
        parrafos: [
          'No publiques promociones, enlaces de descarga ilegal, estafas ni enlaces que lleven a sitios dañinos.',
        ],
      },
      {
        titulo: '6. Moderación',
        parrafos: [
          'El equipo editor puede rechazar propuestas (siempre con una nota) y borrar comentarios que incumplan '
          + 'estas normas. Si tienes dudas sobre una decisión, envía una propuesta de tipo Corrección.',
        ],
      },
    ],
  },

  contacto: {
    eyebrow: 'Comunidad',
    titulo: 'Contacto',
    intro: 'La forma más rápida de hablar con el equipo es dentro de la propia wiki.',
    actualizado: 'noviembre de 2026',
    secciones: [
      {
        titulo: 'Errores en una guía o ideas nuevas',
        parrafos: [
          'Envía una propuesta en la sección Sugerencias. Elige el tipo Corrección para errores y Mejora o '
          + 'Nueva guía para ideas. El equipo la revisa y te responde con una nota que verás en tu perfil.',
        ],
      },
      {
        titulo: 'Dudas sobre una guía',
        parrafos: [
          'Deja un comentario al final del artículo: la comunidad y el equipo editor leen la conversación.',
        ],
      },
      {
        titulo: 'Equipo del proyecto',
        parrafos: [
          'Zoe Gabriel Balagay Aponte · Juandiego Rosas Reyes · Cristian Alexander Vargas Fajardo.',
        ],
      },
    ],
  },

  privacidad: {
    eyebrow: 'Legal',
    titulo: 'Política de privacidad',
    intro: 'Qué datos guarda IndieDex, para qué los usa y qué puedes hacer con ellos.',
    actualizado: 'noviembre de 2026',
    secciones: [
      {
        titulo: 'Datos que guardamos',
        parrafos: [
          'Al crear tu cuenta: nombre de usuario, correo electrónico y contraseña. La contraseña se guarda '
          + 'cifrada (BCrypt): nadie, ni siquiera el equipo, puede leerla.',
          'Mientras participas: tu biografía, tus propuestas, tus votos y tus comentarios.',
        ],
      },
      {
        titulo: 'Para qué los usamos',
        parrafos: [
          'Solo para que funcione la wiki: iniciar sesión, mostrar tus aportes, contar los votos y moderar el contenido.',
          'No vendemos ni compartimos tus datos con terceros, y no los usamos para publicidad.',
        ],
      },
      {
        titulo: 'Qué es público',
        parrafos: [
          'Tu nombre de usuario, tu biografía y tus aportes (propuestas y comentarios) son visibles para todos. '
          + 'Tu correo nunca se muestra a otros usuarios.',
        ],
      },
      {
        titulo: 'Tus opciones',
        parrafos: [
          'Puedes editar tu biografía desde tu perfil y borrar tus comentarios. Para eliminar tu cuenta, '
          + 'escribe al equipo desde Sugerencias.',
        ],
      },
      {
        titulo: 'Proyecto académico',
        parrafos: [
          'IndieDex es un proyecto de clase en etapa de prueba. Te recomendamos no usar contraseñas que '
          + 'uses en otros servicios.',
        ],
      },
    ],
  },

  terminos: {
    eyebrow: 'Legal',
    titulo: 'Términos de uso',
    intro: 'Al crear una cuenta o usar IndieDex aceptas estas condiciones.',
    actualizado: 'noviembre de 2026',
    secciones: [
      {
        titulo: '1. Tu cuenta',
        parrafos: [
          'Eres responsable de tu contraseña y de lo que se publique desde tu cuenta. Tu nombre de usuario es '
          + 'público y no puede suplantar a otra persona ni ser ofensivo.',
        ],
      },
      {
        titulo: '2. El contenido que aportas',
        parrafos: [
          'Tus propuestas y comentarios siguen siendo tuyos, pero al publicarlos aceptas compartirlos bajo la '
          + 'licencia CC BY-SA, como el resto del contenido comunitario de IndieDex.',
          'Solo publica contenido que tengas derecho a compartir.',
        ],
      },
      {
        titulo: '3. Conducta',
        parrafos: [
          'Debes cumplir las Normas de la comunidad. El equipo puede rechazar propuestas, borrar comentarios '
          + 'y, en casos graves, suspender cuentas.',
        ],
      },
      {
        titulo: '4. Los juegos y sus marcas',
        parrafos: [
          'Los juegos, sus nombres e imágenes pertenecen a sus creadores. IndieDex es una wiki de aficionados '
          + 'y no está afiliada a ningún estudio ni editora.',
        ],
      },
      {
        titulo: '5. Sin garantías',
        parrafos: [
          'Las guías se escriben con cuidado, pero pueden contener errores o quedar desactualizadas. '
          + 'IndieDex es un proyecto académico y se ofrece tal como está, sin fines comerciales.',
        ],
      },
      {
        titulo: '6. Cambios',
        parrafos: [
          'Podemos actualizar estos términos. La fecha de la última actualización aparece al final de esta página.',
        ],
      },
    ],
  },

  cookies: {
    eyebrow: 'Legal',
    titulo: 'Cookies y almacenamiento',
    intro: 'IndieDex no usa cookies de publicidad ni de seguimiento.',
    actualizado: 'noviembre de 2026',
    secciones: [
      {
        titulo: 'Qué guardamos en tu navegador',
        parrafos: [
          'Solo tu sesión: al iniciar sesión guardamos un token de acceso en el almacenamiento del navegador.',
          'Si marcas «Mantener mi sesión iniciada» se guarda en localStorage y sigue ahí al cerrar el navegador. '
          + 'Si no la marcas, se guarda en sessionStorage y se borra al cerrar la pestaña.',
        ],
      },
      {
        titulo: 'Servicios externos',
        parrafos: [
          'Las tipografías del sitio se cargan desde Google Fonts, por lo que tu navegador se conecta a los '
          + 'servidores de Google al abrir IndieDex.',
        ],
      },
      {
        titulo: 'Cómo borrarlo',
        parrafos: [
          'Pulsa «Salir» para cerrar tu sesión y borrar el token, o elimina los datos del sitio desde la '
          + 'configuración de tu navegador.',
        ],
      },
    ],
  },
};
