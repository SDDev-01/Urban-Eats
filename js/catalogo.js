/* ============================
   SLIDER DEL BANNER
   ============================ */
(function() {
  const slides = document.getElementById('banner-slides');
  const slideItems = document.querySelectorAll('.banner-slide');
  const navContainer = document.getElementById('banner-nav');
  const prevBtn = document.getElementById('banner-prev');
  const nextBtn = document.getElementById('banner-next');
  
  let currentSlide = 0;
  const totalSlides = slideItems.length;
  let autoSlideInterval;

  // Crear los indicadores (dots)
  for (let i = 0; i < totalSlides; i++) {
    const dot = document.createElement('div');
    dot.className = 'banner-dot';
    if (i === 0) dot.classList.add('active');
    dot.addEventListener('click', () => goToSlide(i));
    navContainer.appendChild(dot);
  }

  const dots = document.querySelectorAll('.banner-dot');

  // Función para ir a un slide específico
  function goToSlide(index) {
    currentSlide = index;
    // Mover el slider
    slides.style.transform = `translateX(-${currentSlide * 100}%)`;
    
    // Actualizar los dots
    dots.forEach((dot, i) => {
      dot.classList.toggle('active', i === currentSlide);
    });
  }

  // Función para ir al siguiente slide
  function nextSlide() {
    currentSlide = (currentSlide + 1) % totalSlides;
    goToSlide(currentSlide);
  }

  // Función para ir al slide anterior
  function prevSlide() {
    currentSlide = (currentSlide - 1 + totalSlides) % totalSlides;
    goToSlide(currentSlide);
  }

  // Event listeners para las flechas
  nextBtn.addEventListener('click', () => {
    nextSlide();
    resetAutoSlide();
  });

  prevBtn.addEventListener('click', () => {
    prevSlide();
    resetAutoSlide();
  });

  // Auto-slide cada 5 segundos
  function startAutoSlide() {
    autoSlideInterval = setInterval(nextSlide, 5000);
  }

  // Reiniciar el auto-slide cuando el usuario interactúa
  function resetAutoSlide() {
    clearInterval(autoSlideInterval);
    startAutoSlide();
  }

  // Iniciar el auto-slide
  startAutoSlide();

  // Pausar el auto-slide cuando el mouse está sobre el banner
  const banner = document.querySelector('.food-banner');
  banner.addEventListener('mouseenter', () => {
    clearInterval(autoSlideInterval);
  });

  banner.addEventListener('mouseleave', () => {
    startAutoSlide();
  });
})();

/* ============================
   URBAN EATS - Catálogo JS
   Usa imágenes reales con emoji
   como fallback en caso de error
   ============================ */

const productos = [
  {
    id: 1, nombre: "Bowl Mediterráneo", categoria: "Bowls",
    descripcion: "Ensalada fresca con quinoa, garbanzos, pepino, tomate cherry, aceitunas y aderezo de tahini.",
    precio: 18500, emoji: "🥗", color: "#d5f5e3", calorias: 450, proteina: "18g",
    imagen: "https://images.unsplash.com/photo-1546069901-ba9599a7e63c?w=400&h=300&fit=crop"
  },
  {
    id: 2, nombre: "Pollo a la Parrilla", categoria: "Platos Principales",
    descripcion: "Pechuga de pollo marinada con hierbas, acompañada de vegetales asados y batata.",
    precio: 22000, emoji: "🍗", color: "#fef9e7", calorias: 520, proteina: "38g",
    imagen: "https://images.unsplash.com/photo-1598103442097-8b74394b95c6?w=400&h=300&fit=crop"
  },
  {
    id: 3, nombre: "Smoothie Bowl Energético", categoria: "Desayunos",
    descripcion: "Base de açaí y plátano con granola, frutas frescas, coco rallado y miel.",
    precio: 16000, emoji: "🍓", color: "#fdecea", calorias: 380, proteina: "12g",
    imagen: "https://images.unsplash.com/photo-1590301157890-4810ed352733?w=400&h=300&fit=crop"
  },
  {
    id: 4, nombre: "Wrap de Vegetales", categoria: "Wraps",
    descripcion: "Tortilla integral rellena de hummus, aguacate, espinaca, zanahoria y pimientos asados.",
    precio: 15000, emoji: "🌯", color: "#fef3e2", calorias: 340, proteina: "14g",
    imagen: "https://images.unsplash.com/photo-1626700051175-6818013e1d4f?w=400&h=300&fit=crop"
  },
  {
    id: 5, nombre: "Bowl de Quinoa y Salmón", categoria: "Bowls",
    descripcion: "Quinoa tricolor, salmón ahumado, edamame, aguacate y vinagreta de jengibre.",
    precio: 26000, emoji: "🐟", color: "#e8f4fd", calorias: 490, proteina: "32g",
    imagen: "https://images.unsplash.com/photo-1559847844-5315695dadae?w=400&h=300&fit=crop"
  },
  {
    id: 6, nombre: "Jugo Verde Detox", categoria: "Bebidas",
    descripcion: "Espinaca, pepino, apio, manzana verde, limón y jengibre. 100% natural.",
    precio: 12000, emoji: "🥤", color: "#eafaf1", calorias: 95, proteina: "2g",
    imagen: "https://images.unsplash.com/photo-1610970881699-44a5587cabec?w=400&h=300&fit=crop"
  },
  {
    id: 7, nombre: "Avena Overnight", categoria: "Desayunos",
    descripcion: "Avena remojada con leche de almendra, chía, frutos rojos y miel de abeja.",
    precio: 13500, emoji: "🥣", color: "#fdf2e9", calorias: 320, proteina: "11g",
    imagen: "https://images.unsplash.com/photo-1590301157890-4810ed352733?w=400&h=300&fit=crop"
  },
  {
    id: 8, nombre: "Wrap de Pollo y Palta", categoria: "Wraps",
    descripcion: "Tortilla de espinaca con pollo grillado, aguacate, lechuga, tomate y salsa griega.",
    precio: 17500, emoji: "🌯", color: "#f9ebea", calorias: 410, proteina: "29g",
    imagen: "https://images.unsplash.com/photo-1626645738196-c2a7c87a8f58?w=400&h=300&fit=crop"
  },
  {
    id: 9, nombre: "Ensalada César Fit", categoria: "Platos Principales",
    descripcion: "Lechuga romana, pollo a la plancha, crutones integrales y aderezo césar light.",
    precio: 19000, emoji: "🥙", color: "#e9f7ef", calorias: 380, proteina: "26g",
    imagen: "https://images.unsplash.com/photo-1546793665-c74683f339c1?w=400&h=300&fit=crop"
  },
  {
    id: 10, nombre: "Kombucha de Frutas", categoria: "Bebidas",
    descripcion: "Kombucha artesanal con frutas tropicales, fermentada naturalmente. Rico en probióticos.",
    precio: 9500, emoji: "🍹", color: "#f4ecf7", calorias: 60, proteina: "0g",
    imagen: "https://images.unsplash.com/photo-1544145945-35e9d6e0a76f?w=400&h=300&fit=crop"
  },
  {
    id: 11, nombre: "Bowl Buddha", categoria: "Bowls",
    descripcion: "Arroz integral, tempeh, brócoli al vapor, zanahoria, aguacate y salsa de maní.",
    precio: 20000, emoji: "🍲", color: "#fef9e7", calorias: 510, proteina: "22g",
    imagen: "https://images.unsplash.com/photo-1512621776951-a57141f2eefd?w=400&h=300&fit=crop"
  },
  {
    id: 12, nombre: "Tostadas de Aguacate", categoria: "Desayunos",
    descripcion: "Pan artesanal integral con aguacate triturado, huevo pochado, semillas y limón.",
    precio: 14000, emoji: "🥑", color: "#d5f5e3", calorias: 350, proteina: "16g",
    imagen: "https://images.unsplash.com/photo-1588137378633-dea1336ce1e2?w=400&h=300&fit=crop"
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
renderizarProductos(productos);
