/* ============================
   PERFIL RESTAURANTE (panel de la cocina)
   ============================
   Los pedidos llegan del servidor en JSON (GET /perfilRestaurante/pedidos) y se
   vuelven a pedir cada 15 segundos, asi la cocina ve los pedidos nuevos sin recargar.
   Cada boton (Aceptar, Rechazar, Marcar listo) guarda el cambio en la base con
   POST /perfilRestaurante/pedido/{id}/estado. Antes los botones solo cambiaban la
   pantalla y al recargar todo volvia a "nuevo".

   Estados (clave CSS -> estado real):
     nuevo = Solicitado, preparando = En preparacion, listo = Listo para envio,
     encamino = En camino, entregado = Entregado, rechazado = Cancelado
*/

const INTERVALO_ACTUALIZACION_MS = 15000;

let pedidos = [];
let filtroActivo = 'todos';
let aceptaPedidos = true;

const esc = (texto) => window.UE.escaparHTML(texto);

// ---- INICIALIZACIÓN ----
document.addEventListener('DOMContentLoaded', function () {
  const btnEstado = document.getElementById('btn-estado');
  aceptaPedidos = btnEstado.dataset.acepta !== 'false';
  actualizarBotonEstado();
  btnEstado.addEventListener('click', toggleEstado);

  iniciarTabs();
  iniciarFiltros();
  iniciarConfirmacionEliminar();

  cargarPedidos();
  setInterval(cargarPedidos, INTERVALO_ACTUALIZACION_MS);
});

// ---- PEDIDOS DESDE EL SERVIDOR ----
async function cargarPedidos() {
  try {
    const respuesta = await fetch('/perfilRestaurante/pedidos', { headers: { 'Accept': 'application/json' } });
    const nuevos = await window.UE.leerRespuestaJson(respuesta);
    if (!respuesta.ok) throw new Error(nuevos.error || 'No se pudieron cargar los pedidos.');

    // Aviso cuando entra un pedido que no estaba antes.
    const habiaIds = new Set(pedidos.map(p => p.codigo));
    const llegoNuevo = pedidos.length > 0 && nuevos.some(p => !habiaIds.has(p.codigo) && p.estado === 'nuevo');
    pedidos = nuevos;
    if (llegoNuevo) window.UE.mostrarToast('¡Llegó un pedido nuevo!', 'fa-bell');

    renderTodo();
    document.getElementById('pedidos-actualizado').textContent =
      'Actualizado a las ' + new Date().toLocaleTimeString('es-CO') + ' · se actualiza solo cada 15 segundos';
  } catch (e) {
    document.getElementById('pedidos-actualizado').textContent =
      e.message === window.UE.MENSAJE_SESION_CAMBIO ? e.message : 'No se pudieron cargar los pedidos. Reintentando...';
  }
}

function renderTodo() {
  renderPedidos();
  renderTablaClientes();
  renderEstadisticas();
}

// ---- ACCIONES SOBRE UN PEDIDO ----
async function cambiarEstado(codigo, accion) {
  let motivo = null;
  if (accion === 'rechazar') {
    motivo = prompt('¿Por qué rechazas el pedido? (el cliente lo verá)', 'Sin ingredientes disponibles');
    if (motivo === null) return; // el gerente cancelo el cuadro de dialogo
  }

  try {
    const respuesta = await fetch(`/perfilRestaurante/pedido/${codigo}/estado`, {
      method: 'POST',
      headers: window.UE.encabezadosCSRF(),
      body: JSON.stringify({ accion, motivo })
    });
    const datos = await window.UE.leerRespuestaJson(respuesta);
    if (!respuesta.ok) {
      window.UE.mostrarToast(datos.error || 'No se pudo cambiar el estado.', 'fa-exclamation-circle');
    } else {
      const mensajes = { aceptar: 'Pedido aceptado: ¡a cocinar!', rechazar: 'Pedido rechazado', listo: 'Listo: ya lo pueden recoger' };
      window.UE.mostrarToast(mensajes[accion] || 'Estado actualizado', 'fa-check');
    }
  } catch (e) {
    window.UE.mostrarToast(e.message === window.UE.MENSAJE_SESION_CAMBIO ? e.message : 'Sin conexión con el servidor.',
      'fa-exclamation-circle');
  }
  cargarPedidos();
}

// ---- BOTÓN ABIERTO / CERRADO (pausa la recepción de pedidos) ----
async function toggleEstado() {
  const nuevoValor = !aceptaPedidos;
  try {
    const respuesta = await fetch('/perfilRestaurante/acepta-pedidos', {
      method: 'POST',
      headers: window.UE.encabezadosCSRF(),
      body: JSON.stringify({ acepta: nuevoValor })
    });
    const datos = await window.UE.leerRespuestaJson(respuesta);
    if (!respuesta.ok) throw new Error(datos.error || '');
    aceptaPedidos = datos.acepta;
    actualizarBotonEstado();
    window.UE.mostrarToast(aceptaPedidos ? 'Cocina abierta: recibiendo pedidos' : 'Pedidos pausados',
      aceptaPedidos ? 'fa-door-open' : 'fa-door-closed');
  } catch (e) {
    window.UE.mostrarToast(e.message || 'No se pudo cambiar el estado de la cocina.', 'fa-exclamation-circle');
  }
}

function actualizarBotonEstado() {
  const btn  = document.getElementById('btn-estado');
  const dot  = document.getElementById('estado-dot');
  const txt  = document.getElementById('estado-texto');
  const hint = document.getElementById('estado-hint');

  btn.classList.toggle('cerrado', !aceptaPedidos);
  dot.classList.toggle('cerrado', !aceptaPedidos);
  txt.textContent  = aceptaPedidos ? 'Abierto' : 'Pausado';
  hint.textContent = aceptaPedidos ? 'Clic para pausar pedidos' : 'Clic para recibir pedidos';
}

// ---- ESTADÍSTICAS ----
function renderEstadisticas() {
  const entregados  = pedidos.filter(p => p.estado === 'entregado');
  const ventasDia   = entregados.reduce((suma, p) => suma + Number(p.total), 0);
  const pendientes  = pedidos.filter(p => ['nuevo', 'preparando', 'listo'].includes(p.estado)).length;
  const clientesSet = new Set(pedidos.filter(p => p.estado !== 'rechazado').map(p => p.cliente));

  document.getElementById('stat-entregados').textContent = entregados.length;
  document.getElementById('stat-ventas').textContent     = window.UE.formatoCOP(ventasDia);
  document.getElementById('stat-pendientes').textContent = pendientes;
  document.getElementById('stat-clientes').textContent   = clientesSet.size;

  const badgePedidos = document.getElementById('badge-pedidos');
  const nuevos = pedidos.filter(p => p.estado === 'nuevo').length;
  badgePedidos.textContent = nuevos;
  badgePedidos.classList.toggle('visible', nuevos > 0);
}

// ---- TABLA DE PEDIDOS (vista general) ----
function renderTablaClientes() {
  const tbody = document.getElementById('tabla-clientes-body');
  const visibles = pedidos.filter(p => p.estado !== 'rechazado');

  if (visibles.length === 0) {
    tbody.innerHTML = '<tr><td class="tabla-vacia" colspan="7">Aún no hay pedidos hoy</td></tr>';
    return;
  }

  tbody.innerHTML = visibles.map(p => `
    <tr>
      <td><strong>${esc(p.cliente)}</strong></td>
      <td>${esc(p.direccion || '—')}</td>
      <td>${esc(p.telefono || '—')}</td>
      <td>${p.items.map(esc).join(', ')}</td>
      <td><strong>${window.UE.formatoCOP(p.total)}</strong></td>
      <td>${esc(p.hora)}</td>
      <td>${badgeHTML(p)}</td>
    </tr>
  `).join('');
}

// ---- GESTIÓN DE PEDIDOS ----
function renderPedidos() {
  const lista = document.getElementById('pedidos-lista');
  const filtrados = filtroActivo === 'todos'
    ? pedidos
    : pedidos.filter(p => p.estado === filtroActivo);

  if (filtrados.length === 0) {
    lista.innerHTML = `
      <div class="sin-pedidos">
        <i class="fas fa-receipt"></i>
        <p>No hay pedidos en este estado</p>
      </div>`;
    return;
  }

  lista.innerHTML = filtrados.map(p => `
    <div class="pedido-card estado-${esc(p.estado)}">
      <div class="pedido-header">
        <div class="pedido-id-hora">
          <span class="pedido-id">Pedido #${esc(p.codigo)}</span>
          <span class="pedido-hora"><i class="fas fa-clock"></i> ${esc(p.hora)}</span>
        </div>
        <div class="pedido-cliente-total">
          <div class="pedido-cliente"><i class="fas fa-user"></i> ${esc(p.cliente)}</div>
          <div class="pedido-total">${window.UE.formatoCOP(p.total)}</div>
        </div>
        ${badgeHTML(p)}
      </div>
      <div class="pedido-items">
        <strong>Productos:</strong> ${p.items.map(esc).join(' · ')}
      </div>
      <div class="pedido-items"><i class="fas fa-location-dot"></i> ${esc(p.direccion || '—')}</div>
      ${p.repartidor ? `<div class="pedido-repartidor"><i class="fas fa-motorcycle"></i> Lo lleva: ${esc(p.repartidor)}</div>` : ''}
      <div class="pedido-acciones">
        ${accionesHTML(p)}
      </div>
    </div>
  `).join('');

  lista.querySelectorAll('[data-accion]').forEach(btn => {
    btn.addEventListener('click', function () {
      this.disabled = true;
      cambiarEstado(this.dataset.id, this.dataset.accion);
    });
  });
}

function accionesHTML(pedido) {
  const id = esc(pedido.codigo);
  switch (pedido.estado) {
    case 'nuevo':
      return `
        <button class="btn-accion btn-aceptar" data-id="${id}" data-accion="aceptar">
          <i class="fas fa-check"></i> Aceptar y preparar
        </button>
        <button class="btn-accion btn-rechazar" data-id="${id}" data-accion="rechazar">
          <i class="fas fa-times"></i> Rechazar
        </button>`;
    case 'preparando':
      return `
        <button class="btn-accion btn-listo" data-id="${id}" data-accion="listo">
          <i class="fas fa-bag-shopping"></i> Marcar listo para envío
        </button>`;
    case 'listo':
      return `<span class="pedido-sin-acciones"><i class="fas fa-hourglass-half"></i> Esperando que un repartidor lo recoja</span>`;
    case 'encamino':
      return `<a class="btn-accion btn-mapa" href="/rastreo/${id}"><i class="fas fa-map-location-dot"></i> Ver recorrido</a>`;
    default:
      return `<span class="pedido-sin-acciones">Pedido finalizado</span>`;
  }
}

// ---- FILTROS ----
function iniciarFiltros() {
  document.querySelectorAll('.filtro-btn').forEach(btn => {
    btn.addEventListener('click', function () {
      document.querySelectorAll('.filtro-btn').forEach(b => b.classList.remove('activo'));
      this.classList.add('activo');
      filtroActivo = this.dataset.filtro;
      renderPedidos();
    });
  });
}

// ---- TABS ----
function iniciarTabs() {
  document.querySelectorAll('.tab-btn').forEach(btn => {
    btn.addEventListener('click', function () {
      const tab = this.dataset.tab;
      document.querySelectorAll('.tab-btn').forEach(b => b.classList.remove('activo'));
      document.querySelectorAll('.tab-content').forEach(c => c.classList.remove('activo'));
      this.classList.add('activo');
      document.getElementById('tab-' + tab).classList.add('activo');
    });
  });
}

// ---- CONFIRMAR ANTES DE ELIMINAR UN PLATO ----
function iniciarConfirmacionEliminar() {
  document.querySelectorAll('.form-eliminar-plato').forEach(form => {
    form.addEventListener('submit', function (evento) {
      const nombre = this.dataset.nombre || 'este plato';
      if (!confirm(`¿Eliminar «${nombre}»? Esta acción no se puede deshacer.`)) {
        evento.preventDefault();
      }
    });
  });
}

// ---- UTILIDADES ----
function badgeHTML(pedido) {
  const iconos = {
    nuevo: 'fa-bell', preparando: 'fa-fire-burner', listo: 'fa-bag-shopping',
    encamino: 'fa-motorcycle', entregado: 'fa-check-circle', rechazado: 'fa-ban'
  };
  return `<span class="badge-estado badge-${esc(pedido.estado)}">
            <i class="fas ${iconos[pedido.estado] || 'fa-circle'}"></i> ${esc(pedido.estadoEtiqueta)}
          </span>`;
}
