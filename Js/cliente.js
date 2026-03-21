/* ============================
   URBAN EATS - Cliente JS
   ============================ */

const CLIENTE_DEFAULT = {
  nombres: 'Laura', apellidos: 'Martínez',
  email: 'laura@email.com', telefono: '311 234 5678',
  direccion: 'Cra 7 # 45-12, Bogotá'
};

let cliente = JSON.parse(localStorage.getItem('ue_cliente') || 'null') || { ...CLIENTE_DEFAULT };

function persistir() {
  localStorage.setItem('ue_cliente', JSON.stringify(cliente));
}

function llenarFormulario() {
  document.getElementById('c-nombres').value   = cliente.nombres;
  document.getElementById('c-apellidos').value = cliente.apellidos;
  document.getElementById('c-email').value     = cliente.email;
  document.getElementById('c-telefono').value  = cliente.telefono;
  document.getElementById('c-direccion').value = cliente.direccion;
  ocultarMensaje();
}

function ocultarMensaje() {
  const msg = document.getElementById('msg-verificacion');
  if (msg) msg.style.display = 'none';
  ['err-nombres','err-apellidos','err-email','err-telefono','err-dir']
    .forEach(id => { document.getElementById(id).textContent = ''; });
}

function mostrarMensaje(exito) {
  const msg = document.getElementById('msg-verificacion');
  msg.style.display = 'flex';
  if (exito) {
    msg.className = 'msg-verificacion msg-exito';
    msg.innerHTML = '<i class="fas fa-check-circle"></i> ¡Registro verificado exitosamente! Datos guardados.';
  } else {
    msg.className = 'msg-verificacion msg-error';
    msg.innerHTML = '<i class="fas fa-exclamation-triangle"></i> Hay errores en el formulario. Por favor corrígelos.';
  }
}

function validarTodo() {
  let ok = true;

  const nombres = document.getElementById('c-nombres').value.trim();
  const errN    = document.getElementById('err-nombres');
  if (!nombres) {
    errN.textContent = '⚠ El nombre es obligatorio.'; ok = false;
  } else if (/\d/.test(nombres)) {
    errN.textContent = '⚠ El nombre no puede contener números.'; ok = false;
  } else if (nombres.length < 2) {
    errN.textContent = '⚠ El nombre debe tener al menos 2 caracteres.'; ok = false;
  } else errN.textContent = '';

  const apellidos = document.getElementById('c-apellidos').value.trim();
  const errA      = document.getElementById('err-apellidos');
  if (!apellidos) {
    errA.textContent = '⚠ El apellido es obligatorio.'; ok = false;
  } else if (/\d/.test(apellidos)) {
    errA.textContent = '⚠ El apellido no puede contener números.'; ok = false;
  } else errA.textContent = '';

  const email  = document.getElementById('c-email').value.trim();
  const errE   = document.getElementById('err-email');
  if (!email) {
    errE.textContent = '⚠ El correo es obligatorio.'; ok = false;
  } else if (!/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(email)) {
    errE.textContent = '⚠ Correo inválido. Formato: nombre@dominio.com'; ok = false;
  } else errE.textContent = '';

  const tel     = document.getElementById('c-telefono').value.trim();
  const errT    = document.getElementById('err-telefono');
  const digitos = tel.replace(/[\s\-]/g, '');
  if (!tel) {
    errT.textContent = '⚠ El teléfono es obligatorio.'; ok = false;
  } else if (!/^\d[\d\s\-]+$/.test(tel)) {
    errT.textContent = '⚠ El teléfono solo puede contener números.'; ok = false;
  } else if (digitos.length < 7 || digitos.length > 15) {
    errT.textContent = '⚠ El teléfono debe tener entre 7 y 15 dígitos.'; ok = false;
  } else errT.textContent = '';

  const dir  = document.getElementById('c-direccion').value.trim();
  const errD = document.getElementById('err-dir');
  if (!dir) {
    errD.textContent = '⚠ La dirección es obligatoria.'; ok = false;
  } else if (dir.length < 5) {
    errD.textContent = '⚠ Ingresa una dirección más completa.'; ok = false;
  } else errD.textContent = '';

  return ok;
}

// Botón Aceptar: valida y guarda si todo está bien
document.getElementById('btn-verificar').addEventListener('click', () => {
  const ok = validarTodo();
  mostrarMensaje(ok);
  if (!ok) return;
  cliente.nombres   = document.getElementById('c-nombres').value.trim();
  cliente.apellidos = document.getElementById('c-apellidos').value.trim();
  cliente.email     = document.getElementById('c-email').value.trim();
  cliente.telefono  = document.getElementById('c-telefono').value.trim();
  cliente.direccion = document.getElementById('c-direccion').value.trim();
  persistir();
  window.UE.mostrarToast('¡Datos del cliente guardados!');
});

llenarFormulario();
