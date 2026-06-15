/* ============================
   PERFIL RESTAURANTE
   ============================ */

// Datos de demostración — reemplazar con llamadas reales a la API/BD
const PEDIDOS_DEMO = [
  {
    id: 'P-001',
    cliente: 'María García',
    direccion: 'Calle 10 #5-20, Bogotá',
    telefono: '311 234 5678',
    items: ['Bowl de Quinua x1', 'Jugo Verde x2'],
    total: 32000,
    estado: 'nuevo',
    hora: '08:15'
  },
  {
    id: 'P-002',
    cliente: 'Carlos López',
    direccion: 'Carrera 7 #80-15, Bogotá',
    telefono: '320 876 5432',
    items: ['Ensalada Tropical x1', 'Agua x1'],
    total: 18500,
    estado: 'aceptado',
    hora: '08:42'
  },
  {
    id: 'P-003',
    cliente: 'Laura Martínez',
    direccion: 'Av. 68 #23-40, Bogotá',
    telefono: '315 112 3344',
    items: ['Wrap Integral x2', 'Limonada x1'],
    total: 27000,
    estado: 'preparando',
    hora: '09:10'
  },
  {
    id: 'P-004',
    cliente: 'Andrés Ríos',
    direccion: 'Calle 100 #15-60, Bogotá',
    telefono: '300 998 7766',
    items: ['Cazuela Vegana x1'],
    total: 22000,
    estado: 'listo',
    hora: '09:30'
  },
  {
    id: 'P-005',
    cliente: 'Sara Niño',
    direccion: 'Transversal 50 #90-30, Bogotá',
    telefono: '318 445 6677',
    items: ['Bowl de Quinua x2', 'Smoothie x1'],
    total: 41000,
    estado: 'entregado',
    hora: '07:55'
  },
  {
    id: 'P-006',
    cliente: 'Felipe Vargas',
    direccion: 'Calle 26 #60-10, Bogotá',
    telefono: '312 889 0011',
    items: ['Sopa de Lentejas x1'],
    total: 14000,
    estado: 'rechazado',
    hora: '07:30'
  }
];

const RESTAURANTE_DEMO = {
  nombre: 'Green Bowl',
  descripcion: 'Comida saludable y deliciosa para todos',
  direccion: 'Calle 45 #12-30, Bogotá',
  telefono: '310 456 7890'
};

let pedidos = [...PEDIDOS_DEMO];
let abierto = true;
let filtroActivo = 'todos';

// ---- INICIALIZACIÓN ----
document.addEventListener('DOMContentLoaded', function () {
  cargarInfoRestaurante();
  actualizarEstado();
  renderTablaClientes();
  renderEstadisticas();
  renderPedidos();
  iniciarTabs();
  iniciarFiltros();
  document.getElementById('btn-estado').addEventListener('click', toggleEstado);
});

function cargarInfoRestaurante() {
  document.getElementById('rest-nombre').textContent    = RESTAURANTE_DEMO.nombre;
  document.getElementById('rest-descripcion').textContent = RESTAURANTE_DEMO.descripcion;
  document.getElementById('rest-direccion').textContent  = RESTAURANTE_DEMO.direccion;
  document.getElementById('rest-telefono').textContent   = RESTAURANTE_DEMO.telefono;
  document.getElementById('navbar-nombre').textContent   = RESTAURANTE_DEMO.nombre;
}

// ---- TOGGLE ABIERTO/CERRADO ----
function toggleEstado() {
  abierto = !abierto;
  actualizarEstado();
  const msg = abierto ? 'Restaurante abierto' : 'Restaurante cerrado';
  if (window.UE && window.UE.mostrarToast) {
    window.UE.mostrarToast(msg, abierto ? 'fa-door-open' : 'fa-door-closed');
  }
}

function actualizarEstado() {
  const btn  = document.getElementById('btn-estado');
  const dot  = document.getElementById('estado-dot');
  const txt  = document.getElementById('estado-texto');
  const hint = document.getElementById('estado-hint');

  if (abierto) {
    btn.classList.remove('cerrado');
    dot.classList.remove('cerrado');
    txt.textContent  = 'Abierto';
    hint.textContent = 'Clic para cerrar';
  } else {
    btn.classList.add('cerrado');
    dot.classList.add('cerrado');
    txt.textContent  = 'Cerrado';
    hint.textContent = 'Clic para abrir';
  }
}

// ---- ESTADÍSTICAS ----
function renderEstadisticas() {
  const entregados  = pedidos.filter(p => p.estado === 'entregado').length;
  const ventasDia   = pedidos
    .filter(p => p.estado === 'entregado')
    .reduce((sum, p) => sum + p.total, 0);
  const pendientes  = pedidos.filter(p => ['nuevo', 'aceptado', 'preparando', 'listo'].includes(p.estado)).length;
  const clientesSet = new Set(pedidos.filter(p => p.estado !== 'rechazado').map(p => p.cliente));

  document.getElementById('stat-entregados').textContent = entregados;
  document.getElementById('stat-ventas').textContent     = '$' + ventasDia.toLocaleString('es-CO');
  document.getElementById('stat-pendientes').textContent = pendientes;
  document.getElementById('stat-clientes').textContent   = clientesSet.size;

  const badgePedidos = document.getElementById('badge-pedidos');
  const nuevos = pedidos.filter(p => p.estado === 'nuevo').length;
  badgePedidos.textContent = nuevos;
  badgePedidos.classList.toggle('visible', nuevos > 0);
}

// ---- TABLA DE CLIENTES ----
function renderTablaClientes() {
  const tbody = document.getElementById('tabla-clientes-body');
  const visibles = pedidos.filter(p => p.estado !== 'rechazado');

  if (visibles.length === 0) {
    tbody.innerHTML = '<tr><td class="tabla-vacia" colspan="7">Aún no hay clientes hoy</td></tr>';
    return;
  }

  tbody.innerHTML = visibles.map(p => `
    <tr>
      <td><strong>${p.cliente}</strong></td>
      <td>${p.direccion || '—'}</td>
      <td>${p.telefono || '—'}</td>
      <td>${p.items.join(', ')}</td>
      <td><strong>$${p.total.toLocaleString('es-CO')}</strong></td>
      <td>${p.hora}</td>
      <td>${badgeHTML(p.estado)}</td>
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
    <div class="pedido-card estado-${p.estado}" id="card-${p.id}">
      <div class="pedido-header">
        <div class="pedido-id-hora">
          <span class="pedido-id">${p.id}</span>
          <span class="pedido-hora"><i class="fas fa-clock"></i> ${p.hora}</span>
        </div>
        <div class="pedido-cliente-total">
          <div class="pedido-cliente"><i class="fas fa-user"></i> ${p.cliente}</div>
          <div class="pedido-total">$${p.total.toLocaleString('es-CO')}</div>
        </div>
        ${badgeHTML(p.estado)}
      </div>
      <div class="pedido-items">
        <strong>Productos:</strong> ${p.items.join(' · ')}
      </div>
      <div class="pedido-acciones">
        ${accionesHTML(p)}
      </div>
    </div>
  `).join('');

  // Eventos de botones
  lista.querySelectorAll('[data-accion]').forEach(btn => {
    btn.addEventListener('click', function () {
      cambiarEstado(this.dataset.id, this.dataset.accion);
    });
  });
}

function accionesHTML(pedido) {
  const { id, estado } = pedido;
  switch (estado) {
    case 'nuevo':
      return `
        <button class="btn-accion btn-aceptar" data-id="${id}" data-accion="aceptado">
          <i class="fas fa-check"></i> Aceptar
        </button>
        <button class="btn-accion btn-rechazar" data-id="${id}" data-accion="rechazado">
          <i class="fas fa-times"></i> Rechazar
        </button>`;
    case 'aceptado':
      return `
        <button class="btn-accion btn-preparando" data-id="${id}" data-accion="preparando">
          <i class="fas fa-fire-burner"></i> Preparando
        </button>`;
    case 'preparando':
      return `
        <button class="btn-accion btn-listo" data-id="${id}" data-accion="listo">
          <i class="fas fa-bag-shopping"></i> Listo para recoger
        </button>`;
    case 'listo':
      return `
        <button class="btn-accion btn-listo" data-id="${id}" data-accion="entregado">
          <i class="fas fa-check-double"></i> Marcar entregado
        </button>`;
    default:
      return `<span class="pedido-sin-acciones">Pedido finalizado</span>`;
  }
}

function cambiarEstado(id, nuevoEstado) {
  const pedido = pedidos.find(p => p.id === id);
  if (!pedido) return;
  pedido.estado = nuevoEstado;

  renderPedidos();
  renderTablaClientes();
  renderEstadisticas();

  const msgs = {
    aceptado:  'Pedido aceptado',
    rechazado: 'Pedido rechazado',
    preparando: 'Pedido en preparación',
    listo:     'Listo para recoger',
    entregado: 'Pedido entregado'
  };
  if (window.UE && window.UE.mostrarToast) {
    window.UE.mostrarToast(msgs[nuevoEstado] || 'Estado actualizado', 'fa-check');
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

// ---- UTILIDADES ----
function badgeHTML(estado) {
  const labels = {
    nuevo:      '<i class="fas fa-bell"></i> Nuevo',
    aceptado:   '<i class="fas fa-thumbs-up"></i> Aceptado',
    preparando: '<i class="fas fa-fire-burner"></i> Preparando',
    listo:      '<i class="fas fa-bag-shopping"></i> Listo',
    entregado:  '<i class="fas fa-check-circle"></i> Entregado',
    rechazado:  '<i class="fas fa-ban"></i> Rechazado'
  };
  return `<span class="badge-estado badge-${estado}">${labels[estado] || estado}</span>`;
}
