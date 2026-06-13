/* ============================
   URBAN EATS - Restaurantes JS
   Lista de restaurantes disponibles
   ============================ */

// Renderizar tarjetas de restaurantes
function renderizarRestaurantes() {
  const grid = document.getElementById('restaurantes-grid');
  const restaurantes = obtenerRestaurantes();
  
  grid.innerHTML = '';

  restaurantes.forEach(restaurante => {
    const card = document.createElement('div');
    card.className = 'restaurante-card';
    card.innerHTML = `
      <div class="restaurante-header" style="background: ${restaurante.color};">
        ${restaurante.logo}
        <span class="restaurante-badge">Abierto</span>
      </div>
      <div class="restaurante-body">
        <h3 class="restaurante-nombre">${restaurante.nombre}</h3>
        <p class="restaurante-descripcion">${restaurante.descripcion}</p>
        <div class="restaurante-info">
          <div class="restaurante-info-item">
            <i class="fas fa-map-marker-alt"></i>
            <span>${restaurante.ubicacion}</span>
          </div>
          <div class="restaurante-info-item">
            <i class="fas fa-clock"></i>
            <span>${restaurante.horario}</span>
          </div>
        </div>
        <button class="btn-ver-menu" data-id="${restaurante.id}">
          <i class="fas fa-arrow-right"></i> Ver Menú
        </button>
      </div>
    `;
    
    // Click en tarjeta completa navega al restaurante
    card.addEventListener('click', (e) => {
      if (!e.target.closest('.btn-ver-menu')) {
        navegarARestaurante(restaurante.id);
      }
    });
    
    grid.appendChild(card);
  });

  // Click en botones "Ver Menú"
  document.querySelectorAll('.btn-ver-menu').forEach(btn => {
    btn.addEventListener('click', (e) => {
      e.stopPropagation();
      navegarARestaurante(parseInt(btn.dataset.id));
    });
  });
}

// Navegar a página de detalle del restaurante
function navegarARestaurante(id) {
  window.location.href = `restaurante-detalle.html?id=${id}`;
}

// Inicializar al cargar la página
renderizarRestaurantes();
