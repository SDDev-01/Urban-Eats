/* ============================
   URBAN EATS - Script Principal
   ============================ */

// ---- NAVBAR: marcar página activa ----
// Las direcciones ahora son rutas de Spring (/catalogo), no archivos (catalogo.html),
// asi que se compara la ruta completa y no el ultimo segmento.
(function marcarActivo() {
  const ruta = window.location.pathname;
  const links = document.querySelectorAll('.navbar-link');
  links.forEach(link => {
    const href = link.getAttribute('href');
    if (href === ruta || (ruta === '/' && href === '/')) {
      link.classList.add('activo');
    }
  });
})();

// ---- SEGURIDAD: escapar texto antes de meterlo en innerHTML ----
// Nombres de platos, cocinas o clientes vienen de la base: si alguien escribe
// "<script>" en un nombre, sin esto el navegador lo ejecutaria (XSS).
function escaparHTML(texto) {
  return String(texto ?? '')
    .replace(/&/g, '&amp;')
    .replace(/</g, '&lt;')
    .replace(/>/g, '&gt;')
    .replace(/"/g, '&quot;')
    .replace(/'/g, '&#39;');
}

// ---- CSRF: encabezados para los fetch que modifican datos (POST) ----
function encabezadosCSRF() {
  const token = document.querySelector('meta[name="csrf-token"]')?.content ?? '';
  return { 'Content-Type': 'application/json', 'X-CSRF-TOKEN': token };
}

// ---- FORMATO DE PESOS COLOMBIANOS: $18.500 ----
function formatoCOP(valor) {
  return '$' + Number(valor || 0).toLocaleString('es-CO', { maximumFractionDigits: 0 });
}

// ---- TOAST ----
function mostrarToast(mensaje, icono = 'fa-check-circle') {
  let contenedor = document.getElementById('toast-container');
  if (!contenedor) {
    contenedor = document.createElement('div');
    contenedor.id = 'toast-container';
    document.body.appendChild(contenedor);
  }
  const toast = document.createElement('div');
  toast.className = 'toast';
  toast.innerHTML = `<i class="fas ${escaparHTML(icono)}"></i> ${escaparHTML(mensaje)}`;
  contenedor.appendChild(toast);
  setTimeout(() => toast.remove(), 3500);
}

// ---- CARRITO: funciones base ----
function obtenerCarrito() {
  return JSON.parse(localStorage.getItem('ue_carrito') || '[]');
}

function guardarCarrito(carrito) {
  localStorage.setItem('ue_carrito', JSON.stringify(carrito));
  actualizarBadgeCarrito();
}

function agregarAlCarrito(producto) {
  const carrito = obtenerCarrito();
  const existe = carrito.find(p => p.id === producto.id);
  if (existe) {
    existe.cantidad += 1;
  } else {
    carrito.push({ ...producto, cantidad: 1 });
  }
  guardarCarrito(carrito);
  mostrarToast(`¡${producto.nombre} agregado al carrito!`);
}

// Agrega un plato desde el modal del catalogo o del detalle de una cocina.
// Reglas: el plato debe estar disponible y el carrito debe ser de UNA sola cocina
// (cada cocina despacha su propio domicilio). El servidor vuelve a validar todo al pagar.
function agregarPlatoAlCarrito(producto, cantidad) {
  if (producto.disponible === false) {
    mostrarToast(`${producto.nombre} no está disponible en este momento.`, 'fa-ban');
    return false;
  }
  let carrito = obtenerCarrito();
  const otraCocina = carrito.find(p => p.restaurante_id && producto.restaurante_id
                                      && p.restaurante_id !== producto.restaurante_id);
  if (otraCocina) {
    const vaciar = confirm(`Tu carrito tiene platos de "${otraCocina.restaurante_nombre}". ` +
      `Cada cocina envía su propio domicilio.\n\n¿Vaciar el carrito y empezar con "${producto.restaurante_nombre}"?`);
    if (!vaciar) return false;
    carrito = [];
  }
  const existe = carrito.find(p => p.id === producto.id);
  if (existe) {
    existe.cantidad += cantidad;
  } else {
    carrito.push({ ...producto, cantidad });
  }
  guardarCarrito(carrito);
  mostrarToast(`¡${producto.nombre} agregado al carrito!`);
  return true;
}

// HTML con la informacion nutricional de un plato para el modal de detalle.
function infoNutricionalHTML(producto) {
  const partes = [];
  if (producto.calorias) {
    partes.push(`<span class="dato-nutricional"><i class="fas fa-fire"></i> ${escaparHTML(producto.calorias)} kcal aprox.</span>`);
  }
  (producto.etiquetas || []).forEach(etiqueta => {
    const clase = etiqueta === 'Balanceado' ? 'sello-plato sello-balanceado' : 'sello-plato';
    partes.push(`<span class="${clase}">${escaparHTML(etiqueta)}</span>`);
  });
  if (producto.alergenos) {
    partes.push(`<span class="dato-alergenos"><i class="fas fa-triangle-exclamation"></i> Contiene: ${escaparHTML(producto.alergenos)}</span>`);
  }
  if (producto.disponible === false) {
    partes.push('<span class="sello-plato sello-agotado">No disponible ahora</span>');
  }
  return partes.join(' ');
}

// Convierte los data-* de un boton "Ver detalles" en el objeto producto del carrito.
function productoDesdeDataset(datos) {
  return {
    id: parseInt(datos.id),
    nombre: datos.nombre,
    descripcion: datos.descripcion,
    precio: parseFloat(datos.precio),
    categoria: datos.categoria,
    restaurante_id: datos.restaurante ? parseInt(datos.restaurante) : null,
    restaurante_nombre: datos.restauranteNombre || '',
    disponible: datos.disponible !== 'false',
    calorias: datos.calorias ? parseInt(datos.calorias) : null,
    etiquetas: datos.etiquetas ? datos.etiquetas.split(',').map(e => e.trim()).filter(Boolean) : [],
    alergenos: datos.alergenos || ''
  };
}

function actualizarBadgeCarrito() {
  const badge = document.getElementById('carrito-badge');
  if (!badge) return;
  const carrito = obtenerCarrito();
  const total = carrito.reduce((sum, p) => sum + p.cantidad, 0);
  badge.textContent = total;
  badge.style.display = total > 0 ? 'flex' : 'none';
}

// Inicializar badge al cargar
actualizarBadgeCarrito();

// ---- LOGIN ----
const formLogin = document.getElementById('form-login');
if (formLogin) {
  formLogin.addEventListener('submit', (e) => {
    e.preventDefault();
    const email = document.getElementById('email-login');
    const password = document.getElementById('password-login');
    const errEmail = document.getElementById('error-email-login');
    const errPass = document.getElementById('error-pass-login');

    // Limpiar errores
    if (errEmail) errEmail.textContent = '';
    if (errPass) errPass.textContent = '';

    let valido = true;

    // Validar email
    const regexEmail = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    if (!regexEmail.test(email.value.trim())) {
      if (errEmail) errEmail.textContent = 'Ingresa un correo válido.';
      valido = false;
    }

    // Validar contraseña (solo que no venga vacia: los usuarios antiguos pueden tener claves cortas)
    if (password.value.length === 0) {
      if (errPass) errPass.textContent = 'Ingresa tu contraseña.';
      valido = false;
    }

    if (valido) {
      formLogin.submit();
    }
  });
}

// ---- REGISTRO ----
const formRegistro = document.getElementById('form-registro');
if (formRegistro) {
  formRegistro.addEventListener('submit', (e) => {
    e.preventDefault();

    const campos = {
      nombres: document.getElementById('reg-nombres'),
      apellidos: document.getElementById('reg-apellidos'),
      email: document.getElementById('reg-email'),
      telefono: document.getElementById('reg-telefono'),
      direccion: document.getElementById('reg-direccion'),
      password: document.getElementById('reg-password'),
    };

    let valido = true;

    // Validar cada campo
    Object.entries(campos).forEach(([key, input]) => {
      const err = document.getElementById('error-' + key);
      if (err) err.textContent = '';
      if (!input || input.value.trim() === '') {
        if (err) err.textContent = 'Este campo es obligatorio.';
        valido = false;
      }
    });

    // Validar email
    const regexEmail = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    if (campos.email && !regexEmail.test(campos.email.value.trim())) {
      const err = document.getElementById('error-email');
      if (err) err.textContent = 'Correo inválido.';
      valido = false;
    }

    // Validar teléfono (exactamente 10 dígitos, solo números)
    const telefono = campos.telefono.value.trim();
    if (campos.telefono && (!/^\d{10}$/.test(telefono))) {
      const err = document.getElementById('error-telefono');
      if (err) err.textContent = 'El teléfono debe tener exactamente 10 dígitos.';
      valido = false;
    }

    // Validar que nombres y apellidos no contengan números
    const regexSoloLetras = /^[\p{L}\s\-'\.]+$/u;
    if (campos.nombres && campos.nombres.value.trim() && !regexSoloLetras.test(campos.nombres.value.trim())) {
      const err = document.getElementById('error-nombres');
      if (err) err.textContent = 'El nombre no debe contener números.';
      valido = false;
    }
    if (campos.apellidos && campos.apellidos.value.trim() && !regexSoloLetras.test(campos.apellidos.value.trim())) {
      const err = document.getElementById('error-apellidos');
      if (err) err.textContent = 'El apellido no debe contener números.';
      valido = false;
    }

    // Validar contraseña (mínimo 8 caracteres, la misma regla que valida el servidor)
    if (campos.password && campos.password.value.length < 8) {
      const err = document.getElementById('error-password');
      if (err) err.textContent = 'La contraseña debe tener al menos 8 caracteres.';
      valido = false;
    }

    if (valido) {
      formRegistro.submit();
    }
  });

  // Validar que el teléfono solo acepte números en tiempo real
  const telefonoInput = document.getElementById('reg-telefono');
  if (telefonoInput) {
    telefonoInput.addEventListener('input', function(e) {
      // Eliminar todo lo que no sea número
      this.value = this.value.replace(/\D/g, '');
      // Limitar a 10 dígitos
      if (this.value.length > 10) {
        this.value = this.value.substring(0, 10);
      }
    });
  }
}

// ---- VALIDACIÓN GENÉRICA ----
function validarCampo(input, errorId, mensaje) {
  const err = document.getElementById(errorId);
  if (!input.value.trim()) {
    if (err) err.textContent = mensaje || 'Campo obligatorio.';
    return false;
  }
  if (err) err.textContent = '';
  return true;
}

// Exportar funciones globales que otros scripts necesitan
// ---- Respuestas JSON y sesion cambiada ----
// Las pestanas de un navegador comparten la sesion de cada direccion (localhost,
// 127.0.0.1...). Si otro usuario inicia sesion, esta pestana queda sin su sesion y el
// servidor responde con el login (HTML), 401 o el 403 de Spring Security.
const MENSAJE_SESION_CAMBIO = 'Tu sesión cambió: otro usuario inició sesión en este navegador. '
  + 'Recarga la página o vuelve a iniciar sesión (usa una dirección distinta para cada rol).';

async function leerRespuestaJson(respuesta) {
  const tipo = respuesta.headers.get('Content-Type') || '';
  if (respuesta.status === 401 || (respuesta.redirected && respuesta.url.includes('/login'))
      || !tipo.includes('json')) {
    throw new Error(MENSAJE_SESION_CAMBIO);
  }
  const datos = await respuesta.json();
  // El error de Spring Security trae "path"; los errores de la aplicacion no.
  if (respuesta.status === 403 && datos.path) throw new Error(MENSAJE_SESION_CAMBIO);
  return datos;
}

// ---- Navegacion con Google Maps o Waze (sin llave ni costo) ----
// Sin "origin", Google Maps y Waze salen desde donde esta el celular del repartidor.
function enlacesNavegacion(lat, lng, textoDireccion, enBicicleta) {
  const conPunto = lat != null && lat !== '' && lng != null && lng !== '';
  const destino = conPunto ? `${lat},${lng}` : `${textoDireccion || ''}, Bogotá`;
  return {
    google: 'https://www.google.com/maps/dir/?api=1&destination=' + encodeURIComponent(destino)
      + '&travelmode=' + (enBicicleta ? 'bicycling' : 'driving'),
    waze: conPunto
      ? `https://waze.com/ul?ll=${lat},${lng}&navigate=yes`
      : 'https://waze.com/ul?q=' + encodeURIComponent(destino) + '&navigate=yes',
  };
}

window.UE = {
  mostrarToast,
  obtenerCarrito,
  guardarCarrito,
  agregarAlCarrito,
  agregarPlatoAlCarrito,
  infoNutricionalHTML,
  productoDesdeDataset,
  actualizarBadgeCarrito,
  validarCampo,
  escaparHTML,
  encabezadosCSRF,
  formatoCOP,
  leerRespuestaJson,
  MENSAJE_SESION_CAMBIO,
  enlacesNavegacion,
};
