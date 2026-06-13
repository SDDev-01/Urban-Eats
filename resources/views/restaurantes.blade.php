<!DOCTYPE html>
<html lang="es">
<head>
  <meta charset="UTF-8">
  <title>Restaurantes – Urban Eats</title>
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <link rel="stylesheet" href="{{ asset('css/styles.css') }}">
  <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.0/css/all.min.css">
  <link rel="stylesheet" href="{{ asset('css/restaurantes.css') }}">
</head>
<body>

  <!-- NAVBAR -->
  <nav class="navbar">
    <a href="{{ url('/catalogo') }}" class="navbar-logo"><img src="{{ asset('images/Logo.png') }}" alt="Urban Eats"><span>Urban Eats</span></a>
    <div class="navbar-links">
      <a href="{{ url('/restaurantes') }}" class="navbar-link"><i class="fas fa-store"></i> Restaurantes</a>
    </div>
    <div class="navbar-right">
      <a href="perfil.php" class="navbar-usuario" style="text-decoration:none;">
        <i class="fas fa-user-circle"></i> <span id="navbar-nombre">Usuario</span>
      </a>
      <a href="{{ url('/carrito') }}" class="btn-carrito" aria-label="Ver carrito">
        <i class="fas fa-shopping-cart"></i>
        <span class="carrito-badge" id="carrito-badge">0</span>
      </a>
    </div>
  </nav>

  <!-- HERO SECTION -->
  <div style="padding-top: var(--navbar-height);">
    <div class="hero-restaurantes">
      <h1><i class="fas fa-store"></i> Nuestros Restaurantes</h1>
      <p>Descubre los mejores lugares de comida saludable en Bogotá</p>
    </div>

    <!-- GRID DE RESTAURANTES -->
    <div class="restaurantes-grid" id="restaurantes-grid">
      <!-- Las tarjetas se generan dinámicamente con JS -->
    </div>
  </div>

  <footer>
    <p>&copy; 2026 Urban Eats – Proyecto Formativo SENA. Comida saludable para todos.</p>
  </footer>

  <div id="toast-container"></div>
  
  <script src="{{ asset('js/data-restaurantes.js') }}"></script>
  <script src="{{ asset('js/app.js') }}"></script>
  <script src="{{ asset('js/restaurantes.js') }}"></script>
</body>
</html>
