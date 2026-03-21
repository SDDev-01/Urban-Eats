/* ============================
   URBAN EATS - Script Principal
   ============================ */

// ---- NAVBAR: marcar página activa ----
(function marcarActivo() {
  const pagina = window.location.pathname.split('/').pop();
  const links = document.querySelectorAll('.navbar-nav a');
  links.forEach(link => {
    const href = link.getAttribute('href');
    if (href === pagina || (pagina === '' && href === 'index.html')) {
      link.classList.add('activo');
    }
  });
})();

// ---- NAVBAR: mostrar nombre de usuario ----
(function mostrarUsuario() {
  const spanNombre = document.getElementById('navbar-nombre');
  if (!spanNombre) return;

  // Primero intenta leer del cliente registrado en el módulo cliente
  const cliente  = JSON.parse(localStorage.getItem('ue_cliente') || 'null');
  const usuario  = JSON.parse(localStorage.getItem('ue_usuario') || 'null');

  if (cliente && cliente.nombres) {
    spanNombre.textContent = cliente.nombres + (cliente.apellidos ? ' ' + cliente.apellidos.split(' ')[0] : '');
  } else if (usuario) {
    spanNombre.textContent = usuario.nombre || usuario.email.split('@')[0];
  } else {
    spanNombre.textContent = 'Usuario';
  }
})();

// ---- NAVBAR: Cerrar sesión ----
const btnSalir = document.getElementById('btn-salir');
if (btnSalir) {
  btnSalir.addEventListener('click', () => {
    localStorage.removeItem('ue_sesion');
    window.location.href = 'login.html';
  });
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
  toast.innerHTML = `<i class="fas ${icono}"></i> ${mensaje}`;
  contenedor.appendChild(toast);
  setTimeout(() => toast.remove(), 3000);
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

    // Validar contraseña
    if (password.value.length < 4) {
      if (errPass) errPass.textContent = 'La contraseña debe tener al menos 4 caracteres.';
      valido = false;
    }

    if (valido) {
      // Guardar sesión simulada
      const usuario = JSON.parse(localStorage.getItem('ue_usuario') || 'null');
      if (usuario && usuario.email === email.value.trim()) {
        localStorage.setItem('ue_sesion', 'activa');
        mostrarToast('Inicio de sesión exitoso. Redirigiendo...');
        setTimeout(() => { window.location.href = 'catalogo.html'; }, 1200);
      } else {
        // Cualquier email/contraseña pasa (modo demo)
        const datosDemo = { nombre: email.value.split('@')[0], email: email.value.trim() };
        localStorage.setItem('ue_usuario', JSON.stringify(datosDemo));
        localStorage.setItem('ue_sesion', 'activa');
        mostrarToast('Bienvenido a Urban Eats!');
        setTimeout(() => { window.location.href = 'catalogo.html'; }, 1200);
      }
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

    if (valido) {
      const usuario = {
        nombres: campos.nombres.value.trim(),
        apellidos: campos.apellidos.value.trim(),
        nombre: campos.nombres.value.trim() + ' ' + campos.apellidos.value.trim(),
        email: campos.email.value.trim(),
        telefono: campos.telefono.value.trim(),
        direccion: campos.direccion.value.trim(),
      };
      localStorage.setItem('ue_usuario', JSON.stringify(usuario));
      localStorage.setItem('ue_sesion', 'activa');
      mostrarToast('¡Cuenta creada exitosamente!');
      setTimeout(() => { window.location.href = 'catalogo.html'; }, 1400);
    }
  });
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
window.UE = {
  mostrarToast,
  obtenerCarrito,
  guardarCarrito,
  agregarAlCarrito,
  actualizarBadgeCarrito,
  validarCampo,
};
