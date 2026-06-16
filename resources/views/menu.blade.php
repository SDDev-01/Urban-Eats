<!DOCTYPE html>
<html lang="es">
<head>
  <meta charset="UTF-8">
  <title>Crear Menú – Urban Eats</title>
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <link rel="stylesheet" href="{{ asset('css/styles.css') }}">
  <link rel="stylesheet" href="{{ asset('css/restaurante.css') }}">
  <link rel="stylesheet" href="{{ asset('css/menu.css') }}">
  <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.0/css/all.min.css">
</head>
<body>

@include('partials.navbar')

<div class="pagina-form">
  <div class="form-wrapper">

    <div class="form-page-header">
      <h2><i class="fas fa-utensils"></i> Crear Menú</h2>
      <p>Crea una categoría de menú para <strong>{{ $restaurante->Nombre }}</strong> y agrega sus platos.</p>
    </div>

    <div class="form-card">
      <div class="form-card-body">

        <form id="form-menu" action="{{ url('/menu') }}" method="POST">
          @csrf

          {{-- Categoría del menú --}}
          <div class="campo">
            <label for="m-categoria">Categoría del menú</label>
            <input type="text" id="m-categoria" name="Categoria"
                   placeholder="Ej: Desayunos, Almuerzos, Bebidas…"
                   value="{{ old('Categoria') }}">
            <span class="error" id="err-m-categoria"></span>
          </div>

          <hr class="divisor">

          {{-- Platos existentes --}}
          <p class="seccion-titulo"><i class="fas fa-list-check"></i> Tomar platos existentes</p>

          @if($platosExistentes->isEmpty())
            <p class="sin-platos">No hay platos registrados aún. Puedes crear nuevos abajo.</p>
          @else
            <div class="platos-existentes-grid">
              @foreach($platosExistentes as $plato)
                <label class="plato-check-label">
                  <input type="checkbox"
                         name="platos_existentes[]"
                         value="{{ $plato->CodigoPlato }}"
                         {{ in_array($plato->CodigoPlato, old('platos_existentes', [])) ? 'checked' : '' }}>
                  <span>
                    <span class="plato-check-nombre">{{ $plato->Nombre }}</span><br>
                    <span class="plato-check-precio">
                      {{ $plato->TipoComida ? $plato->TipoComida.' · ' : '' }}
                      ${{ number_format($plato->Precio ?? 0, 0, ',', '.') }}
                    </span>
                  </span>
                </label>
              @endforeach
            </div>
          @endif

          <hr class="divisor">

          {{-- Nuevos platos --}}
          <p class="seccion-titulo"><i class="fas fa-plus-circle"></i> Agregar nuevos platos</p>

          <div id="nuevos-platos-wrap">
            {{-- Las filas se insertan aquí con JS --}}
          </div>

          <button type="button" class="btn-agregar-plato" id="btn-agregar-plato">
            <i class="fas fa-plus"></i> Agregar plato
          </button>

          {{-- Template oculto --}}
          <template id="tpl-plato">
            <div class="plato-fila">
              <button type="button" class="btn-quitar-plato" title="Quitar">
                <i class="fas fa-times"></i>
              </button>
              <div class="campo">
                <label>Nombre del plato <span style="color:red">*</span></label>
                <input type="text" name="nuevos_platos[__i__][Nombre]" placeholder="Ej: Bandeja Paisa">
              </div>
              <div class="campo">
                <label>Tipo de comida</label>
                <input type="text" name="nuevos_platos[__i__][TipoComida]" placeholder="Ej: Plato fuerte">
              </div>
              <div class="campo">
                <label>Precio (COP)</label>
                <input type="number" step="100" min="0" name="nuevos_platos[__i__][Precio]" placeholder="Ej: 18000">
              </div>
              <div class="campo">
                <label>Disponibilidad</label>
                <select name="nuevos_platos[__i__][Disponibilidad]">
                  <option value="Disponible">Disponible</option>
                  <option value="No disponible">No disponible</option>
                  <option value="Temporalmente agotado">Temporalmente agotado</option>
                </select>
              </div>
              <div class="campo plato-fila-full">
                <label>Descripción</label>
                <input type="text" name="nuevos_platos[__i__][Descripcion]" placeholder="Descripción breve del plato">
              </div>
            </div>
          </template>

          <div id="msg-verificacion" class="msg-verificacion" style="display:none;"></div>

          <div class="form-btn-wrap" style="margin-top:1.5rem;">
            <a href="{{ url('/perfilRestaurante') }}" class="btn btn-gris">
              <i class="fas fa-arrow-left"></i> Cancelar
            </a>
            <button type="button" class="btn btn-verde btn-grande" id="btn-crear-menu">
              <i class="fas fa-check-circle"></i> Crear Menú
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
<script src="{{ asset('js/menu.js') }}"></script>
</body>
</html>
