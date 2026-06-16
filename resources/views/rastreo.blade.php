<!DOCTYPE html>
<html lang="es">
<head>
  <meta charset="UTF-8">
  <title>Seguimiento – Urban Eats</title>
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <link rel="stylesheet" href="{{ asset('css/styles.css') }}">
  <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.0/css/all.min.css">
  <link rel="stylesheet" href="{{ asset('css/rastreo.css') }}">
</head>
<body>

@include('partials.navbar')

  <div class="pagina">
    <div class="rastreo-titulo">
      <h2>Seguimiento del Pedido</h2>
      <p id="pedido-id-label">Pedido #ORD-000000000000</p>
    </div>

    <div class="rastreo-layout">
      <!-- Columna izquierda: resumen del envío -->
      <div>
        <div class="resumen-card-rastreo" style="margin-bottom:1.5rem;">
          <h3><i class="fas fa-box-open" style="color:var(--principal);margin-right:0.5rem;"></i> Resumen del Envío</h3>
          <div id="envio-items"></div>
        </div>
      </div>

      <!-- Columna derecha: timer, pasos, resumen -->
      <div>
        <!-- Temporizador -->
        <div class="timer-card">
          <div><i class="fas fa-clock"></i></div>
          <div class="timer-numero" id="timer-display">35:00</div>
          <div class="timer-label">minutos restantes</div>
        </div>

        <!-- Estado y alerta -->
        <div class="estado-card">
          <div class="alerta-estado">
            <i class="fas fa-bicycle"></i>
            <div>
              <h4>Tu pedido está en camino</h4>
              <p>El repartidor va hacia tu ubicación</p>
            </div>
          </div>

          <div class="pasos" id="pasos-pedido">
            <!-- Se renderizan desde JS -->
          </div>
        </div>

        <!-- Resumen del pedido -->
        <div class="resumen-card-rastreo">
          <h3>Resumen del Pedido</h3>
          <div id="rastreo-items"></div>
          <div class="resumen-total-rastreo">
            <span>Total</span>
            <span class="monto" id="rastreo-total">$0 COP</span>
          </div>
        </div>
      </div>
    </div>
  </div>

  <footer><p>&copy; 2026 Urban Eats – Proyecto Formativo SENA</p></footer>
  <div id="toast-container"></div>
  <script src="{{ asset('js/app.js') }}"></script>
  <script>window.PEDIDO_BD = @json($pedidoData);</script>
  <script src="{{ asset('js/rastreo.js') }}"></script>
</body>
</html>
