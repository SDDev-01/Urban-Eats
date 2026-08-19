const PALETA_COLORES = ['#d5f5e3', '#fef9e7', '#e8f4fd', '#fdecea', '#f0e6ff', '#fff3e0'];

let productosRestaurante = [];
let categoriaActiva = 'todos';
let productoSeleccionado = null;
let cantidadModal = 1;

function colorPorId(id) {
  return PALETA_COLORES[id % PALETA_COLORES.length];
}

function cargarRestaurante() {
  const restaurante = window.RESTAURANTE_BD;
  const productos = window.PRODUCTOS_BD || [];

  if (!restaurante) {
    window.location.href = '/restaurantes';
    return;
  }

  document.title = `${restaurante.nombre} – Urban Eats`;

  const hero = document.getElementById('restaurante-hero');
  hero.style.background = `linear-gradient(135deg, ${colorPorId(restaurante.id)} 0%, #1b5e20 100%)`;
  hero.innerHTML = `
    <div class="restaurante-logo-grande" style="background: rgba(255,255,255,0.25);">
      🍽️
    </div>
    <div class="restaurante-info-hero">
      <h1>${restaurante.nombre}</h1>
      <div class="restaurante-detalles">
        <div class="restaurante-detalle-item">
          <i class="fas fa-map-marker-alt"></i>
          <span>${restaurante.direccion || '—'}</span>
        </div>
        <div class="restaurante-detalle-item">
          <i class="fas fa-clock"></i>
          <span>${restaurante.horario || '—'}</span>
        </div>
      </div>
    </div>
  `;

  productosRestaurante = productos;
  generarFiltros(productos);
  renderizarProductos(productos);
}

function generarFiltros(productos) {
  const wrap = document.querySelector('.filtros-wrap');
  if (!wrap) return;

  const categorias = [...new Set(productos.map(p => p.categoria).filter(Boolean))].sort();
  wrap.innerHTML = '<button class="btn-filtro activo" data-categoria="todos">Todos</button>';

  categorias.forEach(cat => {
    const btn = document.createElement('button');
    btn.className = 'btn-filtro';
    btn.dataset.categoria = cat;
    btn.textContent = cat;
    wrap.appendChild(btn);
  });

  wrap.querySelectorAll('.btn-filtro').forEach(btn => {
    btn.addEventListener('click', () => {
      wrap.querySelectorAll('.btn-filtro').forEach(b => b.classList.remove('activo'));
      btn.classList.add('activo');
      categoriaActiva = btn.dataset.categoria;
      const filtrados = categoriaActiva === 'todos'
        ? productosRestaurante
        : productosRestaurante.filter(p => p.categoria === categoriaActiva);
      renderizarProductos(filtrados);
    });
  });
}

function renderizarProductos(lista) {
  const grid = document.getElementById('productos-grid');
  grid.innerHTML = '';

  if (lista.length === 0) {
    grid.innerHTML = `<p style="text-align:center;color:var(--texto-gris);padding:3rem;grid-column:1/-1;">
      No hay productos disponibles en esta categoría.</p>`;
    return;
  }

  lista.forEach(producto => {
    const color = colorPorId(producto.id);
    const card = document.createElement('div');
    card.className = 'producto-card';
    card.innerHTML = `
      <div class="producto-img-wrap" style="background:${color};">
        <span class="producto-emoji" style="display:flex; font-size:3rem; align-items:center; justify-content:center; height:100%;">🍽️</span>
      </div>
      <div class="producto-body">
        <div class="producto-categoria">${producto.categoria || '—'}</div>
        <div class="producto-nombre">${producto.nombre}</div>
        <div class="producto-desc">${producto.descripcion}</div>
        <div class="producto-footer">
          <span class="producto-precio">$${Number(producto.precio).toLocaleString('es-CO')} COP</span>
          <button class="btn-ver" data-id="${producto.id}">Ver detalles</button>
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

function abrirModal(id) {
  const producto = productosRestaurante.find(p => p.id === id);
  if (!producto) return;

  productoSeleccionado = producto;
  cantidadModal = 1;

  const color = colorPorId(producto.id);

  document.getElementById('modal-titulo').textContent = producto.nombre;
  document.getElementById('modal-categoria').textContent = producto.categoria || '';
  document.getElementById('modal-nombre').textContent = producto.nombre;
  document.getElementById('modal-descripcion').textContent = producto.descripcion;
  document.getElementById('detalle-cantidad').textContent = cantidadModal;

  document.getElementById('modal-imagen-wrap').innerHTML = `
    <div class="detalle-img-fallback" style="background:${color}; font-size:5rem; display:flex; align-items:center; justify-content:center;">
      🍽️
    </div>
  `;

  document.getElementById('modal-stats').innerHTML = '';

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

cargarRestaurante();
