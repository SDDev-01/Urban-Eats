/* El slider del banner está en js/banner.js */

/* ============================
   URBAN EATS - Catálogo JS
   Muestra todos los productos de todos los restaurantes
   ============================ */

let catalogoProductos    = [];
let categoriaActiva      = 'todos';
let productoSeleccionado = null;
let cantidadModal        = 1;

// Inicializar productos después de que data-restaurantes.js se haya cargado
function inicializarCatalogo() {
  catalogoProductos = obtenerProductos();
  if (!catalogoProductos || catalogoProductos.length === 0) {
    console.error('No se pudieron cargar los productos');
    return;
  }
  renderizarProductos(catalogoProductos);
}

// ---- RENDERIZAR TARJETAS ----
function renderizarProductos(lista) {
  const grid = document.getElementById('productos-grid');
  grid.innerHTML = '';

  if (lista.length === 0) {
    grid.innerHTML = `<p style="text-align:center;color:var(--texto-gris);padding:3rem;grid-column:1/-1;">
      No hay productos en esta categoría.</p>`;
    return;
  }

  lista.forEach(producto => {
    const card = document.createElement('div');
    card.className = 'producto-card';
    card.innerHTML = `
      <div class="producto-img-wrap" style="background:${producto.color};">
        <img 
          src="${producto.imagen}" 
          alt="${producto.nombre}"
          class="producto-img"
          onerror="this.style.display='none'; this.nextElementSibling.style.display='flex';"
        >
        <span class="producto-emoji" style="display:none;">${producto.emoji}</span>
      </div>
      <div class="producto-body">
        <div class="producto-categoria">${producto.categoria}</div>
        <div class="producto-nombre">${producto.nombre}</div>
        <div class="producto-desc">${producto.descripcion}</div>
        <div class="producto-footer">
          <span class="producto-precio">$${producto.precio.toLocaleString('es-CO')} COP</span>
          <button class="btn-ver" data-id="${producto.id}">Ver detalles</button>
        </div>
        <div class="producto-restaurante">
          <span class="restaurante-logo-mini">${producto.restauranteLogo}</span>
          <span class="restaurante-nombre-mini">${producto.restauranteNombre}</span>
        </div>
      </div>
    `;
    grid.appendChild(card);
  });

  document.querySelectorAll('.btn-ver').forEach(btn => {
    btn.addEventListener('click', () => {
      abrirModal(parseInt(btn.dataset.id));
    });
  });
}

// ---- FILTROS ----
document.querySelectorAll('.btn-filtro').forEach(btn => {
  btn.addEventListener('click', () => {
    document.querySelectorAll('.btn-filtro').forEach(b => b.classList.remove('activo'));
    btn.classList.add('activo');
    categoriaActiva = btn.dataset.categoria;
    const filtrados = categoriaActiva === 'todos'
      ? catalogoProductos
      : catalogoProductos.filter(p => p.categoria === categoriaActiva);
    renderizarProductos(filtrados);
  });
});

// ---- MODAL ----
function abrirModal(id) {
  const producto = catalogoProductos.find(p => p.id === id);
  if (!producto) return;
  productoSeleccionado = producto;
  cantidadModal = 1;

  document.getElementById('modal-titulo').textContent      = producto.nombre;
  document.getElementById('modal-categoria').textContent   = producto.categoria;
  document.getElementById('modal-nombre').textContent      = producto.nombre;
  document.getElementById('modal-descripcion').textContent = producto.descripcion;
  document.getElementById('detalle-cantidad').textContent  = cantidadModal;

  document.getElementById('modal-imagen-wrap').innerHTML = `
    <img 
      src="${producto.imagen}" 
      alt="${producto.nombre}"
      class="detalle-img"
      onerror="this.style.display='none'; this.nextElementSibling.style.display='flex';"
    >
    <div class="detalle-img-fallback" style="background:${producto.color}; font-size:5rem; display:none;">
      ${producto.emoji}
    </div>
  `;

  document.getElementById('modal-stats').innerHTML = `
    <span class="stat-chip"><i class="fas fa-fire" style="color:#e67e22;"></i> ${producto.calorias} cal</span>
    <span class="stat-chip"><i class="fas fa-dumbbell" style="color:#3498db;"></i> ${producto.proteina} proteína</span>
  `;

  actualizarPrecioModal();
  document.getElementById('modal-detalle').classList.add('abierto');
}

function actualizarPrecioModal() {
  if (!productoSeleccionado) return;
  const total = productoSeleccionado.precio * cantidadModal;
  document.getElementById('modal-precio').textContent = `$${total.toLocaleString('es-CO')} COP`;
}

document.getElementById('modal-cerrar-btn').addEventListener('click', () => {
  document.getElementById('modal-detalle').classList.remove('abierto');
});
document.getElementById('modal-detalle').addEventListener('click', (e) => {
  if (e.target === document.getElementById('modal-detalle')) {
    document.getElementById('modal-detalle').classList.remove('abierto');
  }
});
document.getElementById('btn-mas').addEventListener('click', () => {
  cantidadModal++;
  document.getElementById('detalle-cantidad').textContent = cantidadModal;
  actualizarPrecioModal();
});
document.getElementById('btn-menos').addEventListener('click', () => {
  if (cantidadModal > 1) {
    cantidadModal--;
    document.getElementById('detalle-cantidad').textContent = cantidadModal;
    actualizarPrecioModal();
  }
});
document.getElementById('btn-agregar-carrito').addEventListener('click', () => {
  if (!productoSeleccionado) return;
  const carrito = window.UE.obtenerCarrito();
  const existe  = carrito.find(p => p.id === productoSeleccionado.id);
  if (existe) {
    existe.cantidad += cantidadModal;
  } else {
    carrito.push({ ...productoSeleccionado, cantidad: cantidadModal });
  }
  window.UE.guardarCarrito(carrito);
  window.UE.mostrarToast(`¡${productoSeleccionado.nombre} agregado al carrito!`);
  document.getElementById('modal-detalle').classList.remove('abierto');
});

// ---- INICIAR ----
// Esperar a que el DOM y todos los scripts estén listos
if (document.readyState === 'loading') {
  document.addEventListener('DOMContentLoaded', inicializarCatalogo);
} else {
  inicializarCatalogo();
}
