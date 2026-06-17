<!DOCTYPE html>
<html lang="es">
<head>
  <meta charset="UTF-8">
  <title>Seleccionar Restaurante – Urban Eats</title>
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <link rel="stylesheet" href="{{ asset('css/styles.css') }}">
  <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.0/css/all.min.css">
  <style>
    body {
      background: linear-gradient(135deg, var(--principal) 0%, var(--principal-oscuro) 100%);
      min-height: 100vh;
      display: flex;
      align-items: center;
      justify-content: center;
      padding: 2rem;
    }

    .seleccion-wrap {
      width: 100%;
      max-width: 560px;
      text-align: center;
    }

    .seleccion-logo {
      width: 80px;
      height: 80px;
      background: rgba(255,255,255,0.15);
      border-radius: 50%;
      display: flex;
      align-items: center;
      justify-content: center;
      margin: 0 auto 1.5rem;
      border: 2px solid rgba(255,255,255,0.3);
    }

    .seleccion-logo img {
      width: 56px;
      height: 56px;
      object-fit: contain;
    }

    .seleccion-subtitulo {
      color: rgba(255,255,255,0.8);
      font-size: 0.9rem;
      margin-bottom: 0.4rem;
    }

    .seleccion-titulo {
      color: #fff;
      font-size: 1.6rem;
      font-weight: 800;
      margin-bottom: 2rem;
    }

    .restaurantes-lista {
      display: flex;
      flex-direction: column;
      gap: 0.85rem;
    }

    .restaurante-opcion {
      background: rgba(255,255,255,0.12);
      border: 2px solid rgba(255,255,255,0.2);
      border-radius: 14px;
      padding: 1.2rem 1.5rem;
      display: flex;
      align-items: center;
      gap: 1rem;
      cursor: pointer;
      transition: background 0.2s, transform 0.15s, border-color 0.2s;
      width: 100%;
      text-align: left;
    }

    .restaurante-opcion:hover {
      background: rgba(255,255,255,0.22);
      border-color: rgba(255,255,255,0.5);
      transform: translateY(-3px);
    }

    .restaurante-icono {
      width: 52px;
      height: 52px;
      background: rgba(255,255,255,0.2);
      border-radius: 12px;
      display: flex;
      align-items: center;
      justify-content: center;
      font-size: 1.4rem;
      flex-shrink: 0;
      color: #fff;
    }

    .restaurante-info {
      flex: 1;
    }

    .restaurante-nombre {
      color: #fff;
      font-weight: 700;
      font-size: 1rem;
      margin-bottom: 0.2rem;
    }

    .restaurante-direccion {
      color: rgba(255,255,255,0.65);
      font-size: 0.82rem;
    }

    .restaurante-flecha {
      color: rgba(255,255,255,0.5);
      font-size: 0.9rem;
    }

    .btn-volver {
      display: inline-flex;
      align-items: center;
      gap: 0.5rem;
      color: rgba(255,255,255,0.7);
      font-size: 0.875rem;
      text-decoration: none;
      margin-top: 2rem;
      transition: color 0.2s;
    }

    .btn-volver:hover { color: #fff; }
  </style>
</head>
<body>
  <div class="seleccion-wrap">
    <div class="seleccion-logo">
      <img src="{{ asset('images/Logo.png') }}" alt="Urban Eats">
    </div>

    <p class="seleccion-subtitulo">Modo gerente</p>
    <h1 class="seleccion-titulo">¿Cuál restaurante manejarás hoy?</h1>

    <div class="restaurantes-lista">
      @foreach ($restaurantes as $restaurante)
        <form action="{{ url('/seleccion-restaurante/' . $restaurante->CodigoRestaurante) }}" method="POST">
          @csrf
          <button type="submit" class="restaurante-opcion">
            <div class="restaurante-icono"><i class="fas fa-store"></i></div>
            <div class="restaurante-info">
              <div class="restaurante-nombre">{{ $restaurante->Nombre }}</div>
              @if($restaurante->Direccion)
                <div class="restaurante-direccion"><i class="fas fa-map-marker-alt"></i> {{ $restaurante->Direccion }}</div>
              @endif
            </div>
            <i class="fas fa-chevron-right restaurante-flecha"></i>
          </button>
        </form>
      @endforeach
    </div>

    <a href="{{ url('/seleccion-rol') }}" class="btn-volver">
      <i class="fas fa-arrow-left"></i> Volver
    </a>
  </div>
</body>
</html>
