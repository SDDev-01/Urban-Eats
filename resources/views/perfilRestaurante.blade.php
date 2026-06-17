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
          <h1 id="rest-nombre">{{ $restaurante->Nombre ?? 'Mi Restaurante' }}</h1>
          <p id="rest-descripcion">{{ $restaurante->Horario ?? '' }}</p>
          <div class="rest-meta">
            <span><i class="fas fa-map-marker-alt"></i> <span id="rest-direccion">{{ $restaurante->Direccion ?? 'Dirección del restaurante' }}</span></span>
          </div>
        </div>

        <div class="rest-estado-wrap">
          <a href="{{ url('/menu') }}" class="btn btn-verde" style="margin-bottom:0.5rem;">
            <i class="fas fa-utensils"></i> Crear Menú
          </a>
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

  <!-- ===================== MENÚS Y PLATOS ===================== -->
  <div class="gestion-wrap">

    @if(session('exito'))
      <div class="gestion-alerta gestion-alerta-exito">
        <i class="fas fa-check-circle"></i> {{ session('exito') }}
      </div>
    @endif

    {{-- TABLAS DE MENÚS --}}
    <div class="gestion-bloque">
      <h2 class="gestion-titulo"><i class="fas fa-book-open"></i> Menús y Platos</h2>

      @forelse($menus as $menu)
        <div class="menu-tabla-wrap">
          <div class="menu-tabla-header">
            <span><i class="fas fa-tag"></i> {{ $menu->Categoria }}</span>
            <span class="menu-tabla-count">{{ $menu->plato->count() }} plato(s)</span>
          </div>

          @if($menu->plato->isEmpty())
            <p class="tabla-vacia-msg">Este menú aún no tiene platos.</p>
          @else
            <div class="tabla-scroll">
              <table class="gestion-tabla">
                <thead>
                  <tr>
                    <th>#</th>
                    <th>Nombre</th>
                    <th>Tipo de Comida</th>
                    <th>Precio</th>
                    <th>Disponibilidad</th>
                    <th>Descripción</th>
                    <th></th>
                  </tr>
                </thead>
                <tbody>
                  @foreach($menu->plato as $i => $plato)
                    <tr>
                      <td>{{ $i + 1 }}</td>
                      <td><strong>{{ $plato->Nombre }}</strong></td>
                      <td>{{ $plato->TipoComida ?? '—' }}</td>
                      <td>${{ number_format($plato->Precio ?? 0, 0, ',', '.') }}</td>
                      <td>
                        <form action="{{ url('/perfilRestaurante/plato/'.$plato->CodigoPlato) }}" method="POST" class="form-disponibilidad-inline">
                          @csrf
                          @method('PATCH')
                          <select name="Disponibilidad" class="select-disponibilidad" onchange="this.form.submit()">
                            <option value="Disponible" {{ $plato->Disponibilidad === 'Disponible' ? 'selected' : '' }}>Disponible</option>
                            <option value="No disponible" {{ $plato->Disponibilidad === 'No disponible' ? 'selected' : '' }}>No disponible</option>
                            <option value="Temporalmente agotado" {{ $plato->Disponibilidad === 'Temporalmente agotado' ? 'selected' : '' }}>Temp. agotado</option>
                          </select>
                        </form>
                      </td>
                      <td>{{ $plato->Descripcion ?? '—' }}</td>
                      <td>
                        <form action="{{ url('/perfilRestaurante/plato/'.$plato->CodigoPlato) }}" method="POST"
                              onsubmit="return confirm('¿Eliminar «{{ $plato->Nombre }}»? Esta acción no se puede deshacer.')">
                          @csrf
                          @method('DELETE')
                          <button type="submit" class="btn-plato-eliminar" title="Eliminar plato">
                            <i class="fas fa-trash"></i>
                          </button>
                        </form>
                      </td>
                    </tr>
                  @endforeach
                </tbody>
              </table>
            </div>
          @endif
        </div>
      @empty
        <p class="tabla-vacia-msg">Este restaurante aún no tiene menús registrados. <a href="{{ url('/menu') }}">Crear menú</a></p>
      @endforelse
    </div>

    {{-- FORMULARIO: CREAR PLATO --}}
    @if($menus->isNotEmpty())
      <div class="gestion-bloque">
        <h2 class="gestion-titulo"><i class="fas fa-plus-circle"></i> Agregar Plato</h2>

        <form action="{{ url('/perfilRestaurante/plato') }}" method="POST" class="form-plato-rapido">
          @csrf

          <div class="campo">
            <label for="p-menu">Menú</label>
            <select id="p-menu" name="CodigoMenu">
              @foreach($menus as $menu)
                <option value="{{ $menu->CodigoMenu }}">{{ $menu->Categoria }}</option>
              @endforeach
            </select>
          </div>

          <div class="form-plato-grid">
            <div class="campo">
              <label for="p-nombre">Nombre <span style="color:red">*</span></label>
              <input type="text" id="p-nombre" name="Nombre"
                     placeholder="Ej: Bandeja Paisa" value="{{ old('Nombre') }}">
            </div>
            <div class="campo">
              <label for="p-tipo">Tipo de comida</label>
              <input type="text" id="p-tipo" name="TipoComida"
                     placeholder="Ej: Plato fuerte" value="{{ old('TipoComida') }}">
            </div>
            <div class="campo">
              <label for="p-precio">Precio (COP)</label>
              <input type="number" id="p-precio" name="Precio"
                     step="100" min="0" placeholder="Ej: 18000" value="{{ old('Precio') }}">
            </div>
            <div class="campo">
              <label for="p-disponibilidad">Disponibilidad</label>
              <select id="p-disponibilidad" name="Disponibilidad">
                <option value="Disponible">Disponible</option>
                <option value="No disponible">No disponible</option>
                <option value="Temporalmente agotado">Temporalmente agotado</option>
              </select>
            </div>
            <div class="campo form-plato-full">
              <label for="p-descripcion">Descripción</label>
              <input type="text" id="p-descripcion" name="Descripcion"
                     placeholder="Descripción breve del plato" value="{{ old('Descripcion') }}">
            </div>
          </div>

          <div class="form-btn-wrap" style="margin-top:1rem;">
            <button type="submit" class="btn btn-verde">
              <i class="fas fa-check"></i> Crear Plato
            </button>
          </div>
        </form>
      </div>
    @endif

  </div>
  <!-- ============================================================== -->

  <footer>
    <p>&copy; 2026 Urban Eats – Proyecto Formativo SENA. Comida saludable para todos.</p>
  </footer>

  <script src="{{ asset('js/app.js') }}"></script>
  <script>window.PEDIDOS_BD = @json($pedidosJS);</script>
  <script src="{{ asset('js/perfil-restaurante.js') }}"></script>
</body>
</html>
