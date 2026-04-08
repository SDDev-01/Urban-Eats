/* ============================
   MÉTODO DE PAGO PREFERIDO
   ============================ */
(function gestionarMetodoPago() {
  // Clave en localStorage para el método preferido
  const CLAVE = 'ue_metodo_pago';

  // Leer preferencia guardada (por defecto: tarjeta)
  const preferencia = localStorage.getItem(CLAVE) || 'tarjeta';

  // Elementos del DOM
  const btnTarjeta  = document.getElementById('perfil-met-tarjeta');
  const btnEfectivo = document.getElementById('perfil-met-efectivo');
  const secTarjeta  = document.getElementById('perfil-seccion-tarjeta');
  const secEfectivo = document.getElementById('perfil-seccion-efectivo');
  const btnGuardarTarjeta  = document.getElementById('perfil-btn-guardar-tarjeta');
  const btnGuardarEfectivo = document.getElementById('perfil-btn-guardar-efectivo');

  // Inputs de la tarjeta
  const inputNumero  = document.getElementById('perfil-t-numero');
  const inputTitular = document.getElementById('perfil-t-titular');
  const inputFecha   = document.getElementById('perfil-t-fecha');

  // Preview de la tarjeta
  const previewNumero  = document.getElementById('perfil-preview-numero');
  const previewTitular = document.getElementById('perfil-preview-titular');
  const previewFecha   = document.getElementById('perfil-preview-fecha');

  // Salir si alguno de los elementos necesarios no existe
  if (!btnTarjeta || !btnEfectivo || !secTarjeta || !secEfectivo) return;

  // Formatear número de tarjeta con espacios cada 4 dígitos
  if (inputNumero) {
    inputNumero.addEventListener('input', function(e) {
      let val = e.target.value.replace(/\s/g, '').replace(/\D/g, '');
      let formatted = val.match(/.{1,4}/g)?.join(' ') || '';
      e.target.value = formatted;
      
      // Actualizar preview
      if (previewNumero) {
        previewNumero.textContent = formatted || '•••• •••• •••• ••••';
      }
    });
  }

  // Actualizar titular en preview
  if (inputTitular) {
    inputTitular.addEventListener('input', function(e) {
      if (previewTitular) {
        previewTitular.textContent = e.target.value.toUpperCase() || 'NOMBRE TITULAR';
      }
    });
  }

  // Formatear fecha MM/AA
  if (inputFecha) {
    inputFecha.addEventListener('input', function(e) {
      let val = e.target.value.replace(/\D/g, '');
      if (val.length >= 2) {
        val = val.substring(0, 2) + '/' + val.substring(2, 4);
      }
      e.target.value = val;
      
      // Actualizar preview
      if (previewFecha) {
        previewFecha.textContent = val || 'MM/AA';
      }
    });
  }

  // Mostrar la sección según la preferencia guardada
  function mostrarSeccion(metodo) {
    if (metodo === 'tarjeta') {
      secTarjeta.style.display  = '';
      secEfectivo.style.display = 'none';
      btnTarjeta.classList.add('activo');
      btnEfectivo.classList.remove('activo');
    } else {
      secTarjeta.style.display  = 'none';
      secEfectivo.style.display = '';
      btnEfectivo.classList.add('activo');
      btnTarjeta.classList.remove('activo');
    }
  }

  // Aplicar preferencia al cargar la página
  mostrarSeccion(preferencia);

  // Cambiar sección al hacer clic en los botones de método
  btnTarjeta.addEventListener('click', function () {
    mostrarSeccion('tarjeta');
  });

  btnEfectivo.addEventListener('click', function () {
    mostrarSeccion('efectivo');
  });

  // Guardar método "Tarjeta"
  btnGuardarTarjeta.addEventListener('click', function () {
    const numero  = document.getElementById('perfil-t-numero').value.trim();
    const titular = document.getElementById('perfil-t-titular').value.trim();
    const fecha   = document.getElementById('perfil-t-fecha').value.trim();
    const cvv     = document.getElementById('perfil-t-cvv').value.trim();

    // Limpiar errores previos
    document.getElementById('perfil-err-t-num').textContent   = '';
    document.getElementById('perfil-err-t-tit').textContent   = '';
    document.getElementById('perfil-err-t-fecha').textContent = '';
    document.getElementById('perfil-err-t-cvv').textContent   = '';

    // Validación básica
    let valido = true;
    if (numero.replace(/\s/g, '').length < 13) {
      document.getElementById('perfil-err-t-num').textContent = 'Ingresa un número de tarjeta válido';
      valido = false;
    }
    if (!titular) {
      document.getElementById('perfil-err-t-tit').textContent = 'Ingresa el nombre del titular';
      valido = false;
    }
    if (!/^\d{2}\/\d{2}$/.test(fecha)) {
      document.getElementById('perfil-err-t-fecha').textContent = 'Formato: MM/AA';
      valido = false;
    } else {
      // Verificar que la tarjeta no esté vencida
      const partes   = fecha.split('/');
      const mesCard  = parseInt(partes[0], 10);
      const anioCard = 2000 + parseInt(partes[1], 10);
      const hoy      = new Date();
      if (mesCard < 1 || mesCard > 12 || anioCard < hoy.getFullYear() ||
          (anioCard === hoy.getFullYear() && mesCard < (hoy.getMonth() + 1))) {
        document.getElementById('perfil-err-t-fecha').textContent = 'La tarjeta está vencida';
        valido = false;
      }
    }
    if (cvv.length < 3) {
      document.getElementById('perfil-err-t-cvv').textContent = 'Ingresa el CVV';
      valido = false;
    }
    if (!valido) return;

    // Persistir preferencia (no guardamos datos sensibles de la tarjeta)
    localStorage.setItem(CLAVE, 'tarjeta');

    if (window.UE && window.UE.mostrarToast) {
      window.UE.mostrarToast('Método de pago guardado', 'fa-check-circle');
    }
  });

  // Guardar método "Efectivo"
  btnGuardarEfectivo.addEventListener('click', function () {
    localStorage.setItem(CLAVE, 'efectivo');
    if (window.UE && window.UE.mostrarToast) {
      window.UE.mostrarToast('Método de pago guardado', 'fa-check-circle');
    }
  });
})();

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
