/* ============================
   URBAN EATS - Carrito JS
   ============================ */

const DOMICILIO = 5000;

// Renderiza los items del carrito
function renderizarCarrito() {
  const carrito = window.UE.obtenerCarrito();
  const wrap = document.getElementById('carrito-items-wrap');
  const titulo = document.getElementById('carrito-titulo');
  const totalItems = carrito.reduce((s, p) => s + p.cantidad, 0);

  titulo.textContent = `Tu Carrito (${totalItems} ${totalItems === 1 ? 'producto' : 'productos'})`;

  if (carrito.length === 0) {
    wrap.innerHTML = `
      <div class="carrito-vacio">
        <div class="icono"><i class="fas fa-shopping-cart"></i></div>
        <h3>Tu carrito está vacío</h3>
        <p>Agrega productos desde el catálogo para comenzar tu pedido.</p>
        <a href="catalogo.html" class="btn btn-verde"><i class="fas fa-arrow-left"></i> Ver Catálogo</a>
      </div>
    `;
    actualizarResumen(0);
    return;
  }

  let subtotal = 0;
  let html = '<div class="carrito-items">';

  carrito.forEach(item => {
    const precioItem = item.precio * item.cantidad;
    subtotal += precioItem;
    html += `
      <div class="item-card" data-id="${item.id}">
        <div class="item-emoji">${item.emoji || '🍽️'}</div>
        <div class="item-info">
          <h4>${item.nombre}</h4>
          <span class="precio-unit">$${item.precio.toLocaleString('es-CO')} c/u</span>
        </div>
        <div class="item-cantidad">
          <button class="btn-cant btn-item-menos" data-id="${item.id}">
            <i class="fas fa-minus"></i>
          </button>
          <span class="cant-num">${item.cantidad}</span>
          <button class="btn-cant btn-item-mas" data-id="${item.id}">
            <i class="fas fa-plus"></i>
          </button>
        </div>
        <span class="item-precio">$${precioItem.toLocaleString('es-CO')}</span>
        <button class="btn-eliminar btn-item-eliminar" data-id="${item.id}" title="Eliminar">
          <i class="fas fa-trash"></i>
        </button>
      </div>
    `;
  });

  html += '</div>';
  wrap.innerHTML = html;
  actualizarResumen(subtotal);
  agregarEventosItems();
}

// Actualiza subtotal y total en el resumen
function actualizarResumen(subtotal) {
  const total = subtotal + DOMICILIO;
  document.getElementById('resumen-subtotal').textContent = `$${subtotal.toLocaleString('es-CO')}`;
  document.getElementById('resumen-total').textContent = `$${total.toLocaleString('es-CO')} COP`;
}

// Eventos de + / - / eliminar
function agregarEventosItems() {
  document.querySelectorAll('.btn-item-mas').forEach(btn => {
    btn.addEventListener('click', () => {
      const id = parseInt(btn.dataset.id);
      const carrito = window.UE.obtenerCarrito();
      const item = carrito.find(p => p.id === id);
      if (item) item.cantidad++;
      window.UE.guardarCarrito(carrito);
      renderizarCarrito();
    });
  });

  document.querySelectorAll('.btn-item-menos').forEach(btn => {
    btn.addEventListener('click', () => {
      const id = parseInt(btn.dataset.id);
      const carrito = window.UE.obtenerCarrito();
      const item = carrito.find(p => p.id === id);
      if (item && item.cantidad > 1) {
        item.cantidad--;
      } else {
        const idx = carrito.findIndex(p => p.id === id);
        if (idx > -1) carrito.splice(idx, 1);
      }
      window.UE.guardarCarrito(carrito);
      renderizarCarrito();
    });
  });

  document.querySelectorAll('.btn-item-eliminar').forEach(btn => {
    btn.addEventListener('click', () => {
      const id = parseInt(btn.dataset.id);
      const carrito = window.UE.obtenerCarrito().filter(p => p.id !== id);
      window.UE.guardarCarrito(carrito);
      window.UE.mostrarToast('Producto eliminado del carrito.', 'fa-trash');
      renderizarCarrito();
    });
  });
}

// Limpiar carrito
document.getElementById('btn-limpiar-carrito').addEventListener('click', () => {
  if (window.UE.obtenerCarrito().length === 0) return;
  window.UE.guardarCarrito([]);
  window.UE.mostrarToast('Carrito limpiado.', 'fa-trash');
  renderizarCarrito();
});

// Realizar pedido → redirige a rastreo
document.getElementById('btn-realizar-pedido').addEventListener('click', () => {
  const carrito = window.UE.obtenerCarrito();
  if (carrito.length === 0) {
    window.UE.mostrarToast('Agrega productos antes de realizar el pedido.', 'fa-exclamation-circle');
    return;
  }
  // Guardar pedido simulado
  const pedido = {
    id: 'ORD-' + Date.now(),
    items: carrito,
    fecha: new Date().toISOString(),
    estado: 'preparando'
  };
  localStorage.setItem('ue_pedido_actual', JSON.stringify(pedido));
  window.UE.guardarCarrito([]);
  window.UE.mostrarToast('¡Pedido realizado! Redirigiendo al seguimiento...');
  setTimeout(() => { window.location.href = 'rastreo.html'; }, 1500);
});

// Inicializar
renderizarCarrito();
