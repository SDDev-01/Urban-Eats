/* ============================
   URBAN EATS - Catálogo JS
   Sin imágenes externas: usa
   tarjetas con color + emoji
   ============================ */

const productos = [
  {
    id: 1, nombre: "Bowl Mediterráneo", categoria: "Bowls",
    descripcion: "Ensalada fresca con quinoa, garbanzos, pepino, tomate cherry, aceitunas y aderezo de tahini.",
    precio: 18500, emoji: "🥗", color: "#d5f5e3", calorias: 450, proteina: "18g"
  },
  {
    id: 2, nombre: "Pollo a la Parrilla", categoria: "Platos Principales",
    descripcion: "Pechuga de pollo marinada con hierbas, acompañada de vegetales asados y batata.",
    precio: 22000, emoji: "🍗", color: "#fef9e7", calorias: 520, proteina: "38g"
  },
  {
    id: 3, nombre: "Smoothie Bowl Energético", categoria: "Desayunos",
    descripcion: "Base de açaí y plátano con granola, frutas frescas, coco rallado y miel.",
    precio: 16000, emoji: "🍓", color: "#fdecea", calorias: 380, proteina: "12g"
  },
  {
    id: 4, nombre: "Wrap de Vegetales", categoria: "Wraps",
    descripcion: "Tortilla integral rellena de hummus, aguacate, espinaca, zanahoria y pimientos asados.",
    precio: 15000, emoji: "🌯", color: "#fef3e2", calorias: 340, proteina: "14g"
  },
  {
    id: 5, nombre: "Bowl de Quinoa y Salmón", categoria: "Bowls",
    descripcion: "Quinoa tricolor, salmón ahumado, edamame, aguacate y vinagreta de jengibre.",
    precio: 26000, emoji: "🐟", color: "#e8f4fd", calorias: 490, proteina: "32g"
  },
  {
    id: 6, nombre: "Jugo Verde Detox", categoria: "Bebidas",
    descripcion: "Espinaca, pepino, apio, manzana verde, limón y jengibre. 100% natural.",
    precio: 12000, emoji: "🥤", color: "#eafaf1", calorias: 95, proteina: "2g"
  },
  {
    id: 7, nombre: "Avena Overnight", categoria: "Desayunos",
    descripcion: "Avena remojada con leche de almendra, chía, frutos rojos y miel de abeja.",
    precio: 13500, emoji: "🥣", color: "#fdf2e9", calorias: 320, proteina: "11g"
  },
  {
    id: 8, nombre: "Wrap de Pollo y Palta", categoria: "Wraps",
    descripcion: "Tortilla de espinaca con pollo grillado, aguacate, lechuga, tomate y salsa griega.",
    precio: 17500, emoji: "🌯", color: "#f9ebea", calorias: 410, proteina: "29g"
  },
  {
    id: 9, nombre: "Ensalada César Fit", categoria: "Platos Principales",
    descripcion: "Lechuga romana, pollo a la plancha, crutones integrales y aderezo césar light.",
    precio: 19000, emoji: "🥙", color: "#e9f7ef", calorias: 380, proteina: "26g"
  },
  {
    id: 10, nombre: "Kombucha de Frutas", categoria: "Bebidas",
    descripcion: "Kombucha artesanal con frutas tropicales, fermentada naturalmente. Rico en probióticos.",
    precio: 9500, emoji: "🍹", color: "#f4ecf7", calorias: 60, proteina: "0g"
  },
  {
    id: 11, nombre: "Bowl Buddha", categoria: "Bowls",
    descripcion: "Arroz integral, tempeh, brócoli al vapor, zanahoria, aguacate y salsa de maní.",
    precio: 20000, emoji: "🍲", color: "#fef9e7", calorias: 510, proteina: "22g"
  },
  {
    id: 12, nombre: "Tostadas de Aguacate", categoria: "Desayunos",
    descripcion: "Pan artesanal integral con aguacate triturado, huevo pochado, semillas y limón.",
    precio: 14000, emoji: "🥑", color: "#d5f5e3", calorias: 350, proteina: "16g"
  }
];

let categoriaActiva      = 'todos';
let productoSeleccionado = null;
let cantidadModal        = 1;

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
        <span class="producto-emoji">${producto.emoji}</span>
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
      ? productos
      : productos.filter(p => p.categoria === categoriaActiva);
    renderizarProductos(filtrados);
  });
});

// ---- MODAL ----
function abrirModal(id) {
  const producto = productos.find(p => p.id === id);
  if (!producto) return;
  productoSeleccionado = producto;
  cantidadModal = 1;

  document.getElementById('modal-titulo').textContent      = producto.nombre;
  document.getElementById('modal-categoria').textContent   = producto.categoria;
  document.getElementById('modal-nombre').textContent      = producto.nombre;
  document.getElementById('modal-descripcion').textContent = producto.descripcion;
  document.getElementById('detalle-cantidad').textContent  = cantidadModal;

  document.getElementById('modal-imagen-wrap').innerHTML = `
    <div class="detalle-img-fallback" style="background:${producto.color}; font-size:5rem;">
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
renderizarProductos(productos);
