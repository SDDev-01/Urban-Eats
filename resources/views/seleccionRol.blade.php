<!DOCTYPE html>
<html lang="es">
<head>
  <meta charset="UTF-8">
  <title>¿Cómo deseas ingresar? – Urban Eats</title>
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
      max-width: 520px;
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

    .seleccion-saludo {
      color: rgba(255,255,255,0.85);
      font-size: 0.95rem;
      margin-bottom: 0.4rem;
    }

    .seleccion-titulo {
      color: #fff;
      font-size: 1.7rem;
      font-weight: 800;
      margin-bottom: 2.5rem;
    }

    .seleccion-opciones {
      display: grid;
      grid-template-columns: 1fr 1fr;
      gap: 1.25rem;
    }

    .opcion-card {
      background: rgba(255,255,255,0.12);
      border: 2px solid rgba(255,255,255,0.25);
      border-radius: 16px;
      padding: 2rem 1.25rem;
      text-decoration: none;
      color: #fff;
      display: flex;
      flex-direction: column;
      align-items: center;
      gap: 1rem;
      transition: background 0.2s, transform 0.15s, border-color 0.2s;
      cursor: pointer;
    }

    .opcion-card:hover {
      background: rgba(255,255,255,0.22);
      border-color: rgba(255,255,255,0.55);
      transform: translateY(-4px);
      color: #fff;
    }

    .opcion-icono {
      width: 64px;
      height: 64px;
      background: rgba(255,255,255,0.2);
      border-radius: 50%;
      display: flex;
      align-items: center;
      justify-content: center;
      font-size: 1.6rem;
    }

    .opcion-nombre {
      font-weight: 700;
      font-size: 1rem;
    }

    .opcion-desc {
      font-size: 0.8rem;
      color: rgba(255,255,255,0.7);
      line-height: 1.4;
    }

    @media (max-width: 480px) {
      .seleccion-opciones { grid-template-columns: 1fr; }
    }
  </style>
</head>
<body>
  <div class="seleccion-wrap">
    <div class="seleccion-logo">
      <img src="{{ asset('images/Logo.png') }}" alt="Urban Eats">
    </div>

    <p class="seleccion-saludo">Bienvenido, {{ session('Nombres') }}</p>
    <h1 class="seleccion-titulo">¿Cómo deseas ingresar?</h1>

    <div class="seleccion-opciones">
      <a href="{{ url('/catalogo') }}" class="opcion-card">
        <div class="opcion-icono"><i class="fas fa-user"></i></div>
        <div>
          <div class="opcion-nombre">Como usuario</div>
          <div class="opcion-desc">Explora restaurantes y realiza pedidos</div>
        </div>
      </a>

      <a href="{{ url('/seleccion-restaurante') }}" class="opcion-card">
        <div class="opcion-icono"><i class="fas fa-store"></i></div>
        <div>
          <div class="opcion-nombre">Como gerente</div>
          <div class="opcion-desc">Administra tu restaurante y gestiona pedidos</div>
        </div>
      </a>
    </div>
  </div>
</body>
</html>
