/* ============================
   URBAN EATS - Script Principal
   ============================ */

// ---- NAVBAR: marcar página activa ----
(function marcarActivo() {
  const pagina = window.location.pathname.split('/').pop();
  const links = document.querySelectorAll('.navbar-link');
  links.forEach(link => {
    const href = link.getAttribute('href');
    if (href === pagina || (pagina === '' && href === 'index.html')) {
      link.classList.add('activo');
    }
  });
})();

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

    // Validar contraseña (mínimo 4 caracteres, igual que el login)
    if (campos.password && campos.password.value.length < 4) {
      const err = document.getElementById('error-password');
      if (err) err.textContent = 'La contraseña debe tener al menos 4 caracteres.';
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
window.UE = {
  mostrarToast,
  obtenerCarrito,
  guardarCarrito,
  agregarAlCarrito,
  actualizarBadgeCarrito,
  validarCampo,
};
