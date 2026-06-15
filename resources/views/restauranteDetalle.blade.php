<!DOCTYPE html>
<html lang="es">
<head>
  <meta charset="UTF-8">
  <title>Restaurante – Urban Eats</title>
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <link rel="stylesheet" href="{{ asset('css/styles.css') }}">
  <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.0/css/all.min.css">
  <link rel="stylesheet" href="{{ asset('css/restaurante-detalle.css') }}">
</head>
<body>

  <!-- NAVBAR -->
@include('partials.navbar')

  <div style="padding-top: var(--navbar-height);">
    <!-- HEADER DEL RESTAURANTE -->
    <div class="restaurante-hero" id="restaurante-hero">
      <!-- Se genera dinámicamente con JS -->
    </div>

    <!-- BOTÓN VOLVER -->
    <div class="volver-section">
      <a href="{{ url('/restaurantes') }}" class="btn-volver">
        <i class="fas fa-arrow-left"></i> Volver a Restaurantes
      </a>
    </div>

    <!-- FILTROS DE CATEGORÍAS -->
    <div class="filtros-wrap">
      <button class="btn-filtro activo" data-categoria="todos">Todos</button>
      <button class="btn-filtro" data-categoria="Bowls">Bowls</button>
      <button class="btn-filtro" data-categoria="Platos Principales">Platos Principales</button>
      <button class="btn-filtro" data-categoria="Desayunos">Desayunos</button>
      <button class="btn-filtro" data-categoria="Wraps">Wraps</button>
      <button class="btn-filtro" data-categoria="Bebidas">Bebidas</button>
    </div>

    <!-- GRID DE PRODUCTOS -->
    <div class="productos-grid" id="productos-grid">
      <!-- Se genera dinámicamente con JS -->
    </div>
  </div>

  <!-- MODAL DETALLE PRODUCTO -->
  <div class="modal-overlay" id="modal-detalle">
    <div class="modal" style="max-width: 660px;">
      <div class="modal-header">
        <h3 id="modal-titulo">Detalle del producto</h3>
        <button class="modal-cerrar" id="modal-cerrar-btn"><i class="fas fa-times"></i></button>
      </div>
      <div class="detalle-grid">
        <div id="modal-imagen-wrap"></div>
        <div class="detalle-info">
          <span class="categoria-tag" id="modal-categoria"></span>
          <h2 id="modal-nombre"></h2>
          <p id="modal-descripcion"></p>
          <div class="detalle-stats" id="modal-stats"></div>
          <div class="cantidad-control">
            <label>Cantidad</label>
            <button class="btn-cantidad" id="btn-menos"><i class="fas fa-minus"></i></button>
            <span id="detalle-cantidad">1</span>
            <button class="btn-cantidad" id="btn-mas"><i class="fas fa-plus"></i></button>
          </div>
          <div class="detalle-precio" id="modal-precio"></div>
          <button class="btn btn-verde btn-grande" id="btn-agregar-carrito">
            <i class="fas fa-shopping-cart"></i> Agregar al Carrito
          </button>
        </div>
      </div>
    </div>
  </div>

  <footer>
    <p>&copy; 2026 Urban Eats – Proyecto Formativo SENA. Comida saludable para todos.</p>
  </footer>

  <div id="toast-container"></div>
  
  <script src="{{ asset('js/data-restaurantes.js') }}"></script>
  <script src="{{ asset('js/app.js') }}"></script>
  <script src="{{ asset('js/restaurante-detalle.js') }}"></script>
</body>
</html>
