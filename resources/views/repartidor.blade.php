<!DOCTYPE html>
<html lang="es">
<head>
  <meta charset="UTF-8">
  <title>Registro Repartidor – Urban Eats</title>
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <link rel="stylesheet" href="{{ asset('css/styles.css') }}">
  <link rel="stylesheet" href="{{ asset('css/repartidor.css') }}">
  <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.0/css/all.min.css">
</head>
<body>

@include('partials.navbar')

<div class="pagina-form">
  <div class="form-wrapper">

    <div class="form-page-header">
      <h2><i class="fas fa-motorcycle"></i> Registro de Repartidor</h2>
      <p>Hola, <strong>{{ session('Nombres') }}</strong>. Ingresa los datos de tu vehículo para comenzar a repartir.</p>
    </div>

    @if ($errors->any())
      <div class="msg-verificacion msg-error" style="margin-bottom:1.2rem;">
        <i class="fas fa-exclamation-triangle"></i>
        <ul style="margin:0;padding-left:1rem;">
          @foreach ($errors->all() as $error)
            <li>{{ $error }}</li>
          @endforeach
        </ul>
      </div>
    @endif

    <div class="form-card">
      <div class="form-card-body">

        <form id="form-repartidor" action="{{ url('/repartidor') }}" method="POST">
          @csrf

          <div class="seccion-sep">
            <i class="fas fa-motorcycle"></i> Datos del Vehículo
          </div>

          <div class="form-grid">
            <div class="campo">
              <label for="rp-vehiculo">Tipo de vehículo <span style="color:red">*</span></label>
              <select id="rp-vehiculo" name="TipoVehiculo">
                <option value="">Seleccionar...</option>
                <option value="Moto" {{ old('TipoVehiculo') === 'Moto' ? 'selected' : '' }}>Moto</option>
                <option value="Bicicleta" {{ old('TipoVehiculo') === 'Bicicleta' ? 'selected' : '' }}>Bicicleta</option>
                <option value="Bici" {{ old('TipoVehiculo') === 'Bici' ? 'selected' : '' }}>Bici</option>
                <option value="Carro" {{ old('TipoVehiculo') === 'Carro' ? 'selected' : '' }}>Carro</option>
              </select>
              <span class="error">{{ $errors->first('TipoVehiculo') }}</span>
            </div>
            <div class="campo">
              <label for="rp-placa">Placa <span style="color:red">*</span></label>
              <input type="text" id="rp-placa" name="Placa"
                     placeholder="ABC-123"
                     value="{{ old('Placa') }}"
                     style="text-transform:uppercase;">
              <span class="error">{{ $errors->first('Placa') }}</span>
            </div>
          </div>

          <div class="campo">
            <label for="rp-soat">SOAT (número de póliza)</label>
            <div class="campo-icono">
              <i class="fas fa-file-alt"></i>
              <input type="text" id="rp-soat" name="SOAT"
                     placeholder="SOA-987654"
                     value="{{ old('SOAT') }}">
            </div>
            <span class="error">{{ $errors->first('SOAT') }}</span>
          </div>

          <div class="campo">
            <label for="rp-seguro">Seguro del vehículo</label>
            <div class="campo-icono">
              <i class="fas fa-shield-alt"></i>
              <input type="text" id="rp-seguro" name="SeguroVehiculo"
                     placeholder="Aseguradora / N° póliza"
                     value="{{ old('SeguroVehiculo') }}">
            </div>
            <span class="error">{{ $errors->first('SeguroVehiculo') }}</span>
          </div>

          <div class="form-btn-wrap" style="margin-top:1.5rem;">
            <a href="{{ url('/catalogo') }}" class="btn btn-gris">
              <i class="fas fa-arrow-left"></i> Cancelar
            </a>
            <button type="submit" class="btn btn-verde btn-grande">
              <i class="fas fa-check-circle"></i> Registrarme
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
<script src="{{ asset('js/repartidor.js') }}"></script>
</body>
</html>
