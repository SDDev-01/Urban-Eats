/* ============================
   URBAN EATS - Pago JS
   ============================ */

let metodoActivo = 'tarjeta';

// ---- CARGAR RESUMEN DEL PEDIDO ----
(function cargarResumen() {
  const carrito = window.UE.obtenerCarrito();
  const wrap = document.getElementById('resumen-items-pago');

  if (carrito.length === 0) {
    wrap.innerHTML = '<p style="color:var(--texto-gris);font-size:0.875rem;">No hay productos en el carrito.</p>';
    document.getElementById('pago-subtotal').textContent = '$0';
    document.getElementById('pago-total').textContent = '$0 COP';
    return;
  }

  let subtotal = 0;
  let html = '';
  carrito.forEach(item => {
    const precioItem = item.precio * item.cantidad;
    subtotal += precioItem;
    html += `
      <div style="display:flex;justify-content:space-between;font-size:0.875rem;margin-bottom:0.4rem;">
        <span>${item.cantidad}x ${item.nombre}</span>
        <span>$${precioItem.toLocaleString('es-CO')}</span>
      </div>
    `;
  });
  wrap.innerHTML = html;
  document.getElementById('pago-subtotal').textContent = `$${subtotal.toLocaleString('es-CO')}`;
  document.getElementById('pago-total').textContent = `$${subtotal.toLocaleString('es-CO')} COP`;
})();

// ---- CAMBIAR MÉTODO DE PAGO ----
document.querySelectorAll('.metodo-btn').forEach(btn => {
  btn.addEventListener('click', () => {
    document.querySelectorAll('.metodo-btn').forEach(b => b.classList.remove('activo'));
    btn.classList.add('activo');
    metodoActivo = btn.dataset.metodo;

    document.getElementById('seccion-tarjeta').style.display  = metodoActivo === 'tarjeta'  ? 'block' : 'none';
    document.getElementById('seccion-efectivo').style.display = metodoActivo === 'efectivo' ? 'block' : 'none';
  });
});

// ---- PREVIEW DE TARJETA ----
const inputNumero  = document.getElementById('t-numero');
const inputTitular = document.getElementById('t-titular');
const inputFecha   = document.getElementById('t-fecha');
const inputCvv     = document.getElementById('t-cvv');

// Formatear número de tarjeta (grupos de 4)
inputNumero.addEventListener('input', () => {
  let val = inputNumero.value.replace(/\D/g, '').substring(0, 16);
  inputNumero.value = val.replace(/(.{4})/g, '$1 ').trim();
  const preview = val.padEnd(16, '•').replace(/(.{4})/g, '$1 ').trim();
  document.getElementById('preview-numero').textContent = preview;
});

// Titular en mayúsculas en preview
inputTitular.addEventListener('input', () => {
  const val = inputTitular.value.toUpperCase() || 'NOMBRE TITULAR';
  document.getElementById('preview-titular').textContent = val;
});

// Formatear fecha MM/AA
inputFecha.addEventListener('input', () => {
  let val = inputFecha.value.replace(/\D/g, '').substring(0, 4);
  if (val.length >= 3) {
    val = val.substring(0, 2) + '/' + val.substring(2);
  }
  inputFecha.value = val;
  document.getElementById('preview-fecha').textContent = val || 'MM/AA';
});

// Solo permitir números en CVV
inputCvv.addEventListener('input', () => {
  inputCvv.value = inputCvv.value.replace(/\D/g, '');
});

// ---- VALIDACIÓN TARJETA ----
function validarTarjeta() {
  let valido = true;
  const numero  = document.getElementById('t-numero').value.replace(/\s/g, '');
  const titular = document.getElementById('t-titular').value.trim();
  const fecha   = document.getElementById('t-fecha').value.trim();
  const cvv     = document.getElementById('t-cvv').value.trim();

  // Número
  const errNum = document.getElementById('err-t-num');
  if (numero.length < 13 || numero.length > 16 || !/^\d+$/.test(numero)) {
    errNum.textContent = 'Número de tarjeta inválido (13-16 dígitos).'; valido = false;
  } else errNum.textContent = '';

  // Titular
  const errTit = document.getElementById('err-t-tit');
  if (!titular) { errTit.textContent = 'El nombre del titular es obligatorio.'; valido = false; }
  else errTit.textContent = '';

  // Fecha MM/AA
  const errFecha = document.getElementById('err-t-fecha');
  const regexFecha = /^(0[1-9]|1[0-2])\/\d{2}$/;
  if (!regexFecha.test(fecha)) {
    errFecha.textContent = 'Formato inválido. Usa MM/AA.'; valido = false;
  } else errFecha.textContent = '';

  // CVV
  const errCvv = document.getElementById('err-t-cvv');
  if (cvv.length < 3 || cvv.length > 4 || !/^\d+$/.test(cvv)) {
    errCvv.textContent = 'CVV inválido (3-4 dígitos).'; valido = false;
  } else errCvv.textContent = '';

  return valido;
}

// ---- CONFIRMAR PAGO ----
document.getElementById('btn-pagar').addEventListener('click', () => {
  if (metodoActivo === 'tarjeta' && !validarTarjeta()) {
    window.UE.mostrarToast('Por favor corrige los errores antes de continuar.', 'fa-exclamation-circle');
    return;
  }

  const carrito = window.UE.obtenerCarrito();
  if (carrito.length === 0) {
    window.UE.mostrarToast('El carrito está vacío.', 'fa-exclamation-circle');
    return;
  }

  const btn = document.getElementById('btn-pagar');
  btn.disabled = true;

  fetch('/pedido/confirmar', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
      'X-CSRF-TOKEN': document.querySelector('meta[name="csrf-token"]').content,
    },
    body: JSON.stringify({ items: carrito, metodo_pago: metodoActivo }),
  })
    .then(res => res.json())
    .then(data => {
      if (data.redirect) {
        window.UE.mostrarToast('¡Pago procesado exitosamente!');
        window.UE.guardarCarrito([]);
        setTimeout(() => { window.location.href = data.redirect; }, 1800);
      } else {
        window.UE.mostrarToast(data.error || 'Error al procesar el pago.', 'fa-exclamation-circle');
        btn.disabled = false;
      }
    })
    .catch(() => {
      window.UE.mostrarToast('Error de conexión. Inténtalo nuevamente.', 'fa-exclamation-circle');
      btn.disabled = false;
    });
});
