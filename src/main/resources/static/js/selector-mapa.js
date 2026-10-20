/* ============================
   Selector de ubicacion en el mapa (Leaflet + OpenStreetMap)
   ============================
   Se usa para ubicar una cocina o una direccion de entrega: con un clic se pone
   el punto y sus coordenadas quedan en dos campos ocultos del formulario.
*/
function iniciarSelectorMapa(idMapa, idLatitud, idLongitud, idTexto) {
  const campoLat = document.getElementById(idLatitud);
  const campoLng = document.getElementById(idLongitud);
  const texto = document.getElementById(idTexto);
  if (typeof L === 'undefined') {
    texto.textContent = 'No se pudo cargar el mapa (sin conexión a internet).';
    return;
  }

  const inicial = campoLat.value && campoLng.value ? [Number(campoLat.value), Number(campoLng.value)] : null;
  const mapa = L.map(idMapa).setView(inicial || [4.6486, -74.0880], inicial ? 16 : 12);
  L.tileLayer('https://tile.openstreetmap.org/{z}/{x}/{y}.png', {
    maxZoom: 19,
    attribution: '&copy; colaboradores de <a href="https://www.openstreetmap.org/copyright">OpenStreetMap</a>'
  }).addTo(mapa);

  let marcador = null;
  function poner(lat, lng) {
    // 7 decimales = precision de ~1 cm (la columna de la base es DECIMAL(10,7)).
    campoLat.value = lat.toFixed(7);
    campoLng.value = lng.toFixed(7);
    if (marcador) marcador.setLatLng([lat, lng]);
    else marcador = L.marker([lat, lng], { draggable: true }).addTo(mapa)
      .on('dragend', e => poner(e.target.getLatLng().lat, e.target.getLatLng().lng));
    texto.textContent = `Ubicación: ${lat.toFixed(5)}, ${lng.toFixed(5)} (puedes arrastrar el punto).`;
  }
  if (inicial) poner(inicial[0], inicial[1]);
  mapa.on('click', e => poner(e.latlng.lat, e.latlng.lng));
}
