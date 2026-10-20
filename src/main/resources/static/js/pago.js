/* ============================
    URBAN EATS - Pago JS
   ============================
   1) Pide al servidor el resumen del carrito (POST /pago/resumen): el servidor usa
      los precios de la base, revisa que todo sea de una cocina y calcula el total.
   2) Cobra:
      - Modo demostracion: boton "Pagar en modo demostracion" (no hay cobro real).
      - Normal: Payment Brick de MercadoPago con el total que dio el servidor.
   3) POST /process_payment. Solo si el pago queda "approved" el servidor crea el
      pedido; entonces se vacia el carrito y se va al seguimiento del pedido.
      Si el pago se rechaza, se muestra el motivo y el carrito NO se borra.
*/

const esc = (texto) => window.UE.escaparHTML(texto);
let totalServidor = 0;

document.addEventListener('DOMContentLoaded', iniciarPago);

async function iniciarPago() {
  const carrito = window.UE.obtenerCarrito();
  if (carrito.length === 0) {
    mostrarError('Tu carrito está vacío. Agrega platos desde el catálogo.');
    document.getElementById('resumen-items-pago').innerHTML =
      '<p style="font-size:0.875rem;"><a href="/catalogo" style="color:var(--principal);font-weight:700;">Ir al catálogo</a></p>';
    return;
  }

  // 1) Resumen validado por el servidor
  let resumen;
  try {
    const respuesta = await fetch('/pago/resumen', {
      method: 'POST',
      headers: window.UE.encabezadosCSRF(),
      body: JSON.stringify({ items: carrito })
    });
    resumen = await respuesta.json();
    if (!respuesta.ok) {
      mostrarError(resumen.error + ' Ajusta tu carrito para continuar.');
      document.getElementById('resumen-items-pago').innerHTML =
        '<p style="font-size:0.875rem;"><a href="/carrito" style="color:var(--principal);font-weight:700;">Volver al carrito</a></p>';
      return;
    }
  } catch (e) {
    mostrarError('No pudimos conectarnos con el servidor. Revisa tu conexión.');
    return;
  }

  pintarResumen(resumen);

  // 2) Medio de pago
  if (window.PAGOS_MODO_DEMO) {
    document.getElementById('pago-demo').style.display = 'block';
    document.getElementById('btn-pagar-demo').addEventListener('click', pagarEnModoDemo);
  } else if (!window.MP_PUBLIC_KEY) {
    mostrarError('La pasarela de pagos no está configurada (faltan las credenciales de prueba de MercadoPago).');
  } else {
    renderPaymentBrick();
  }
}

function pintarResumen(resumen) {
  totalServidor = Number(resumen.total);
  document.getElementById('resumen-cocina').innerHTML = `<i class="fas fa-store"></i> ${esc(resumen.cocina)}`;
  document.getElementById('resumen-items-pago').innerHTML = resumen.lineas.map(linea => `
    <div style="display:flex;justify-content:space-between;font-size:0.875rem;margin-bottom:0.4rem;">
      <span>${esc(linea.cantidad)}x ${esc(linea.nombre)}</span>
      <span>${window.UE.formatoCOP(linea.subtotal)}</span>
    </div>`).join('');
  document.getElementById('pago-subtotal').textContent = window.UE.formatoCOP(totalServidor);
  document.getElementById('pago-total').textContent = window.UE.formatoCOP(totalServidor) + ' COP';
}

function mostrarError(mensaje) {
  const caja = document.getElementById('pago-error');
  document.getElementById('pago-error-texto').textContent = mensaje;
  caja.style.display = mensaje ? 'block' : 'none';
  if (mensaje) caja.scrollIntoView({ behavior: 'smooth', block: 'center' });
}

function direccionElegida() {
  const select = document.getElementById('direccion-entrega');
  return select ? Number(select.value) : null;
}

// ---- 3) Enviar el pago al servidor (lo usan el modo demo y el Brick) ----
async function enviarPago(datosPago) {
  const respuesta = await fetch('/process_payment', {
    method: 'POST',
    headers: window.UE.encabezadosCSRF(),
    body: JSON.stringify({
      ...datosPago,
      items: window.UE.obtenerCarrito(),
      codigoDireccion: direccionElegida()
    })
  });
  const datos = await respuesta.json();

  if (datos.status === 'approved' && datos.pedido) {
    window.UE.guardarCarrito([]);
    window.UE.mostrarToast('¡Pago aprobado! Tu pedido llegó a la cocina.');
    setTimeout(() => { window.location.href = `/rastreo/${datos.pedido}`; }, 800);
    return true;
  }
  mostrarError(datos.mensaje_error || 'Tu pago fue rechazado. Intenta con otro medio de pago.');
  return false;
}

async function pagarEnModoDemo() {
  const boton = document.getElementById('btn-pagar-demo');
  boton.disabled = true;
  mostrarError('');
  try {
    const exito = await enviarPago({ metodo: 'demo' });
    if (!exito) boton.disabled = false;
  } catch (e) {
    mostrarError('No pudimos procesar el pago. Intenta de nuevo.');
    boton.disabled = false;
  }
}

// ---- Payment Brick de MercadoPago ----
async function renderPaymentBrick() {
  const mp = new MercadoPago(window.MP_PUBLIC_KEY);
  const bricksBuilder = mp.bricks();

  const settings = {
    initialization: {
      amount: totalServidor, // el total que calculo el servidor
    },
    customization: {
      visual: {
        style: {
          theme: 'default',
          customVariables: {
            baseColor: '#fec10b',
            baseColorFirstVariant: '#e6ac00',
            baseColorSecondVariant: '#f7643b',
            buttonTextColor: '#111417',
            outlinePrimaryColor: '#fec10b',
            outlineSecondaryColor: '#ddd4bf',
            errorColor: '#a42828',
          },
        },
      },
      paymentMethods: {
        // Solo tarjetas: se aprueban o rechazan al instante. (Los pagos en efectivo
        // quedan "pendientes" por dias y no sirven para un domicilio nocturno.)
        creditCard: 'all',
        debitCard: 'all',
        prepaidCard: 'all',
      },
    },
    callbacks: {
      onReady: () => {},
      onSubmit: ({ formData }) => {
        mostrarError('');
        return enviarPago(formData)
          .then(exito => { if (!exito) throw new Error('rechazado'); })
          .catch(() => { /* el mensaje ya se mostro; el Brick queda listo para reintentar */ });
      },
      onError: (error) => {
        console.error(error);
        mostrarError('Ocurrió un error en el formulario de pago. Revisa los datos de la tarjeta.');
      },
    },
  };
  window.paymentBrickController = await bricksBuilder.create('payment', 'paymentBrick_container', settings);
}

// Cierra el brick cada que el usuario recarga, cierra o sale del sitio
window.addEventListener('beforeunload', () => {
  if (window.paymentBrickController) {
    window.paymentBrickController.unmount();
  }
});
