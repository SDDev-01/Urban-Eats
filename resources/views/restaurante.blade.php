<!DOCTYPE html>
<html lang="es">
<head>
  <meta charset="UTF-8">
  <title>Restaurante – Urban Eats</title>
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <link rel="stylesheet" href="{{ asset('css/styles.css') }}">
  <link rel="stylesheet" href="{{ asset('css/restaurante.css') }}">
  <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.0/css/all.min.css">
</head>
<body>

@include('partials.navbar')

  <div class="pagina-form">
    <div class="form-wrapper">

      <div class="form-page-header">
        <h2><i class="fas fa-store"></i> Datos del Restaurante</h2>
        <p>Ingresa o actualiza la información del restaurante.</p>
      </div>

      <div class="form-card">
        <div class="form-card-body">
          <form id="form-restaurante" action="{{ url('/restaurante') }}" method="POST">
            @csrf

            <div class="campo">
              <label for="r-nombre">Nombre del restaurante</label>
              <input type="text" id="r-nombre" name="Nombre" placeholder="Ej: Green Kitchen">
              <span class="error" id="err-r-nombre"></span>
            </div>

            <div class="campo">
              <label for="r-direccion">Dirección</label>
              <div class="campo-icono">
                <i class="fas fa-map-marker-alt"></i>
                <input type="text" id="r-direccion" name="Ubicacion" placeholder="Calle 72 # 5-80, Bogotá">
              </div>
              <span class="error" id="err-r-dir"></span>
            </div>

            <div class="campo">
              <label for="r-horario">Horario de atención</label>
              <div class="campo-icono">
                <i class="fas fa-clock"></i>
                <input type="text" id="r-horario" name="Horario" placeholder="Ej: 12:00 AM – 5:00 AM">
              </div>
              <span class="error" id="err-r-horario"></span>
            </div>

            <!-- Menú editable -->
            <div class="campo">
              <label>Menú</label>
              <div class="menu-chips-wrap" id="menu-chips"></div>
              <div class="menu-add-row" id="menu-add-row">
                <input type="text" id="input-menu" placeholder="Agregar ítem al menú...">
                <button type="button" class="btn btn-verde" id="btn-add-item" style="padding:0.6rem 0.9rem;">
                  <i class="fas fa-plus"></i>
                </button>
              </div>
            </div>

            <!-- Mensaje de verificación -->
            <div id="msg-verificacion" class="msg-verificacion" style="display:none;"></div>

            <!-- Botón al final del formulario -->
            <div class="form-btn-wrap">
              <button type="button" class="btn btn-verde btn-grande" id="btn-verificar">
                <i class="fas fa-check-circle"></i> Aceptar
              </button>
            </div>

          </form>
        </div>
      </div>

    </div>
  </div>

  <footer><p>&copy; 2026 Urban Eats – Proyecto Formativo SENA</p></footer>
  <div id="toast-container"></div>
  <script src="{{ asset('js/app.js') }}"></script>
  <script src="{{ asset('js/restaurante.js') }}"></script>
</body>
</html>