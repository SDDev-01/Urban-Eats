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
        //console.log(formData);
        //console.log("CLICK EN PAGAR");
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

//cierra el brick cada que el usuario recarga, cierra o sale del sitio

window.addEventListener('beforeunload', () => {
  if (window.paymentBrickController) {
    window.paymentBrickController.unmount();
  }
});
