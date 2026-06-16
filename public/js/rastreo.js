/* ============================
   URBAN EATS - Rastreo JS
   ============================ */

// ID del pedido actual (generado en esta sesión)
const pedidoId = 'ORD-' + Date.now();

// Mostrar ID del pedido
document.getElementById('pedido-id-label').textContent = `Pedido #${pedidoId}`;

// ---- CARGAR ITEMS EN RESUMEN DE TOTALES ----
(function cargarItems() {
  const wrap = document.getElementById('rastreo-items');
  const pedido = window.PEDIDO_BD;

  // Priorizar datos del servidor; caer en localStorage si no hay pedido en sesión
  const items = pedido
    ? pedido.items.map(i => ({ nombre: i.nombre, cantidad: i.cantidad, precio: i.precio_unitario, subtotal: i.subtotal }))
    : (window.UE ? window.UE.obtenerCarrito().map(i => ({ nombre: i.nombre, cantidad: i.cantidad, precio: i.precio, subtotal: i.precio * i.cantidad })) : []);

  let total = 0;
  let html = '';
  items.forEach(item => {
    total += item.subtotal;
    html += `
      <div class="resumen-item">
        <span class="nombre">${item.cantidad}x ${item.nombre}</span>
        <span>$${item.subtotal.toLocaleString('es-CO')}</span>
      </div>
    `;
  });
  wrap.innerHTML = html || '<p style="color:var(--texto-gris);font-size:0.85rem;">Sin productos.</p>';
  document.getElementById('rastreo-total').textContent = `$${total.toLocaleString('es-CO')} COP`;
})();

// ---- RESUMEN DEL ENVÍO (con descripción) ----
(function cargarResumenEnvio() {
  const wrap = document.getElementById('envio-items');
  if (!wrap) return;

  const pedido = window.PEDIDO_BD;

  // Priorizar datos del servidor
  const items = pedido
    ? pedido.items
    : (window.UE ? window.UE.obtenerCarrito().map(i => ({ nombre: i.nombre, descripcion: i.descripcion ?? '', cantidad: i.cantidad, subtotal: i.precio * i.cantidad })) : []);

  if (items.length === 0) {
    wrap.innerHTML = '<p style="color:var(--texto-gris);font-size:0.85rem;">No hay productos en este pedido.</p>';
    return;
  }

  let html = '';
  items.forEach(item => {
    const subtotal = item.subtotal ?? (item.precio_unitario * item.cantidad);
    html += `
      <div style="padding:0.85rem 0; border-bottom:1px solid var(--borde);">
        <div style="display:flex;justify-content:space-between;align-items:center;margin-bottom:0.3rem;">
          <span style="font-weight:700;font-size:0.9rem;">${item.cantidad}x ${item.nombre}</span>
          <span style="font-weight:700;color:var(--terciario);font-size:0.875rem;">$${subtotal.toLocaleString('es-CO')}</span>
        </div>
        ${item.descripcion ? `<p style="font-size:0.8rem;color:var(--texto-gris);margin:0;">${item.descripcion}</p>` : ''}
      </div>
    `;
  });
  wrap.innerHTML = html;
})();

// ---- PASOS DEL PEDIDO ----
const pasos = [
  { id: 1, titulo: 'Preparando',  tiempo: '0-10 min',   icono: 'fa-fire' },
  { id: 2, titulo: 'En camino',   tiempo: '10-25 min',  icono: 'fa-motorcycle' },
  { id: 3, titulo: 'Muy cerca',   tiempo: '25-35 min',  icono: 'fa-map-marker-alt' },
  { id: 4, titulo: 'Entregado',   tiempo: '35 min',     icono: 'fa-check-circle' },
];

// Comenzar en paso 2 (en camino) para que se vea el estado activo del wireframe
let pasoActual = 2;

function renderizarPasos() {
  const wrap = document.getElementById('pasos-pedido');
  let html = '';

  pasos.forEach((paso, index) => {
    const esCompletado = paso.id < pasoActual;
    const esActivo     = paso.id === pasoActual;
    const esPendiente  = paso.id > pasoActual;

    let claseIcono = 'pendiente';
    let contenidoIcono = paso.id;

    if (esCompletado) { claseIcono = 'completado'; contenidoIcono = '<i class="fas fa-check"></i>'; }
    if (esActivo)     { claseIcono = 'activo';     contenidoIcono = `<i class="fas ${paso.icono}"></i>`; }

    html += `
      <div class="paso">
        <div class="paso-icono ${claseIcono}">${contenidoIcono}</div>
        <div class="paso-info">
          <h4 class="${esPendiente ? 'inactivo' : ''}">${paso.titulo}</h4>
          <span>${paso.tiempo}</span>
        </div>
      </div>
    `;

    // Línea separadora entre pasos (excepto el último)
    if (index < pasos.length - 1) {
      html += `<div class="paso-linea"></div>`;
    }
  });

  wrap.innerHTML = html;
}

// ---- TEMPORIZADOR ----
// Empezar en 35 minutos (2100 segundos) y contar hacia atrás
let segundosTotales = 2100;

function actualizarTimer() {
  const minutos  = Math.floor(segundosTotales / 60);
  const segundos = segundosTotales % 60;
  const display  = `${String(minutos).padStart(2, '0')}:${String(segundos).padStart(2, '0')}`;
  document.getElementById('timer-display').textContent = display;

  // Avanzar pasos automáticamente según el tiempo restante
  if (segundosTotales <= 600 && pasoActual < 3) {
    pasoActual = 3;
    renderizarPasos();
    actualizarAlerta('Estamos muy cerca', 'El repartidor está a menos de 5 minutos de tu ubicación.', '#e8f5e9', '#27ae60', 'fa-map-marker-alt');
  }
  if (segundosTotales <= 0) {
    pasoActual = 4;
    renderizarPasos();
    document.getElementById('timer-display').textContent = '00:00';
    actualizarAlerta('¡Pedido entregado!', 'Tu pedido fue entregado exitosamente.', '#e8f5e9', '#27ae60', 'fa-check-circle');
    clearInterval(intervaloTimer);
  }
}

function actualizarAlerta(titulo, texto, fondo, color, icono) {
  const alerta = document.querySelector('.alerta-estado');
  if (!alerta) return;
  alerta.style.background = fondo;
  alerta.innerHTML = `
    <i class="fas ${icono}" style="color:${color}; font-size:1.1rem; margin-top:0.1rem;"></i>
    <div>
      <h4 style="color:${color};">${titulo}</h4>
      <p>${texto}</p>
    </div>
  `;
}

// Iniciar
renderizarPasos();
actualizarTimer();

const intervaloTimer = setInterval(() => {
  if (segundosTotales > 0) {
    segundosTotales--;
    actualizarTimer();
  }
}, 1000);

