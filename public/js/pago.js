/* ============================
    URBAN EATS - Pago JS
   ============================ */

//cambiar todo lo de js relacionado a una tarjeta, eso lo maneja mercado pago
// ---- mercado pago ----
const mp = new MercadoPago(window.MP_PUBLIC_KEY);
const bricksBuilder = mp.bricks();

// ---- CARGAR RESUMEN DEL PEDIDO ----
let subtotal = 0;

(function cargarResumen() {
  subtotal = 0
  const carrito = window.UE.obtenerCarrito();
  const wrap = document.getElementById('resumen-items-pago');

  if (carrito.length === 0) {
    wrap.innerHTML = '<p style="color:var(--texto-gris);font-size:0.875rem;">No hay productos en el carrito.</p>';
    document.getElementById('pago-subtotal').textContent = '$0';
    document.getElementById('pago-total').textContent = '$0 COP';
    return;
  }

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
  //pago total
  document.getElementById('pago-total').textContent = `$${subtotal.toLocaleString('es-CO')} COP`;
  document.getElementById('monto-input').value=subtotal;
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

// brick de mercado pago

const renderPaymentBrick = async (bricksBuilder) => {
  const settings = {
    initialization: {
      /*
      "amount" es el monto total a pagar por todos los medios de pago con excepción de la Cuenta de Mercado Pago y Cuotas sin tarjeta de crédito, las cuales tienen su valor de procesamiento determinado en el backend a través del "preferenceId"
     */
      amount: subtotal,
      //dejar el preferenceId para despues
      //preferenceId: "<PREFERENCE_ID>",
    },
    customization: {
      paymentMethods: {
        ticket: "all",
        //este es PSE
        //bankTransfer: "all",
        creditCard: "all",
        prepaidCard: "all",
        debitCard: "all",
        //mercadoPago: "all",
      },
    },
    callbacks: {
      onReady: () => {
        /*
        Callback llamado cuando el Brick está listo.
        Aquí puede ocultar cargamentos de su sitio, por ejemplo.
       */
      },
      onSubmit: ({ selectedPaymentMethod, formData }) => {
        console.log(formData);
        console.log("CLICK EN PAGAR");
        console.log(formData);
       // callback llamado al hacer clic en el botón enviar datos
        return new Promise((resolve, reject) => {
          fetch("/process_payment", {
            method: "POST",
            headers: {
              "Content-Type": "application/json",
            },
            body: JSON.stringify(formData),
          })
            .then((response) => response.json())
            .then((response) => {
              // recibir el resultado del pago
              if (response.status === "approved") {
                window.location.href = "/rastreo";
              } else {
                window.location.href = "/pago?error=1";
              }
              resolve();
            })
            .catch((error) => {
             // manejar la respuesta de error al intentar crear el pago
              reject();
            });
        });
      },
      onError: (error) => {
       // callback llamado para todos los casos de error de Brick
        console.error(error);
      },
    },
  };
  window.paymentBrickController = await bricksBuilder.create(
    "payment",
    "paymentBrick_container",
    settings
  );
};
renderPaymentBrick(bricksBuilder);

// fin del brick de mercado pago

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

//cierra el brick cada que el usuario recarga, cierra o sale del sitio

window.addEventListener('beforeunload', () => {
  if (window.paymentBrickController) {
    window.paymentBrickController.unmount();
  }
});
