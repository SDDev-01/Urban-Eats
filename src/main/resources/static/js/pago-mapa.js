/* ============================
    URBAN EATS - Mapa de la entrega (pagina de pago)
   ============================
   Muestra en el mapa la cocina del carrito y el punto de la direccion elegida.
   - Si la direccion no tiene punto, el cliente lo marca con un clic.
   - El punto se puede arrastrar para corregirlo.
   Cada cambio se guarda de una vez en POST /perfil/direccion/{id}/ubicacion, porque
   sin ese punto la cocina y el repartidor no pueden calcular la ruta del domicilio.
   pago.js llama a window.MapaEntrega.ponerCocina() cuando el servidor le da el resumen.
*/
(function () {
  const BOGOTA = [4.6486, -74.0880];
  const select = document.getElementById('direccion-entrega');
  const contenedor = document.getElementById('mapa-entrega');
  const texto = document.getElementById('mapa-entrega-texto');
  let mapa = null;
  let marcadorCocina = null;
  let marcadorEntrega = null;

  window.MapaEntrega = { ponerCocina: () => {} };
  if (!select || !contenedor) return;
  if (typeof L === 'undefined') {
    texto.textContent = 'No se pudo cargar el mapa (sin conexión a internet).';
    return;
  }

  mapa = L.map(contenedor).setView(BOGOTA, 12);
  L.tileLayer('https://tile.openstreetmap.org/{z}/{x}/{y}.png', {
    maxZoom: 19,
    attribution: '&copy; colaboradores de <a href="https://www.openstreetmap.org/copyright">OpenStreetMap</a>'
  }).addTo(mapa);
  mapa.on('click', (e) => guardarPunto(e.latlng.lat, e.latlng.lng));
  select.addEventListener('change', mostrarDireccion);
  mostrarDireccion();

  window.MapaEntrega.ponerCocina = function (lat, lng, nombre) {
    if (lat == null || lng == null) return;
    const icono = L.divIcon({ className: 'icono-cocina', html: '<i class="fas fa-store" style="color:#a42828;font-size:1.6rem;"></i>', iconSize: [28, 28], iconAnchor: [14, 28] });
    marcadorCocina = L.marker([Number(lat), Number(lng)], { icon: icono }).addTo(mapa).bindPopup('Cocina: ' + nombre);
    encuadrar();
  };

  function opcionElegida() {
    return select.options[select.selectedIndex];
  }

  function mostrarDireccion() {
    const opcion = opcionElegida();
    const lat = opcion.dataset.lat;
    const lng = opcion.dataset.lng;
    if (lat && lng) {
      ponerMarcadorEntrega(Number(lat), Number(lng));
      texto.classList.remove('falta-punto');
      texto.textContent = 'Aquí se entrega. Si el punto no es exacto, arrástralo o haz clic en el lugar correcto.';
    } else {
      if (marcadorEntrega) { mapa.removeLayer(marcadorEntrega); marcadorEntrega = null; }
      texto.classList.add('falta-punto');
      texto.textContent = 'Esta dirección no tiene ubicación: haz clic en el mapa donde debemos entregar.';
    }
    encuadrar();
  }

  function ponerMarcadorEntrega(lat, lng) {
    if (marcadorEntrega) {
      marcadorEntrega.setLatLng([lat, lng]);
    } else {
      marcadorEntrega = L.marker([lat, lng], { draggable: true }).addTo(mapa)
        .on('dragend', (e) => guardarPunto(e.target.getLatLng().lat, e.target.getLatLng().lng));
    }
    marcadorEntrega.bindPopup(opcionElegida().dataset.texto || 'Entrega');
  }

  function encuadrar() {
    const puntos = [marcadorCocina, marcadorEntrega].filter(Boolean).map(m => m.getLatLng());
    if (puntos.length === 2) mapa.fitBounds(L.latLngBounds(puntos), { padding: [40, 40] });
    else if (puntos.length === 1) mapa.setView(puntos[0], 15);
  }

  async function guardarPunto(lat, lng) {
    const opcion = opcionElegida();
    ponerMarcadorEntrega(lat, lng);
    texto.classList.remove('falta-punto');
    texto.textContent = 'Guardando la ubicación...';
    try {
      const respuesta = await fetch(`/perfil/direccion/${opcion.value}/ubicacion`, {
        method: 'POST',
        headers: window.UE.encabezadosCSRF(),
        body: JSON.stringify({ lat: Number(lat.toFixed(7)), lng: Number(lng.toFixed(7)) })
      });
      const datos = await window.UE.leerRespuestaJson(respuesta);
      if (!respuesta.ok) throw new Error(datos.error || 'No se pudo guardar la ubicación.');
      opcion.dataset.lat = datos.lat;
      opcion.dataset.lng = datos.lng;
      opcion.textContent = opcion.dataset.texto;
      texto.textContent = 'Ubicación guardada. La cocina y el repartidor verán este punto.';
      encuadrar();
    } catch (e) {
      texto.classList.add('falta-punto');
      texto.textContent = e.message;
      mostrarDireccionGuardada(opcion);
    }
  }

  /** Si no se pudo guardar, vuelve al punto que tenia (o lo quita). */
  function mostrarDireccionGuardada(opcion) {
    if (opcion.dataset.lat && opcion.dataset.lng) {
      ponerMarcadorEntrega(Number(opcion.dataset.lat), Number(opcion.dataset.lng));
    } else if (marcadorEntrega) {
      mapa.removeLayer(marcadorEntrega);
      marcadorEntrega = null;
    }
  }
})();
