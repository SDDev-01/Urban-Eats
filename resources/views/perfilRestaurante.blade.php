<!DOCTYPE html>
<html lang="es">
<head>
  <meta charset="UTF-8">
  <title>Mi Restaurante – Urban Eats</title>
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <link rel="stylesheet" href="{{ asset('css/styles.css') }}">
  <link rel="stylesheet" href="{{ asset('css/perfil-restaurante.css') }}">
  <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.0/css/all.min.css">
</head>
<body>

@include('partials.navbar')

  <div class="pagina">

    <!-- HERO DEL RESTAURANTE -->
    <div class="rest-hero">
      <div class="rest-hero-inner">

        <!-- =====================================================
             LOGO DEL RESTAURANTE
             Reemplaza el bloque <div class="rest-logo-icon">
             por: <img src="images/NOMBRE-DE-TU-LOGO.png" alt="Logo restaurante" class="rest-logo-img">
             ===================================================== -->
        <div class="rest-logo-icon">
          <i class="fas fa-store"></i>
        </div>

        <div class="rest-hero-info">
          <h1 id="rest-nombre">Nombre del Restaurante</h1>
          <p id="rest-descripcion">Comida saludable y deliciosa para todos</p>
          <div class="rest-meta">
            <span><i class="fas fa-map-marker-alt"></i> <span id="rest-direccion">Dirección del restaurante</span></span>
            <span><i class="fas fa-phone"></i> <span id="rest-telefono">000 000 0000</span></span>
          </div>
        </div>

        <div class="rest-estado-wrap">
          <button class="rest-toggle-estado" id="btn-estado">
            <span class="estado-dot" id="estado-dot"></span>
            <span id="estado-texto">Abierto</span>
          </button>
          <p class="estado-hint" id="estado-hint">Clic para cerrar</p>
        </div>

      </div>
    </div>

    <!-- TABS -->
    <div class="tabs-bar">
      <button class="tab-btn activo" data-tab="general">
        <i class="fas fa-chart-bar"></i> Vista General
      </button>
      <button class="tab-btn" data-tab="pedidos">
        <i class="fas fa-receipt"></i> Gestión de Pedidos
        <span class="badge-pedidos" id="badge-pedidos">0</span>
      </button>
    </div>

    <!-- ======================== ZONA 1: VISTA GENERAL ======================== -->
    <div class="tab-content activo" id="tab-general">
      <div class="zona-wrapper">

        <!-- Estadísticas -->
        <div class="stats-grid">
          <div class="stat-card">
            <div class="stat-icon" style="background: var(--terciario-claro); color: var(--terciario);">
              <i class="fas fa-check-circle"></i>
            </div>
            <div class="stat-info">
              <span class="stat-valor" id="stat-entregados">0</span>
              <span class="stat-label">Pedidos entregados hoy</span>
            </div>
          </div>
          <div class="stat-card">
            <div class="stat-icon" style="background: var(--secundario-claro); color: #A42828;">
              <i class="fas fa-dollar-sign"></i>
            </div>
            <div class="stat-info">
              <span class="stat-valor" id="stat-ventas">$0</span>
              <span class="stat-label">Ventas del día</span>
            </div>
          </div>
          <div class="stat-card">
            <div class="stat-icon" style="background: var(--principal-claro); color: var(--principal);">
              <i class="fas fa-clock"></i>
            </div>
            <div class="stat-info">
              <span class="stat-valor" id="stat-pendientes">0</span>
              <span class="stat-label">Pedidos pendientes</span>
            </div>
          </div>
          <div class="stat-card">
            <div class="stat-icon" style="background: var(--terciario-claro); color: #F7643B;">
              <i class="fas fa-users"></i>
            </div>
            <div class="stat-info">
              <span class="stat-valor" id="stat-clientes">0</span>
              <span class="stat-label">Clientes hoy</span>
            </div>
          </div>
        </div>

        <!-- Clientes del día -->
        <div class="zona-card">
          <h3><i class="fas fa-users"></i> Clientes del día</h3>
          <div class="tabla-wrap">
            <table class="tabla-clientes">
              <thead>
                <tr>
                  <th>Cliente</th>
                  <th>Dirección</th>
                  <th>Teléfono</th>
                  <th>Pedido</th>
                  <th>Total</th>
                  <th>Hora</th>
                  <th>Estado</th>
                </tr>
              </thead>
              <tbody id="tabla-clientes-body">
              </tbody>
            </table>
          </div>
        </div>

      </div>
    </div>

    <!-- ======================== ZONA 2: GESTIÓN DE PEDIDOS ======================== -->
    <div class="tab-content" id="tab-pedidos">
      <div class="zona-wrapper">

        <div class="pedidos-filtros">
          <button class="filtro-btn activo" data-filtro="todos">Todos</button>
          <button class="filtro-btn" data-filtro="nuevo">Nuevos</button>
          <button class="filtro-btn" data-filtro="aceptado">Aceptados</button>
          <button class="filtro-btn" data-filtro="preparando">Preparando</button>
          <button class="filtro-btn" data-filtro="listo">Listos</button>
          <button class="filtro-btn" data-filtro="entregado">Entregados</button>
        </div>

        <div class="pedidos-lista" id="pedidos-lista">
        </div>

      </div>
    </div>

  </div>

  <footer>
    <p>&copy; 2026 Urban Eats – Proyecto Formativo SENA. Comida saludable para todos.</p>
  </footer>

  <script src="{{ asset('js/app.js') }}"></script>
  <script src="{{ asset('js/perfil-restaurante.js') }}"></script>
</body>
</html>
