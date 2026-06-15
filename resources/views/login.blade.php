<!DOCTYPE html>
<html lang="es">
<head>
  <meta charset="UTF-8">
  <title>Iniciar Sesión – Urban Eats</title>
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <link rel="stylesheet" href="{{ asset('css/styles.css') }}">
  <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.0/css/all.min.css">
  <link rel="stylesheet" href="{{ asset('css/login.css') }}">
</head>
<body>

  <div class="login-card">
    <div class="login-logo"><img src="{{ asset('images/Logo.png') }}" alt="Urban Eats"></div>
    <h1>Urban Eats</h1>
    <p class="subtitulo">Comida saludable para tu día a día</p>

  <form id="form-login" action="{{ url('/login') }}" method="POST" autocomplete="off">
    @csrf
      <div class="campo">
        <label for="email-login">Email</label>
        <div class="campo-icono">
          <i class="fas fa-envelope"></i>
          <input type="email" id="email-login" name="email" placeholder="tu@email.com" autocomplete="email" required>
        </div>
        <span class="error" id="error-email-login"></span>
      </div>

      <div class="campo">
        <label for="password-login">Contraseña</label>
        <div class="campo-icono">
          <i class="fas fa-lock"></i>
          <input type="password" id="password-login" name="password" placeholder="••••••••" autocomplete="current-password" required>
        </div>
        <span class="error" id="error-pass-login"></span>
      </div>

      <button type="submit" class="btn-login">Iniciar Sesión</button>
      <p class="exito-msg" id="login-exito"></p>

      <div class="login-footer">
        ¿No tienes cuenta? <a href="{{ url('/registro') }}">Regístrate aquí</a>
      </div>
    </form>
  </div>

  <div class="demo-nota">
    🌙 <strong>Abierto 24/7</strong> – Pedidos para trabajadores y estudiantes nocturnos
  </div>

  <div id="toast-container"></div>
  <script src="{{ asset('js/app.js') }}"></script>
</body>
</html>
