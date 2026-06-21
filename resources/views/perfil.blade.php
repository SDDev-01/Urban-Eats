<!DOCTYPE html>
<html lang="es">
<head>
  <meta charset="UTF-8">
  <title>Mi Perfil – Urban Eats</title>
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <link rel="stylesheet" href="{{ asset('css/styles.css') }}">
  <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.0/css/all.min.css">
  <link rel="stylesheet" href="{{ asset('css/pago.css') }}">
  <link rel="stylesheet" href="{{ asset('css/perfil.css') }}">
</head>
<body>

  @include('partials.navbar')

  <div class="pagina">
    <div class="perfil-container">
      
      <!-- Cabecera del perfil -->
      <div class="perfil-header">
        <div class="perfil-avatar">
          <i class="fas fa-user"></i>
        </div>
        <h1 class="perfil-nombre" id="perfil-nombre">{{ $usuario->Nombres }}</h1>
        <p class="perfil-tipo">Cliente de Urban Eats</p>
      </div>

      <!-- Información personal -->
      <div class="perfil-card">
        <h3><i class="fas fa-id-card"></i> Información Personal</h3>

        @if (session('exito'))
          <div class="msg-verificacion msg-exito" style="margin-bottom:1.2rem;">
            <i class="fas fa-check-circle"></i> {{ session('exito') }}
          </div>
        @endif

        {{-- Vista de datos --}}
        <div id="perfil-vista">
          <div class="perfil-info-grid">
            <div class="perfil-info-item">
              <span class="perfil-info-label">Nombres</span>
              <span class="perfil-info-value">{{ $usuario->Nombres }}</span>
            </div>
            <div class="perfil-info-item">
              <span class="perfil-info-label">Apellidos</span>
              <span class="perfil-info-value">{{ $usuario->Apellidos }}</span>
            </div>
            <div class="perfil-info-item">
              <span class="perfil-info-label">Email</span>
              <span class="perfil-info-value">{{ $usuario->Correo }}</span>
            </div>
            <div class="perfil-info-item">
              <span class="perfil-info-label">Teléfono</span>
              <span class="perfil-info-value">{{ $usuario->telefono->first()?->Telefono ?? '—' }}</span>
            </div>
            <div class="perfil-info-item full">
              <span class="perfil-info-label">Dirección</span>
              <span class="perfil-info-value">{{ $usuario->direccion->first()?->Direccion ?? '—' }}</span>
            </div>
          </div>
          <div style="margin-top:1.5rem;">
            <button type="button" class="btn btn-verde btn-grande" id="btn-mostrar-edicion">
              <i class="fas fa-edit"></i> Editar Información
            </button>
          </div>
        </div>

        {{-- Formulario de edición --}}
        <div id="perfil-edicion" style="display:none;">
          <form action="{{ url('/perfil') }}" method="POST">
            @csrf
            @method('PATCH')

            @if ($errors->any())
              <div class="msg-verificacion msg-error" style="margin-bottom:1rem;">
                <i class="fas fa-exclamation-triangle"></i>
                <ul style="margin:0;padding-left:1rem;">
                  @foreach ($errors->all() as $error)
                    <li>{{ $error }}</li>
                  @endforeach
                </ul>
              </div>
            @endif

            <div class="form-grid">
              <div class="campo">
                <label>Nombres <span style="color:red">*</span></label>
                <input type="text" name="Nombres" value="{{ old('Nombres', $usuario->Nombres) }}" required>
              </div>
              <div class="campo">
                <label>Apellidos <span style="color:red">*</span></label>
                <input type="text" name="Apellidos" value="{{ old('Apellidos', $usuario->Apellidos) }}" required>
              </div>
            </div>
            <div class="campo">
              <label>Email <span style="color:red">*</span></label>
              <div class="campo-icono">
                <i class="fas fa-envelope"></i>
                <input type="email" name="Correo" value="{{ old('Correo', $usuario->Correo) }}" required>
              </div>
            </div>
            <div class="campo">
              <label>Teléfono</label>
              <div class="campo-icono">
                <i class="fas fa-phone"></i>
                <input type="text" name="Telefono" maxlength="10"
                       value="{{ old('Telefono', $usuario->telefono->first()?->Telefono) }}"
                       placeholder="10 dígitos">
              </div>
            </div>
            <div class="campo">
              <label>Dirección</label>
              <div class="campo-icono">
                <i class="fas fa-map-marker-alt"></i>
                <input type="text" name="Direccion"
                       value="{{ old('Direccion', $usuario->direccion->first()?->Direccion) }}"
                       placeholder="Tu dirección de entrega">
              </div>
            </div>

            <div style="display:flex;gap:0.75rem;margin-top:1.5rem;flex-wrap:wrap;">
              <button type="submit" class="btn btn-verde btn-grande">
                <i class="fas fa-save"></i> Guardar cambios
              </button>
              <button type="button" class="btn btn-gris" id="btn-cancelar-edicion">
                Cancelar
              </button>
            </div>
          </form>
        </div>
      </div>

      <!-- Otras opciones -->
      <div class="perfil-card">
        <h3><i class="fas fa-rocket"></i> ¿Quieres hacer más?</h3>
        <div class="opciones-grid">
          <a href="{{ url('/repartidor') }}" class="opcion-card">
            <div class="opcion-icon"><i class="fas fa-motorcycle" style="color: #3498db;"></i></div>
            <div class="opcion-titulo">Sé Repartidor</div>
            <div class="opcion-desc">Gana dinero entregando pedidos</div>
          </a>
          <a href="{{ url('/restaurante') }}" class="opcion-card">
            <div class="opcion-icon"><i class="fas fa-store" style="color: #e67e22;"></i></div>
            <div class="opcion-titulo">Registra tu Restaurante</div>
            <div class="opcion-desc">Vende tu comida saludable</div>
          </a>
        </div>
      </div>

<!-- Cerrar sesión -->
      <div class="perfil-card perfil-logout">
        <form action="{{ url('/logout') }}" method="POST">
          @csrf
          <button type="submit" class="btn btn-verde btn-grande">
            <i class="fas fa-sign-out-alt"></i> Cerrar Sesión
          </button>
        </form>
      </div>

    </div>
  </div>

  <footer>
    <p>&copy; 2026 Urban Eats – Proyecto Formativo SENA. Comida saludable para todos.</p>
  </footer>

  <script src="{{ asset('js/app.js') }}"></script>
  <script src="{{ asset('js/perfil.js') }}"></script>
  <script>
    const btnMostrar   = document.getElementById('btn-mostrar-edicion');
    const btnCancelar  = document.getElementById('btn-cancelar-edicion');
    const vista        = document.getElementById('perfil-vista');
    const edicion      = document.getElementById('perfil-edicion');

    btnMostrar?.addEventListener('click', () => {
      vista.style.display   = 'none';
      edicion.style.display = 'block';
    });
    btnCancelar?.addEventListener('click', () => {
      edicion.style.display = 'none';
      vista.style.display   = 'block';
    });

    // Si hay errores de validación, abrir el formulario automáticamente
    @if ($errors->any())
      vista.style.display   = 'none';
      edicion.style.display = 'block';
    @endif
  </script>
</body>
</html>
