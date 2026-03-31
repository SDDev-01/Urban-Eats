/* ============================
   CARGAR DATOS DEL PERFIL
   ============================ */
(function cargarPerfil() {
  const cliente = JSON.parse(localStorage.getItem('ue_cliente') || 'null');
  
  if (!cliente || !cliente.nombres) {
    // Si no hay datos del cliente, mostrar valores por defecto
    document.getElementById('perfil-nombre').textContent = 'Usuario';
    document.getElementById('info-nombres').textContent = 'No registrado';
    document.getElementById('info-apellidos').textContent = 'No registrado';
    document.getElementById('info-email').textContent = 'No registrado';
    document.getElementById('info-telefono').textContent = 'No registrado';
    document.getElementById('info-direccion').textContent = 'No registrado';
    return;
  }

  // Mostrar datos del cliente
  const nombreCompleto = (cliente.nombres + ' ' + (cliente.apellidos || '')).trim();
  document.getElementById('perfil-nombre').textContent = nombreCompleto || 'Usuario';
  document.getElementById('info-nombres').textContent = cliente.nombres || '-';
  document.getElementById('info-apellidos').textContent = cliente.apellidos || '-';
  document.getElementById('info-email').textContent = cliente.email || '-';
  document.getElementById('info-telefono').textContent = cliente.telefono || '-';
  document.getElementById('info-direccion').textContent = cliente.direccion || '-';
})();
