/* ============================
   URBAN EATS - Rastreo JS
   ============================ */

// ---- CARGAR PEDIDO ----
const pedido = JSON.parse(localStorage.getItem('ue_pedido_actual') || 'null');
const pedidoId = pedido ? pedido.id : ('ORD-' + Date.now());

// Mostrar ID del pedido
document.getElementById('pedido-id-label').textContent = `Pedido #${pedidoId}`;

// ---- CARGAR ITEMS EN RESUMEN ----
(function cargarItems() {
  const wrap = document.getElementById('rastreo-items');
  let items = pedido ? pedido.items : [];

  // Si no hay pedido guardado, mostrar demo
  if (!items || items.length === 0) {
    items = [{ nombre: 'Bowl Mediterráneo', cantidad: 1, precio: 18500 }];
  }

  let subtotal = 0;
  let html = '';
  items.forEach(item => {
    const precio = item.precio * item.cantidad;
    subtotal += precio;
    html += `
      <div class="resumen-item">
        <span class="nombre">${item.cantidad}x ${item.nombre}</span>
        <span>$${precio.toLocaleString('es-CO')}</span>
      </div>
    `;
  });
  wrap.innerHTML = html;

  const total = subtotal + 5000;
  document.getElementById('rastreo-total').textContent = `$${total.toLocaleString('es-CO')} COP`;
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
    actualizarAlerta('Estamos muy cerca', 'Carlos está a menos de 5 minutos de tu ubicación.', '#e8f5e9', '#27ae60', 'fa-map-marker-alt');
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

// ---- BOTÓN LLAMAR ----
document.querySelector('.btn-llamar').addEventListener('click', () => {
  window.UE.mostrarToast('Conectando con Carlos Rodríguez...', 'fa-phone');
});
