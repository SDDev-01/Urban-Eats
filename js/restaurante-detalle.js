/* ============================
   URBAN EATS - Restaurante Detalle JS
   Muestra el menú de un restaurante específico
   ============================ */

// Variables globales
let restauranteActual = null;
let productosRestaurante = [];
let categoriaActiva = 'todos';
let productoSeleccionado = null;
let cantidadModal = 1;

// Obtener ID del restaurante desde URL
function obtenerIdRestaurante() {
  const params = new URLSearchParams(window.location.search);
  return parseInt(params.get('id'));
}

// Cargar y renderizar información del restaurante
function cargarRestaurante() {
  const id = obtenerIdRestaurante();
  if (!id) {
    window.location.href = 'restaurantes.html';
    return;
  }

  restauranteActual = obtenerRestaurantePorId(id);
  if (!restauranteActual) {
    window.UE.mostrarToast('Restaurante no encontrado', 'error');
    window.location.href = 'restaurantes.html';
    return;
  }

  // Renderizar hero del restaurante
  const heroElement = document.getElementById('restaurante-hero');
  heroElement.style.background = `linear-gradient(135deg, ${restauranteActual.color} 0%, var(--verde-oscuro) 100%)`;
  heroElement.innerHTML = `
    <div class="restaurante-logo-grande" style="background: rgba(255, 255, 255, 0.25);">
      ${restauranteActual.logo}
    </div>
    <div class="restaurante-info-hero">
      <h1>${restauranteActual.nombre}</h1>
      <p>${restauranteActual.descripcion}</p>
      <div class="restaurante-detalles">
        <div class="restaurante-detalle-item">
          <i class="fas fa-map-marker-alt"></i>
          <span>${restauranteActual.ubicacion}</span>
        </div>
        <div class="restaurante-detalle-item">
          <i class="fas fa-clock"></i>
          <span>${restauranteActual.horario}</span>
        </div>
        <div class="restaurante-detalle-item">
          <i class="fas fa-check-circle"></i>
          <span>Abierto ahora</span>
        </div>
      </div>
    </div>
  `;

  // Actualizar título de la página
  document.title = `${restauranteActual.nombre} – Urban Eats`;

  // Cargar productos del restaurante
  productosRestaurante = obtenerProductosDeRestaurante(id);
  renderizarProductos(productosRestaurante);
}

// Renderizar tarjetas de productos
function renderizarProductos(lista) {
  const grid = document.getElementById('productos-grid');
  grid.innerHTML = '';

  if (lista.length === 0) {
    grid.innerHTML = `<p style="text-align:center;color:var(--texto-gris);padding:3rem;grid-column:1/-1;">
      No hay productos disponibles en esta categoría.</p>`;
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
      </div>
    `;
    grid.appendChild(card);
  });

  // Agregar event listeners a botones
  document.querySelectorAll('.btn-ver').forEach(btn => {
    btn.addEventListener('click', () => {
      abrirModal(parseInt(btn.dataset.id));
    });
  });
}

// Filtros de categorías
document.querySelectorAll('.btn-filtro').forEach(btn => {
  btn.addEventListener('click', () => {
    document.querySelectorAll('.btn-filtro').forEach(b => b.classList.remove('activo'));
    btn.classList.add('activo');
    categoriaActiva = btn.dataset.categoria;
    
    const filtrados = categoriaActiva === 'todos'
      ? productosRestaurante
      : productosRestaurante.filter(p => p.categoria === categoriaActiva);
    
    renderizarProductos(filtrados);
  });
});

// Abrir modal de detalle del producto
function abrirModal(id) {
  const producto = productosRestaurante.find(p => p.id === id);
  if (!producto) return;
  
  productoSeleccionado = producto;
  cantidadModal = 1;

  document.getElementById('modal-titulo').textContent = producto.nombre;
  document.getElementById('modal-categoria').textContent = producto.categoria;
  document.getElementById('modal-nombre').textContent = producto.nombre;
  document.getElementById('modal-descripcion').textContent = producto.descripcion;
  document.getElementById('detalle-cantidad').textContent = cantidadModal;

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

// Actualizar precio en el modal
function actualizarPrecioModal() {
  if (!productoSeleccionado) return;
  const total = productoSeleccionado.precio * cantidadModal;
  document.getElementById('modal-precio').textContent = `$${total.toLocaleString('es-CO')} COP`;
}

// Cerrar modal
document.getElementById('modal-cerrar-btn').addEventListener('click', () => {
  document.getElementById('modal-detalle').classList.remove('abierto');
});

document.getElementById('modal-detalle').addEventListener('click', (e) => {
  if (e.target === document.getElementById('modal-detalle')) {
    document.getElementById('modal-detalle').classList.remove('abierto');
  }
});

// Controles de cantidad
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

// Agregar al carrito
document.getElementById('btn-agregar-carrito').addEventListener('click', () => {
  if (!productoSeleccionado) return;
  
  const carrito = window.UE.obtenerCarrito();
  const existe = carrito.find(p => p.id === productoSeleccionado.id);
  
  if (existe) {
    existe.cantidad += cantidadModal;
  } else {
    carrito.push({ ...productoSeleccionado, cantidad: cantidadModal });
  }
  
  window.UE.guardarCarrito(carrito);
  window.UE.mostrarToast(`¡${productoSeleccionado.nombre} agregado al carrito!`);
  document.getElementById('modal-detalle').classList.remove('abierto');
});

// Inicializar al cargar la página
cargarRestaurante();
