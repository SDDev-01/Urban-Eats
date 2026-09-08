/* El slider del banner está en js/banner.js */

/* Las tarjetas de producto las renderiza Thymeleaf desde CatalogoController.
   Este archivo solo maneja: filtros por categoría, modal de detalle y carrito. */

const PALETA_COLORES = ['#d5f5e3', '#fef9e7', '#e8f4fd', '#fdecea', '#f0e6ff', '#fff3e0'];

let productoSeleccionado = null;
let cantidadModal = 1;

function colorPorId(id) {
  return PALETA_COLORES[id % PALETA_COLORES.length];
}

/* ---- FILTROS: muestran u ocultan las tarjetas que ya están en el DOM ---- */

function iniciarFiltros() {
  const wrap = document.querySelector('.filtros-wrap');
  if (!wrap) return;

  wrap.querySelectorAll('.btn-filtro').forEach(btn => {
    btn.addEventListener('click', () => {
      wrap.querySelectorAll('.btn-filtro').forEach(b => b.classList.remove('activo'));
      btn.classList.add('activo');
      aplicarFiltro(btn.dataset.categoria);
    });
  });
}

function aplicarFiltro(categoria) {
  const tarjetas = document.querySelectorAll('.producto-card');
  let visibles = 0;

  tarjetas.forEach(card => {
    const coincide = categoria === 'todos' || card.dataset.categoria === categoria;
    card.style.display = coincide ? '' : 'none';
    if (coincide) visibles++;
  });

  mostrarVacio(visibles === 0);
}

function mostrarVacio(vacio) {
  const grid = document.getElementById('productos-grid');
  let aviso = document.getElementById('aviso-vacio');

  if (!vacio) {
    if (aviso) aviso.remove();
    return;
  }
  if (aviso) return;

  aviso = document.createElement('p');
  aviso.id = 'aviso-vacio';
  aviso.style.cssText = 'text-align:center;color:var(--texto-gris);padding:3rem;grid-column:1/-1;';
  aviso.textContent = 'No hay productos en esta categoría.';
  grid.appendChild(aviso);
}

/* ---- ORDEN POR PRECIO: reordena las tarjetas ya renderizadas, sin volver a pedirlas al servidor ---- */

let ordenPrecioOrdenOriginal = null;

function iniciarOrdenPrecio() {
  const btn = document.getElementById('btn-orden-precio');
  const grid = document.getElementById('productos-grid');
  if (!btn || !grid) return;

  // Se guarda el orden con el que Thymeleaf renderizó las tarjetas, para poder volver a él.
  ordenPrecioOrdenOriginal = Array.from(grid.querySelectorAll('.producto-card'));

  btn.addEventListener('click', () => {
    const siguiente = { none: 'asc', asc: 'desc', desc: 'none' }[btn.dataset.orden];
    btn.dataset.orden = siguiente;
    aplicarOrdenPrecio(siguiente);
    actualizarBotonOrdenPrecio(siguiente);
  });
}

function aplicarOrdenPrecio(orden) {
  const grid = document.getElementById('productos-grid');

  if (orden === 'none') {
    ordenPrecioOrdenOriginal.forEach(card => grid.appendChild(card));
    return;
  }

  const tarjetas = Array.from(grid.querySelectorAll('.producto-card'));
  tarjetas.sort((a, b) => {
    const precioA = parseFloat(a.dataset.precio) || 0;
    const precioB = parseFloat(b.dataset.precio) || 0;
    return orden === 'asc' ? precioA - precioB : precioB - precioA;
  });
  tarjetas.forEach(card => grid.appendChild(card));
}

function actualizarBotonOrdenPrecio(orden) {
  const btn = document.getElementById('btn-orden-precio');
  const icono = btn.querySelector('i');
  const texto = document.getElementById('btn-orden-precio-texto');

  const config = {
    none: { icono: 'fa-sort', texto: 'Ordenar por precio', activo: false },
    asc: { icono: 'fa-sort-amount-up-alt', texto: 'Más barato primero', activo: true },
    desc: { icono: 'fa-sort-amount-down-alt', texto: 'Más caro primero', activo: true },
  }[orden];

  icono.className = `fas ${config.icono}`;
  texto.textContent = config.texto;
  btn.classList.toggle('activo', config.activo);
}

/* ---- COLOR DE FONDO: se aplica en cliente porque depende del id ---- */

function pintarFondos() {
  document.querySelectorAll('.btn-ver').forEach(btn => {
    const wrap = btn.closest('.producto-card').querySelector('.producto-img-wrap');
    if (wrap) wrap.style.background = colorPorId(parseInt(btn.dataset.id));
  });
}

/* ---- MODAL: los datos salen de los data-* que puso Thymeleaf ---- */

function iniciarBotonesVer() {
  document.querySelectorAll('.btn-ver').forEach(btn => {
    btn.addEventListener('click', () => abrirModal(btn.dataset));
  });
}

function abrirModal(datos) {
  productoSeleccionado = {
    id: parseInt(datos.id),
    nombre: datos.nombre,
    descripcion: datos.descripcion,
    precio: parseFloat(datos.precio),
    categoria: datos.categoria
  };
  cantidadModal = 1;

  const color = colorPorId(productoSeleccionado.id);

  document.getElementById('modal-titulo').textContent = productoSeleccionado.nombre;
  document.getElementById('modal-categoria').textContent = productoSeleccionado.categoria || '';
  document.getElementById('modal-nombre').textContent = productoSeleccionado.nombre;
  document.getElementById('modal-descripcion').textContent = productoSeleccionado.descripcion;
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

/* ---- LISTENERS DEL MODAL Y CARRITO ---- */

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

/* ---- ARRANQUE ---- */

function inicializarCatalogo() {
  pintarFondos();
  iniciarFiltros();
  iniciarBotonesVer();
  iniciarOrdenPrecio();
}

if (document.readyState === 'loading') {
  document.addEventListener('DOMContentLoaded', inicializarCatalogo);
} else {
  inicializarCatalogo();
}
