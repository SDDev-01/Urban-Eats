<!DOCTYPE html>
<html lang="es">
<head>
  <meta charset="UTF-8">
  <meta name="csrf-token" content="{{ csrf_token() }}">
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
      <p id="pedido-id-label">Pedido #—</p>
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

        {{-- ── ESTADO: BUSCANDO REPARTIDOR ─── --}}
        <div id="estado-buscando" style="display:none;">
          <div class="timer-card" style="background: linear-gradient(135deg,#fff3e0,#ffe0b2);">
            <div><i class="fas fa-search" style="color:#e65100;animation:spin 2s linear infinite;"></i></div>
            <div class="timer-numero" style="color:#e65100;font-size:2rem;margin:0.5rem 0;">Buscando...</div>
            <div class="timer-label" style="opacity:0.8;">Esperando que un repartidor tome tu pedido</div>
          </div>
          <div class="estado-card">
            <div class="alerta-estado" style="background:#fff3e0;">
              <i class="fas fa-clock" style="color:#e65100;font-size:1.1rem;margin-top:0.1rem;"></i>
              <div>
                <h4 style="color:#e65100;">Buscando repartidor</h4>
                <p>Tu pedido fue recibido. Estamos asignando un repartidor...</p>
              </div>
            </div>
            <div style="margin-top:1rem;text-align:center;">
              <button id="btn-cancelar-pedido" class="btn btn-gris" style="gap:0.4rem;">
                <i class="fas fa-times-circle"></i> Cancelar pedido
              </button>
            </div>
          </div>
        </div>

        {{-- ── ESTADO: CANCELADO ─────────────── --}}
        <div id="estado-cancelado" style="display:none;">
          <div class="timer-card" style="background: linear-gradient(135deg,#ffebee,#ffcdd2);">
            <div><i class="fas fa-times-circle" style="color:var(--principal);"></i></div>
            <div class="timer-numero" style="color:var(--principal);font-size:2rem;">Cancelado</div>
            <div class="timer-label">No se encontró repartidor a tiempo</div>
          </div>
          <div class="estado-card">
            <div class="alerta-estado" style="background:var(--principal-claro);">
              <i class="fas fa-exclamation-circle" style="color:var(--principal);font-size:1.1rem;margin-top:0.1rem;"></i>
              <div>
                <h4 style="color:var(--principal);">Pedido cancelado</h4>
                <p>No se encontró un repartidor disponible. Por favor intenta nuevamente.</p>
              </div>
            </div>
            <div style="margin-top:1rem;text-align:center;">
              <a href="{{ url('/catalogo') }}" class="btn btn-verde">
                <i class="fas fa-redo"></i> Volver al catálogo
              </a>
            </div>
          </div>
        </div>

        {{-- ── ESTADO: EN PROCESO / ENTREGADO ── --}}
        <div id="estado-encamino">
          <div class="timer-card">
            <div><i class="fas fa-clock"></i></div>
            <div class="timer-numero" id="timer-display">35:00</div>
            <div class="timer-label">minutos restantes</div>
          </div>

          <div class="estado-card">
            <div class="alerta-estado" id="alerta-estado">
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
