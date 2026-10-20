/* ============================
   URBAN EATS - Rastreo JS
   ============================
   Muestra el estado REAL del pedido (antes era un contador falso de 5 minutos):
   - Cada 5 segundos consulta GET /rastreo/{id}/datos hasta que el pedido termine.
   - Dibuja el mapa con Leaflet + OpenStreetMap: cocina, direccion de entrega y la
     ultima posicion del repartidor (GPS del celular o simulacion de la demo).
   - La ruta por calles la calcula el servicio Python con OpenRouteService
     (GET /rastreo/{id}/ruta). Si no esta disponible, se dibuja una linea recta.
*/

const ID_PEDIDO = window.CODIGO_PEDIDO;
const INTERVALO_MS = 5000;
const CENTRO_BOGOTA = [4.6486, -74.0880];
const esc = (texto) => window.UE.escaparHTML(texto);

const PASOS = [
  { paso: 1, titulo: 'Solicitado',        texto: 'La cocina recibió tu pedido',     icono: 'fa-receipt' },
  { paso: 2, titulo: 'En preparación',    texto: 'Están cocinando tu comida',       icono: 'fa-fire-burner' },
  { paso: 3, titulo: 'Listo para envío',  texto: 'Esperando al repartidor',         icono: 'fa-bag-shopping' },
  { paso: 4, titulo: 'En camino',         texto: 'El repartidor va hacia ti',       icono: 'fa-motorcycle' },
  { paso: 5, titulo: 'Entregado',         texto: '¡Buen provecho!',                 icono: 'fa-check-circle' },
];

let mapa = null;
let marcadorCocina = null;
let marcadorDestino = null;
let marcadorRepartidor = null;
let rutaDibujada = false;
let encuadreHecho = false;
let itemsPintados = false;
let temporizador = null;

// ---- ARRANQUE ----
iniciarMapa();
actualizar();
temporizador = setInterval(actualizar, INTERVALO_MS);

document.getElementById('btn-cancelar-pedido').addEventListener('click', cancelarPedido);

async function actualizar() {
  try {
    const respuesta = await fetch(`/rastreo/${ID_PEDIDO}/datos`, { headers: { 'Accept': 'application/json' } });
    if (!respuesta.ok) throw new Error('HTTP ' + respuesta.status);
    const datos = await respuesta.json();

    pintarEstado(datos);
    pintarPasos(datos.paso);
    pintarRepartidor(datos);
    pintarHistorial(datos.historial);
    if (!itemsPintados) pintarItems(datos);
    pintarMapa(datos);

    document.getElementById('btn-cancelar-pedido').style.display = datos.puedeCancelar ? 'inline-flex' : 'none';

    // Entregado o cancelado: ya no cambia, se deja de consultar.
    if (datos.paso === 5 || datos.paso === 0) {
      clearInterval(temporizador);
    }
  } catch (e) {
    document.getElementById('detalle-estado').textContent = 'Sin conexión. Reintentando...';
  }
}

// ---- ESTADO PRINCIPAL ----
function pintarEstado(datos) {
  const tarjeta = document.getElementById('tarjeta-estado');
  const icono = document.getElementById('icono-estado');
  const info = PASOS.find(p => p.paso === datos.paso);

  document.getElementById('texto-estado').textContent = datos.estadoEtiqueta;
  if (datos.paso === 0) {
    tarjeta.style.background = 'linear-gradient(135deg,#ffebee,#ffcdd2)';
    icono.className = 'fas fa-times-circle';
    const ultimo = datos.historial[datos.historial.length - 1];
    document.getElementById('detalle-estado').textContent = ultimo?.comentario || 'El pedido fue cancelado.';
    return;
  }
  icono.className = 'fas ' + (info ? info.icono : 'fa-receipt');
  let detalle = info ? info.texto : '';
  if (datos.paso < 5 && datos.horaEstimada) detalle += ` · Llegada estimada: ${datos.horaEstimada}`;
  document.getElementById('detalle-estado').textContent = detalle;
}

function pintarPasos(pasoActual) {
  const wrap = document.getElementById('pasos-pedido');
  wrap.innerHTML = PASOS.map((paso, indice) => {
    const completado = pasoActual > paso.paso || pasoActual === 5;
    const activo = pasoActual === paso.paso && pasoActual !== 5;
    let clase = 'pendiente';
    let contenido = paso.paso;
    if (completado) { clase = 'completado'; contenido = '<i class="fas fa-check"></i>'; }
    if (activo)     { clase = 'activo';     contenido = `<i class="fas ${paso.icono}"></i>`; }
    const linea = indice < PASOS.length - 1 ? '<div class="paso-linea"></div>' : '';
    return `<div class="paso">
        <div class="paso-icono ${clase}">${contenido}</div>
        <div class="paso-info">
          <h4 class="${!completado && !activo ? 'inactivo' : ''}">${paso.titulo}</h4>
          <span>${paso.texto}</span>
        </div>
      </div>${linea}`;
  }).join('');
}

function pintarRepartidor(datos) {
  const caja = document.getElementById('repartidor-info');
  if (!datos.repartidor) {
    caja.style.display = 'none';
    return;
  }
  caja.style.display = 'block';
  const ubicacion = datos.repartidor.actualizado
    ? `Última ubicación: ${esc(datos.repartidor.actualizado)}`
    : 'Aún no comparte su ubicación';
  caja.innerHTML = `
    <div class="alerta-estado" style="background:var(--terciario-claro);margin-top:1rem;">
      <i class="fas fa-motorcycle" style="color:#b8431f;font-size:1.1rem;margin-top:0.1rem;"></i>
      <div><h4 style="color:#b8431f;">${esc(datos.repartidor.nombre)}</h4>
      <p>${esc(datos.repartidor.vehiculo)} · ${ubicacion}</p></div>
    </div>`;
}

function pintarHistorial(historial) {
  document.getElementById('historial-pedido').innerHTML = historial.map(hito => `
    <li><span class="hora-hito">${esc(hito.hora)}</span>
        <strong>${esc(hito.estado)}</strong>
        <span class="comentario-hito">${esc(hito.comentario || '')}</span></li>`).join('');
}

function pintarItems(datos) {
  itemsPintados = true;
  document.getElementById('rastreo-cocina').innerHTML = `<i class="fas fa-store"></i> ${esc(datos.cocina.nombre)} · ${esc(datos.fecha)} ${esc(datos.hora)}`;
  document.getElementById('rastreo-items').innerHTML = datos.items.map(item => `
    <div class="resumen-item">
      <span class="nombre">${esc(item.cantidad)}x ${esc(item.nombre)}</span>
      <span>${window.UE.formatoCOP(item.subtotal)}</span>
    </div>`).join('') || '<p style="color:var(--texto-gris);font-size:0.85rem;">Sin productos.</p>';
  document.getElementById('rastreo-total').textContent = window.UE.formatoCOP(datos.total) + ' COP';
}

// ---- MAPA ----
function iniciarMapa() {
  if (typeof L === 'undefined') {
    document.getElementById('mapa-info').textContent = 'No se pudo cargar el mapa (sin conexión a internet).';
    return;
  }
  mapa = L.map('mapa', { zoomControl: true }).setView(CENTRO_BOGOTA, 12);
  L.tileLayer('https://tile.openstreetmap.org/{z}/{x}/{y}.png', {
    maxZoom: 19,
    attribution: '&copy; colaboradores de <a href="https://www.openstreetmap.org/copyright">OpenStreetMap</a>'
  }).addTo(mapa);
}

function icono(clase, simbolo) {
  return L.divIcon({ className: 'marcador-mapa ' + clase, html: `<i class="fas ${simbolo}"></i>`,
                     iconSize: [34, 34], iconAnchor: [17, 17] });
}

function aLatLng(punto) {
  return punto && punto.lat != null && punto.lng != null ? [Number(punto.lat), Number(punto.lng)] : null;
}

function pintarMapa(datos) {
  if (!mapa) return;
  const cocina = aLatLng(datos.cocina);
  const destino = aLatLng(datos.destino);
  const repartidor = datos.repartidor ? aLatLng(datos.repartidor) : null;

  if (cocina && !marcadorCocina) {
    marcadorCocina = L.marker(cocina, { icon: icono('cocina', 'fa-store') }).addTo(mapa)
      .bindPopup(`<strong>${esc(datos.cocina.nombre)}</strong><br>${esc(datos.cocina.direccion || '')}`);
  }
  if (destino && !marcadorDestino) {
    marcadorDestino = L.marker(destino, { icon: icono('destino', 'fa-house') }).addTo(mapa)
      .bindPopup(`<strong>Entrega</strong><br>${esc(datos.destino.direccion || '')}`);
  }
  if (repartidor) {
    if (!marcadorRepartidor) {
      marcadorRepartidor = L.marker(repartidor, { icon: icono('repartidor', 'fa-motorcycle'), zIndexOffset: 1000 })
        .addTo(mapa).bindPopup(`<strong>${esc(datos.repartidor.nombre)}</strong>`);
    } else {
      marcadorRepartidor.setLatLng(repartidor);
    }
  }

  if (cocina && destino && !rutaDibujada) {
    rutaDibujada = true;
    dibujarRuta(cocina, destino);
  }

  if (!encuadreHecho) {
    const puntos = [cocina, destino, repartidor].filter(Boolean);
    if (puntos.length >= 2) {
      mapa.fitBounds(L.latLngBounds(puntos), { padding: [40, 40] });
      encuadreHecho = true;
    } else if (puntos.length === 1) {
      mapa.setView(puntos[0], 15);
    }
    if (!destino) {
      document.getElementById('mapa-info').textContent =
        'La dirección de entrega no tiene ubicación en el mapa. Agrégala desde tu perfil.';
    }
  }
}

/** Ruta por calles (servicio Python + OpenRouteService) o, si falla, linea recta. */
async function dibujarRuta(cocina, destino) {
  const info = document.getElementById('mapa-info');
  try {
    const respuesta = await fetch(`/rastreo/${ID_PEDIDO}/ruta`);
    if (!respuesta.ok) throw new Error('sin ruta');
    const ruta = await respuesta.json();
    L.polyline(ruta.coordenadas, { color: '#a42828', weight: 5, opacity: 0.85 }).addTo(mapa);
    const km = (ruta.distanciaMetros / 1000).toFixed(1);
    const minutos = Math.round(ruta.duracionSegundos / 60);
    info.textContent = ruta.proveedor === 'openrouteservice'
      ? `Ruta por calles: ${km} km · unos ${minutos} min en moto (OpenRouteService).`
      : `Distancia aproximada: ${km} km · unos ${minutos} min (línea recta: la ruta por calles no está disponible).`;
  } catch (e) {
    L.polyline([cocina, destino], { color: '#a42828', weight: 3, dashArray: '8 8' }).addTo(mapa);
    const km = (distanciaKm(cocina, destino)).toFixed(1);
    info.textContent = `Distancia en línea recta: ${km} km (el cálculo de ruta por calles no está disponible).`;
  }
}

/** Distancia entre dos puntos en km (formula de Haversine). */
function distanciaKm([lat1, lng1], [lat2, lng2]) {
  const radio = 6371;
  const rad = (grados) => grados * Math.PI / 180;
  const dLat = rad(lat2 - lat1);
  const dLng = rad(lng2 - lng1);
  const a = Math.sin(dLat / 2) ** 2 + Math.cos(rad(lat1)) * Math.cos(rad(lat2)) * Math.sin(dLng / 2) ** 2;
  return 2 * radio * Math.asin(Math.sqrt(a));
}

// ---- CANCELAR (solo el cliente y solo mientras la cocina no lo acepta) ----
async function cancelarPedido() {
  if (!confirm('¿Cancelar el pedido? Solo es posible mientras la cocina no lo haya aceptado.')) return;
  try {
    const respuesta = await fetch(`/pedido/${ID_PEDIDO}/cancelar`, { method: 'POST', headers: window.UE.encabezadosCSRF() });
    const datos = await respuesta.json();
    window.UE.mostrarToast(respuesta.ok ? 'Pedido cancelado.' : datos.error, respuesta.ok ? 'fa-check' : 'fa-exclamation-circle');
  } catch (e) {
    window.UE.mostrarToast('No se pudo cancelar. Intenta de nuevo.', 'fa-exclamation-circle');
  }
  actualizar();
}
