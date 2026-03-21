/* ============================
   URBAN EATS - Restaurante JS
   ============================ */

const RESTAURANTE_DEFAULT = {
  nombre: '', direccion: '',
  horario: '',
  menu: []
};

let restaurante = JSON.parse(localStorage.getItem('ue_restaurante') || 'null') ||
  { ...RESTAURANTE_DEFAULT, menu: [...RESTAURANTE_DEFAULT.menu] };
let menuTemp = [...restaurante.menu];

function persistir() {
  localStorage.setItem('ue_restaurante', JSON.stringify(restaurante));
}

function renderizarChips() {
  const wrap = document.getElementById('menu-chips');
  if (menuTemp.length === 0) {
    wrap.innerHTML = '<span style="font-size:0.82rem;color:var(--texto-gris);font-style:italic;">Sin ítems en el menú.</span>';
    return;
  }
  wrap.innerHTML = menuTemp.map((item, i) => `
    <div class="menu-chip">
      ${item}
      <button type="button" onclick="quitarItem(${i})" title="Quitar">
        <i class="fas fa-times"></i>
      </button>
    </div>
  `).join('');
}

function quitarItem(i) {
  menuTemp.splice(i, 1);
  renderizarChips();
}

function llenarFormulario() {
  document.getElementById('r-nombre').value    = restaurante.nombre;
  document.getElementById('r-direccion').value = restaurante.direccion;
  document.getElementById('r-horario').value   = restaurante.horario;
  menuTemp = [...restaurante.menu];
  renderizarChips();
  ocultarMensaje();
}

function ocultarMensaje() {
  const msg = document.getElementById('msg-verificacion');
  if (msg) msg.style.display = 'none';
  ['err-r-nombre','err-r-dir','err-r-horario']
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

function agregarItem() {
  const input = document.getElementById('input-menu');
  const val   = input.value.trim();
  if (!val) return;
  menuTemp.push(val);
  input.value = '';
  renderizarChips();
  input.focus();
}

document.getElementById('btn-add-item').addEventListener('click', agregarItem);
document.getElementById('input-menu').addEventListener('keydown', (e) => {
  if (e.key === 'Enter') { e.preventDefault(); agregarItem(); }
});

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
  const ok = validarTodo();
  mostrarMensaje(ok);
  if (!ok) return;
  restaurante.nombre    = document.getElementById('r-nombre').value.trim();
  restaurante.direccion = document.getElementById('r-direccion').value.trim();
  restaurante.horario   = document.getElementById('r-horario').value.trim();
  restaurante.menu      = [...menuTemp];
  persistir();
  window.UE.mostrarToast('¡Datos del restaurante guardados!');
});

llenarFormulario();
