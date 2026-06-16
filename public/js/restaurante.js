/* ============================
   URBAN EATS - Restaurante JS
   ============================ */

function ocultarMensaje() {
  const msg = document.getElementById('msg-verificacion');
  if (msg) msg.style.display = 'none';
  ['err-r-nombre', 'err-r-dir', 'err-r-horario']
    .forEach(id => { document.getElementById(id).textContent = ''; });
}

function mostrarMensaje(exito) {
  const msg = document.getElementById('msg-verificacion');
  msg.style.display = 'flex';
  if (exito) {
    msg.className = 'msg-verificacion msg-exito';
    msg.innerHTML = '<i class="fas fa-check-circle"></i> Datos válidos. Enviando...';
  } else {
    msg.className = 'msg-verificacion msg-error';
    msg.innerHTML = '<i class="fas fa-exclamation-triangle"></i> Hay errores en el formulario. Por favor corrígelos.';
  }
}

function validarTodo() {
  let ok = true;

  const nombre = document.getElementById('r-nombre').value.trim();
  const errN   = document.getElementById('err-r-nombre');
  if (!nombre) {
    errN.textContent = '⚠ El nombre del restaurante es obligatorio.'; ok = false;
  } else if (nombre.length < 3) {
    errN.textContent = '⚠ El nombre debe tener al menos 3 caracteres.'; ok = false;
  } else errN.textContent = '';

  const dir    = document.getElementById('r-direccion').value.trim();
  const errDir = document.getElementById('err-r-dir');
  if (!dir) {
    errDir.textContent = '⚠ La dirección es obligatoria.'; ok = false;
  } else if (dir.length < 5) {
    errDir.textContent = '⚠ Ingresa una dirección más completa.'; ok = false;
  } else errDir.textContent = '';

  const horario = document.getElementById('r-horario').value.trim();
  const errHor  = document.getElementById('err-r-horario');
  if (!horario) {
    errHor.textContent = '⚠ El horario es obligatorio. Ej: 12:00 AM – 5:00 AM'; ok = false;
  } else errHor.textContent = '';

  return ok;
}

document.getElementById('btn-verificar').addEventListener('click', () => {
  ocultarMensaje();
  const ok = validarTodo();
  if (!ok) {
    mostrarMensaje(false);
    return;
  }
  document.getElementById('form-restaurante').submit();
});
