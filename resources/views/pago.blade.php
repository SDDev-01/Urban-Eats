<!DOCTYPE html>
<html lang="es">
<head>
  <meta charset="UTF-8">
  <title>Pago – Urban Eats</title>
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <meta name="csrf-token" content="{{ csrf_token() }}">
  <link rel="stylesheet" href="{{ asset('css/styles.css') }}">
  <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.0/css/all.min.css">
  <link rel="stylesheet" href="{{ asset('css/pago.css') }}">
</head>
<body>

@include('partials.navbar')
@if(request('error'))
  <p style="color:red;">El pago no pudo completarse</p>
@endif


  <div class="pagina">
    <div style="max-width:1000px; margin:0 auto; padding:0 2rem 1rem;">
      <h2 class="seccion-titulo"><i class="fas fa-credit-card" style="color:var(--verde);"></i> Método de Pago</h2>
      <p class="seccion-subtitulo">Elige cómo quieres pagar tu pedido</p>
    </div>

    <div class="pago-layout">
      <!--sacar variable public key de ENV-->
      <script>
        window.MP_PUBLIC_KEY = "{{ env('MERCADOPAGO_PUBLIC_KEY') }}";
      </script>
      <!-- Columna izquierda: mercado pago -->
      <script src="https://sdk.mercadopago.com/js/v2"></script>
      <!--contenedor del brick-->
      <div id="paymentBrick_container">
        
      </div>

      <!-- Resumen lateral -->
      <div class="resumen-pago">
        <h3>Resumen del Pedido</h3>
        <div id="resumen-items-pago">
          <p style="color:var(--texto-gris); font-size:0.875rem;">No hay pedido activo.</p>
        </div>
        <hr class="resumen-separador">
        <div class="resumen-linea"><span class="label">Subtotal</span><span id="pago-subtotal">$0</span></div>
        <hr class="resumen-separador">
        <div class="resumen-total-pago">
          <span>Total</span>
          <!--monto pago-->
          <span class="monto" id="pago-total">$0 COP</span>
          <input type="hidden" id="monto-input" name="monto" value="0">
        </div>
        <!-- boton viejo de pago
        <button class="btn-pagar" id="btn-pagar">
          <i class="fas fa-lock"></i> Confirmar Pago
        </button>
        <div class="seguridad-nota">
          <i class="fas fa-shield-alt" style="color:var(--verde);"></i>
          Pago 100% seguro y encriptado
        </div>
        -->
      </div>
    </div>
  </div>

  <footer><p>&copy; 2026 Urban Eats – Proyecto Formativo SENA</p></footer>
  <div id="toast-container"></div>
  <script src="{{ asset('js/app.js') }}"></script>
  <script src="{{ asset('js/pago.js') }}"></script>
</body>
</html>
