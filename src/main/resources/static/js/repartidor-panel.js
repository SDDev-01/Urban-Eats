/* ============================
   PANEL DEL REPARTIDOR
   ============================
   1) Compartir GPS: usa la ubicacion real del celular (navigator.geolocation) y la
      envia cada 5 segundos a POST /repartidor/ubicacion. El cliente la ve en su mapa.
      Nota: el navegador solo entrega el GPS en paginas seguras (https o localhost).
   2) Simular recorrido: para la demostracion en un solo computador. Toma la ruta
      cocina -> cliente y avanza el punto cada 2 segundos, guardandolo en la base
      igual que lo haria el GPS. Asi el cliente ve la moto moverse en el mapa.
   3) Si no hay pedidos en camino, la pagina se recarga cada 20 segundos para que
      aparezcan los pedidos que las cocinas van marcando como listos.
*/

const INTERVALO_GPS_MS = 5000;
const INTERVALO_SIMULACION_MS = 2000;
const PUNTOS_SIMULACION = 40;

let seguimientoActivo = false;

document.querySelectorAll('.tarjeta-en-camino').forEach(tarjeta => {
  const envioId = tarjeta.dataset.envio;
  const pedidoId = tarjeta.dataset.pedido;
  const texto = tarjeta.querySelector('.texto-ubicacion');

  tarjeta.querySelector('.btn-gps')?.addEventListener('click', (evento) => {
    compartirGPS(envioId, texto, evento.currentTarget);
  });
  // Navegacion por voz hasta la entrega (Google Maps o Waze en el celular)
  const enlaces = window.UE.enlacesNavegacion(tarjeta.dataset.lat, tarjeta.dataset.lng,
    tarjeta.dataset.direccion, tarjeta.dataset.bici === 'true');
  const google = tarjeta.querySelector('.btn-google-maps');
  const waze = tarjeta.querySelector('.btn-waze');
  if (google) google.href = enlaces.google;
  if (waze) waze.href = enlaces.waze;
  tarjeta.querySelector('.btn-simular')?.addEventListener('click', (evento) => {
    simularRecorrido(envioId, pedidoId, texto, evento.currentTarget);
  });
});

// ---- Envia una posicion al servidor ----
async function enviarUbicacion(envioId, lat, lng) {
  const respuesta = await fetch('/repartidor/ubicacion', {
    method: 'POST',
    headers: window.UE.encabezadosCSRF(),
    body: JSON.stringify({ envioId: Number(envioId), lat, lng })
  });
  if (!respuesta.ok) {
    const datos = await window.UE.leerRespuestaJson(respuesta);
    throw new Error(datos.error || 'No se pudo guardar la ubicación');
  }
}

// ---- 1) GPS real del celular ----
function compartirGPS(envioId, texto, boton) {
  if (!('geolocation' in navigator)) {
    texto.textContent = 'Este navegador no tiene GPS.';
    return;
  }
  seguimientoActivo = true;
  boton.disabled = true;
  texto.textContent = 'Buscando señal GPS...';

  let ultimoEnvio = 0;
  navigator.geolocation.watchPosition(async (posicion) => {
    const ahora = Date.now();
    if (ahora - ultimoEnvio < INTERVALO_GPS_MS) return; // no saturar el servidor
    ultimoEnvio = ahora;
    try {
      await enviarUbicacion(envioId, posicion.coords.latitude, posicion.coords.longitude);
      texto.textContent = `GPS compartido (precisión ±${Math.round(posicion.coords.accuracy)} m) · ${new Date().toLocaleTimeString('es-CO')}`;
    } catch (e) {
      texto.textContent = e.message;
    }
  }, (error) => {
    texto.textContent = error.code === 1
      ? 'Permiso de ubicación denegado. Usa "Simular recorrido" para la demo.'
      : 'No se pudo leer el GPS (' + error.message + ').';
    boton.disabled = false;
  }, { enableHighAccuracy: true, maximumAge: 3000, timeout: 15000 });
}

// ---- 2) Simulacion del recorrido sobre la ruta ----
async function simularRecorrido(envioId, pedidoId, texto, boton) {
  seguimientoActivo = true;
  boton.disabled = true;
  texto.textContent = 'Calculando ruta...';

  let puntos;
  try {
    puntos = await obtenerPuntosRuta(pedidoId);
  } catch (e) {
    texto.textContent = 'No se pudo calcular la ruta: ' + e.message;
    boton.disabled = false;
    return;
  }

  let indice = 0;
  const temporizador = setInterval(async () => {
    if (indice >= puntos.length) {
      clearInterval(temporizador);
      texto.textContent = 'Llegaste al destino. Marca el pedido como entregado.';
      boton.disabled = false;
      return;
    }
    const [lat, lng] = puntos[indice];
    try {
      await enviarUbicacion(envioId, lat, lng);
      texto.textContent = `Simulando recorrido: punto ${indice + 1} de ${puntos.length}`;
    } catch (e) {
      clearInterval(temporizador);
      texto.textContent = e.message;
      boton.disabled = false;
    }
    indice++;
  }, INTERVALO_SIMULACION_MS);
}

/**
 * Puntos [lat, lng] desde la cocina hasta el cliente.
 * Primero intenta la ruta real por calles (servicio Python + OpenRouteService);
 * si no esta disponible, usa una linea recta entre los dos puntos.
 */
async function obtenerPuntosRuta(pedidoId) {
  try {
    const respuesta = await fetch(`/rastreo/${pedidoId}/ruta`);
    if (respuesta.ok) {
      const ruta = await respuesta.json();
      if (ruta.coordenadas && ruta.coordenadas.length > 1) {
        return reducirPuntos(ruta.coordenadas, PUNTOS_SIMULACION);
      }
    }
  } catch (_) { /* sin ruta por calles: se usa la linea recta */ }

  const respuesta = await fetch(`/rastreo/${pedidoId}/datos`);
  if (!respuesta.ok) throw new Error('pedido no encontrado');
  const datos = await respuesta.json();
  if (!datos.cocina?.lat || !datos.destino?.lat) {
    throw new Error('la cocina o la dirección de entrega no tienen ubicación en el mapa');
  }
  const inicio = [Number(datos.cocina.lat), Number(datos.cocina.lng)];
  const fin = [Number(datos.destino.lat), Number(datos.destino.lng)];
  return Array.from({ length: PUNTOS_SIMULACION }, (_, i) => {
    const t = i / (PUNTOS_SIMULACION - 1);
    return [inicio[0] + (fin[0] - inicio[0]) * t, inicio[1] + (fin[1] - inicio[1]) * t];
  });
}

/** Toma como maximo "cantidad" puntos repartidos a lo largo de la ruta. */
function reducirPuntos(puntos, cantidad) {
  if (puntos.length <= cantidad) return puntos;
  const paso = (puntos.length - 1) / (cantidad - 1);
  return Array.from({ length: cantidad }, (_, i) => puntos[Math.round(i * paso)]);
}

// ---- 3) Recarga automatica mientras no haya un seguimiento en curso ----
setInterval(() => {
  const hayEnCamino = document.querySelectorAll('.tarjeta-en-camino').length > 0;
  if (!seguimientoActivo && !hayEnCamino) {
    window.location.reload();
  }
}, 20000);
