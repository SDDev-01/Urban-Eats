/* ============================
   URBAN EATS - Repartidor JS
   ============================ */

const REPARTIDOR_DEFAULT = {
  nombres: '', apellidos: '',
  email: '', telefono: '',
  direccion: '',
  vehiculo: '', placa: '',
  licencia: '', soat: '', seguro: ''
};

let repartidor = JSON.parse(localStorage.getItem('ue_repartidor') || 'null') || { ...REPARTIDOR_DEFAULT };

function persistir() {
  localStorage.setItem('ue_repartidor', JSON.stringify(repartidor));
}

function llenarFormulario() {
  document.getElementById('rp-nombres').value   = repartidor.nombres;
  document.getElementById('rp-apellidos').value = repartidor.apellidos;
  document.getElementById('rp-email').value     = repartidor.email;
  document.getElementById('rp-telefono').value  = repartidor.telefono;
  document.getElementById('rp-direccion').value = repartidor.direccion;
  document.getElementById('rp-vehiculo').value  = repartidor.vehiculo;
  document.getElementById('rp-placa').value     = repartidor.placa;
  document.getElementById('rp-licencia').value  = repartidor.licencia;
  document.getElementById('rp-soat').value      = repartidor.soat;
  document.getElementById('rp-seguro').value    = repartidor.seguro;
  ocultarMensaje();
}

function ocultarMensaje() {
  const msg = document.getElementById('msg-verificacion');
  if (msg) msg.style.display = 'none';
  ['err-nombres','err-apellidos','err-email','err-telefono','err-dir',
   'err-vehiculo','err-placa','err-licencia','err-soat','err-seguro']
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

  const nombres = document.getElementById('rp-nombres').value.trim();
  const errN    = document.getElementById('err-nombres');
  if (!nombres) { errN.textContent = '⚠ El nombre es obligatorio.'; ok = false; }
  else if (/\d/.test(nombres)) { errN.textContent = '⚠ El nombre no puede contener números.'; ok = false; }
  else errN.textContent = '';

  const apellidos = document.getElementById('rp-apellidos').value.trim();
  const errA      = document.getElementById('err-apellidos');
  if (!apellidos) { errA.textContent = '⚠ El apellido es obligatorio.'; ok = false; }
  else if (/\d/.test(apellidos)) { errA.textContent = '⚠ El apellido no puede contener números.'; ok = false; }
  else errA.textContent = '';

  const email = document.getElementById('rp-email').value.trim();
  const errE  = document.getElementById('err-email');
  if (!email) { errE.textContent = '⚠ El correo es obligatorio.'; ok = false; }
  else if (!/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(email)) { errE.textContent = '⚠ Correo inválido. Formato: nombre@dominio.com'; ok = false; }
  else errE.textContent = '';

  const tel     = document.getElementById('rp-telefono').value.trim();
  const errT    = document.getElementById('err-telefono');
  const digitos = tel.replace(/[\s\-]/g, '');
  if (!tel) { errT.textContent = '⚠ El teléfono es obligatorio.'; ok = false; }
  else if (!/^\d[\d\s\-]+$/.test(tel)) { errT.textContent = '⚠ Solo puede contener números.'; ok = false; }
  else if (digitos.length < 7 || digitos.length > 15) { errT.textContent = '⚠ Debe tener entre 7 y 15 dígitos.'; ok = false; }
  else errT.textContent = '';

  const dir  = document.getElementById('rp-direccion').value.trim();
  const errD = document.getElementById('err-dir');
  if (!dir) { errD.textContent = '⚠ La dirección es obligatoria.'; ok = false; }
  else if (dir.length < 5) { errD.textContent = '⚠ Ingresa una dirección más completa.'; ok = false; }
  else errD.textContent = '';

  const veh    = document.getElementById('rp-vehiculo').value;
  const errVeh = document.getElementById('err-vehiculo');
  if (!veh) { errVeh.textContent = '⚠ Selecciona el tipo de vehículo.'; ok = false; }
  else errVeh.textContent = '';

  const placa  = document.getElementById('rp-placa').value.trim();
  const errP   = document.getElementById('err-placa');
  if (!placa) { errP.textContent = '⚠ La placa es obligatoria.'; ok = false; }
  else if (placa.length < 3) { errP.textContent = '⚠ Placa inválida. Ej: ABC-123'; ok = false; }
  else errP.textContent = '';

  const lic  = document.getElementById('rp-licencia').value.trim();
  const errL = document.getElementById('err-licencia');
  if (!lic) { errL.textContent = '⚠ La licencia es obligatoria.'; ok = false; }
  else errL.textContent = '';

  const soat  = document.getElementById('rp-soat').value.trim();
  const errS  = document.getElementById('err-soat');
  if (!soat) { errS.textContent = '⚠ El SOAT es obligatorio.'; ok = false; }
  else errS.textContent = '';

  const seguro = document.getElementById('rp-seguro').value.trim();
  const errSeg = document.getElementById('err-seguro');
  if (!seguro) { errSeg.textContent = '⚠ El seguro es obligatorio.'; ok = false; }
  else errSeg.textContent = '';

  return ok;
}

document.getElementById('btn-verificar').addEventListener('click', () => {
  const ok = validarTodo();
  mostrarMensaje(ok);
  if (!ok) return;
  repartidor.nombres   = document.getElementById('rp-nombres').value.trim();
  repartidor.apellidos = document.getElementById('rp-apellidos').value.trim();
  repartidor.email     = document.getElementById('rp-email').value.trim();
  repartidor.telefono  = document.getElementById('rp-telefono').value.trim();
  repartidor.direccion = document.getElementById('rp-direccion').value.trim();
  repartidor.vehiculo  = document.getElementById('rp-vehiculo').value;
  repartidor.placa     = document.getElementById('rp-placa').value.trim().toUpperCase();
  repartidor.licencia  = document.getElementById('rp-licencia').value.trim();
  repartidor.soat      = document.getElementById('rp-soat').value.trim();
  repartidor.seguro    = document.getElementById('rp-seguro').value.trim();
  persistir();
  window.UE.mostrarToast('¡Datos del repartidor guardados!');
});

llenarFormulario();
