const PALETA = ['#d5f5e3', '#fef9e7', '#e8f4fd', '#fdecea', '#f0e6ff', '#fff3e0'];

function renderizarRestaurantes() {
  const grid = document.getElementById('restaurantes-grid');
  const restaurantes = window.RESTAURANTES_BD || [];

  grid.innerHTML = '';

  if (restaurantes.length === 0) {
    grid.innerHTML = '<p style="text-align:center;padding:3rem;color:var(--texto-gris);">No hay restaurantes disponibles.</p>';
    return;
  }

  restaurantes.forEach((restaurante, i) => {
    const color = PALETA[i % PALETA.length];
    const card = document.createElement('div');
    card.className = 'restaurante-card';
    const emojisHtml = (restaurante.emojisComida || []).map((emoji, i) =>
      `<span class="emoji-decorativo emoji-pos-${i}">${emoji}</span>`
    ).join('');

    const headerContent = restaurante.imagen
      ? `<img src="${restaurante.imagen}" alt="${restaurante.nombre}" style="width:100%;height:100%;object-fit:cover;display:block;">`
      : `${emojisHtml}<span class="restaurante-logo-principal">${restaurante.logo || '🍽️'}</span>`;

    card.innerHTML = `
      <div class="restaurante-header" style="${restaurante.imagen ? '' : `background: ${restaurante.color};`}">
        ${headerContent}
        <span class="restaurante-badge">Abierto</span>
      </div>
      <div class="restaurante-body">
        <h3 class="restaurante-nombre">${restaurante.nombre}</h3>
        <p class="restaurante-descripcion">${restaurante.horario || 'Horario no disponible'}</p>
        <div class="restaurante-info">
          <div class="restaurante-info-item">
            <i class="fas fa-map-marker-alt"></i>
            <span>${restaurante.direccion || '—'}</span>
          </div>
          <div class="restaurante-info-item">
            <i class="fas fa-clock"></i>
            <span>${restaurante.horario || '—'}</span>
          </div>
        </div>
        <button class="btn-ver-menu" data-id="${restaurante.id}">
          <i class="fas fa-arrow-right"></i> Ver Menú
        </button>
      </div>
    `;

    card.addEventListener('click', (e) => {
      if (!e.target.closest('.btn-ver-menu')) {
        navegarARestaurante(restaurante.id);
      }
    });

    grid.appendChild(card);
  });

  document.querySelectorAll('.btn-ver-menu').forEach(btn => {
    btn.addEventListener('click', (e) => {
      e.stopPropagation();
      navegarARestaurante(parseInt(btn.dataset.id));
    });
  });
}

function navegarARestaurante(id) {
  window.location.href = `/restauranteDetalle?id=${id}`;
}

renderizarRestaurantes();
