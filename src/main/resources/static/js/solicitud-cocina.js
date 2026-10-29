/* ============================
   URBAN EATS - Solicitud de cocina
   ============================
   Validaciones en el navegador para que la solicitud salga completa en un
   solo envio y el tecnico la reciba lista para revisar:
   - NIT, DV, matricula, telefono y horas solo aceptan numeros.
   - El DV se compara con el calculo de la DIAN mientras se escribe.
   - Las fechas se escriben dd/mm/aaaa (o se eligen en el calendario) y se
     revisan de verdad: no pasa un 31 de febrero ni un documento vencido.
   - Al "Enviar para revision" se exigen los documentos obligatorios con su PDF.
   El servidor repite estas reglas; esto solo ahorra el viaje de ida y vuelta. */
(function () {
  'use strict';

  var PESOS_DIAN = [3, 7, 13, 17, 19, 23, 29, 37, 41, 43, 47, 53, 59, 67, 71];
  var MAX_PDF = 5 * 1024 * 1024;
  var DIAS_CAMARA = 30;
  var HORAS_MINIMAS = 10;

  function $(selector, raiz) { return (raiz || document).querySelector(selector); }
  function $$(selector, raiz) { return Array.prototype.slice.call((raiz || document).querySelectorAll(selector)); }

  function hoy() {
    var d = new Date();
    d.setHours(0, 0, 0, 0);
    return d;
  }

  function dos(n) { return (n < 10 ? '0' : '') + n; }

  /** "dd/mm/aaaa" -> Date, o null si la fecha no existe (por ejemplo 31/02). */
  function leerFecha(texto) {
    var m = /^(\d{2})\/(\d{2})\/(\d{4})$/.exec((texto || '').trim());
    if (!m) return null;
    var dia = +m[1], mes = +m[2], anio = +m[3];
    var f = new Date(anio, mes - 1, dia);
    return f.getFullYear() === anio && f.getMonth() === mes - 1 && f.getDate() === dia ? f : null;
  }

  // ---------- Mensaje junto a cada campo ----------

  /** Muestra el mensaje bajo el campo (o lo quita si viene vacio). Devuelve true si el campo esta bien. */
  function marcar(campo, mensaje) {
    var caja = campo.closest('.sol-campo') || campo.parentElement;
    var aviso = $('.error-campo', caja);
    if (!mensaje) {
      caja.classList.remove('con-error');
      if (aviso) aviso.textContent = '';
      return true;
    }
    if (!aviso) {
      aviso = document.createElement('div');
      aviso.className = 'error-campo';
      caja.appendChild(aviso);
    }
    caja.classList.add('con-error');
    aviso.textContent = mensaje;
    return false;
  }

  // ---------- Solo numeros ----------

  /**
   * Deja solo digitos y corta al largo de data-solo-numeros. No se usa maxlength: el navegador
   * cortaria lo que se pega ("320 555 0199") antes de quitar los espacios y se perderian digitos.
   */
  function soloNumeros(campo) {
    var limpio = campo.value.replace(/\D+/g, '');
    var maximo = parseInt(campo.getAttribute('data-solo-numeros'), 10);
    var prefijo = campo.getAttribute('data-prefijo-pais');
    if (prefijo && maximo > 0 && limpio.length > maximo && limpio.indexOf(prefijo) === 0) {
      limpio = limpio.slice(prefijo.length); // +57 320 555 0199 -> 3205550199
    }
    if (maximo > 0) limpio = limpio.slice(0, maximo);
    if (limpio !== campo.value) campo.value = limpio;
  }

  /** Si pegan el NIT con su DV (900.123.456-8), el DV pasa a su propio campo. */
  function separarDv(campo) {
    var partes = /^([\d.\s]+)-\s*(\d)\s*$/.exec(campo.value);
    var dv = $('#digito_verificacion');
    if (partes && dv) {
      campo.value = partes[1];
      dv.value = partes[2];
    }
  }

  // ---------- NIT, DV, telefono y matricula ----------

  /** Digito de verificacion con el algoritmo modulo 11 de la DIAN (el mismo del servicio Python). */
  function digitoDian(nit) {
    var suma = 0, digitos = nit.split('').reverse();
    for (var i = 0; i < digitos.length; i++) suma += Number(digitos[i]) * PESOS_DIAN[i];
    var residuo = suma % 11;
    return residuo < 2 ? residuo : 11 - residuo;
  }

  function revisarNit() {
    var nit = $('#nit');
    if (!nit) return true;
    if (!nit.value) return marcar(nit, '');
    return marcar(nit, nit.value.length < 6 ? 'El NIT debe tener entre 6 y 15 dígitos.' : '');
  }

  function revisarDv() {
    var nit = $('#nit'), dv = $('#digito_verificacion');
    if (!nit || !dv) return true;
    if (!dv.value || nit.value.length < 6) return marcar(dv, '');
    var esperado = digitoDian(nit.value);
    return marcar(dv, Number(dv.value) === esperado ? ''
      : 'No corresponde al NIT ' + nit.value + ': según la DIAN debería ser ' + esperado + '. Revisa el RUT.');
  }

  function revisarTelefono() {
    var tel = $('#telefono');
    if (!tel) return true;
    if (!tel.value) return marcar(tel, '');
    return marcar(tel, /^(3\d{9}|601\d{7})$/.test(tel.value) ? ''
      : 'Escribe un celular de 10 dígitos que empiece por 3, o un fijo de Bogotá que empiece por 601.');
  }

  function revisarMatricula() {
    var matricula = $('#matricula_mercantil'), persona = $('#tipo_persona');
    if (!matricula || !persona) return true;
    return marcar(matricula, persona.value === 'Juridica' && !matricula.value
      ? 'Una persona jurídica debe registrar su matrícula mercantil.' : '');
  }

  // ---------- Fechas dd/mm/aaaa ----------

  /** Pone las barras mientras se escribe: 31012026 -> 31/01/2026. */
  function mascaraFecha(campo) {
    var d = campo.value.replace(/\D+/g, '').slice(0, 8);
    var texto = d.length > 4 ? d.slice(0, 2) + '/' + d.slice(2, 4) + '/' + d.slice(4)
              : d.length > 2 ? d.slice(0, 2) + '/' + d.slice(2) : d;
    if (texto !== campo.value) campo.value = texto;
  }

  /** El documento al que pertenece el campo: su recuadro, o el formulario de "Agregar documento". */
  function documentoDe(campo) { return campo.closest('[data-documento]') || campo.form; }

  function tipoDe(campo) {
    var caja = campo.closest('[data-documento]');
    if (caja) return caja.getAttribute('data-documento');
    var tipo = campo.form ? $('select[name="tipo_documento"]', campo.form) : null;
    return tipo ? tipo.value : '';
  }

  function otraFecha(campo) {
    var cual = campo.getAttribute('data-fecha') === 'expedicion' ? 'vencimiento' : 'expedicion';
    return $('[data-fecha="' + cual + '"]', documentoDe(campo));
  }

  function revisarFecha(campo) {
    var texto = campo.value.trim();
    if (!texto) return marcar(campo, '');
    if (texto.length < 10) return marcar(campo, 'Completa la fecha: dd/mm/aaaa.');
    var fecha = leerFecha(texto);
    if (!fecha) return marcar(campo, 'Esa fecha no existe. Revisa el día y el mes (dd/mm/aaaa).');
    var h = hoy();

    if (campo.getAttribute('data-fecha') === 'expedicion') {
      if (fecha > h) return marcar(campo, 'La fecha de expedición no puede ser futura.');
      if (fecha.getFullYear() < 1950) return marcar(campo, 'Revisa el año de expedición.');
      if (tipoDe(campo) === 'Camara de Comercio') {
        var limite = new Date(h);
        limite.setDate(limite.getDate() - DIAS_CAMARA);
        if (fecha < limite) return marcar(campo, 'El certificado debe estar expedido hace 30 días o menos.');
      }
      return marcar(campo, '');
    }

    var expedicion = otraFecha(campo);
    var desde = expedicion ? leerFecha(expedicion.value) : null;
    if (desde && fecha <= desde) return marcar(campo, 'El vencimiento debe ser posterior a la expedición.');
    if (fecha < h) return marcar(campo, 'El documento ya está vencido: adjunta uno vigente.');
    if (fecha.getFullYear() > h.getFullYear() + 20) return marcar(campo, 'Revisa el año de vencimiento.');
    return marcar(campo, '');
  }

  function prepararFecha(campo) {
    campo.addEventListener('input', function () {
      mascaraFecha(campo);
      if (campo.value.length < 10) { marcar(campo, ''); return; }
      revisarFecha(campo);
      var otra = otraFecha(campo);
      if (otra && otra.value.length === 10) revisarFecha(otra);
    });
    campo.addEventListener('blur', function () { revisarFecha(campo); });

    // El boton abre el calendario del navegador; la fecha elegida se copia en formato dd/mm/aaaa.
    var boton = $('.btn-calendario', campo.parentElement);
    var nativa = $('.fecha-nativa', campo.parentElement);
    if (!boton || !nativa) return;
    boton.addEventListener('click', function () {
      var actual = leerFecha(campo.value);
      nativa.value = actual ? actual.getFullYear() + '-' + dos(actual.getMonth() + 1) + '-' + dos(actual.getDate()) : '';
      try { nativa.showPicker(); } catch (e) { campo.focus(); }
    });
    nativa.addEventListener('change', function () {
      if (!nativa.value) return;
      var partes = nativa.value.split('-');
      campo.value = partes[2] + '/' + partes[1] + '/' + partes[0];
      revisarFecha(campo);
      var otra = otraFecha(campo);
      if (otra && otra.value) revisarFecha(otra);
    });
  }

  // ---------- Archivos y documentos ----------

  function revisarArchivo(campo, obligatorio) {
    var archivo = campo.files && campo.files[0];
    if (!archivo) return marcar(campo, obligatorio ? 'Adjunta el PDF del documento.' : '');
    if (!/\.pdf$/i.test(archivo.name) && archivo.type !== 'application/pdf') return marcar(campo, 'El archivo debe ser PDF.');
    if (archivo.size > MAX_PDF) return marcar(campo, 'El PDF pesa más de 5 MB.');
    return marcar(campo, '');
  }

  function documentoLleno(caja) {
    return $$('input', caja).some(function (c) {
      if (c.classList.contains('fecha-nativa')) return false;
      return c.type === 'file' ? c.files.length > 0 : c.value.trim() !== '';
    });
  }

  /** Revisa un documento del formulario de creacion. Los obligatorios deben ir completos y con PDF. */
  function revisarDocumento(caja, juridica) {
    var requisito = caja.getAttribute('data-requerido');
    var obligatorio = requisito === 'siempre' || (requisito === 'juridica' && juridica);
    if (!obligatorio && !documentoLleno(caja)) {
      $$('input, select', caja).forEach(function (c) { marcar(c, ''); });
      return true;
    }
    var ok = true;
    var expedicion = $('[data-fecha="expedicion"]', caja);
    var vencimiento = $('[data-fecha="vencimiento"]', caja);
    var pdf = $('input[type="file"]', caja);
    var titular = $('[name^="doc_titular_"]', caja);
    var horas = $('[name^="doc_horas_"]', caja);
    if (expedicion) {
      ok = (expedicion.value.trim() ? revisarFecha(expedicion)
            : marcar(expedicion, 'Escribe la fecha de expedición.')) && ok;
    }
    if (vencimiento) ok = revisarFecha(vencimiento) && ok;
    if (pdf) ok = revisarArchivo(pdf, true) && ok;
    if (titular) ok = marcar(titular, titular.value.trim() ? '' : 'Escribe el nombre del manipulador.') && ok;
    if (horas) {
      ok = marcar(horas, !horas.value ? 'Escribe las horas del curso.'
        : Number(horas.value) < HORAS_MINIMAS ? 'El curso debe ser de ' + HORAS_MINIMAS + ' horas o más.' : '') && ok;
    }
    return ok;
  }

  /** Aviso arriba del formulario y salto al primer campo con error. */
  function mostrarResumen(form, hayErrores) {
    var aviso = $('.sol-alerta[data-js]', form);
    if (!hayErrores) {
      if (aviso) aviso.remove();
      return;
    }
    if (!aviso) {
      aviso = document.createElement('div');
      aviso.className = 'sol-alerta error';
      aviso.setAttribute('data-js', '');
      aviso.setAttribute('role', 'alert');
      form.insertBefore(aviso, form.firstChild);
    }
    aviso.textContent = 'Revisa los campos marcados en rojo. Así la solicitud le llega completa al técnico.';
    var primero = $('.con-error', form);
    if (primero) primero.scrollIntoView({ behavior: 'smooth', block: 'center' });
  }

  // ---------- Conexion con la pagina ----------

  $$('[data-solo-numeros]').forEach(function (campo) {
    campo.addEventListener('input', function () {
      if (campo.id === 'nit') separarDv(campo);
      soloNumeros(campo);
    });
    soloNumeros(campo);
  });
  $$('[data-fecha]').forEach(prepararFecha);
  $$('input[type="file"][accept="application/pdf"]').forEach(function (campo) {
    campo.addEventListener('change', function () { revisarArchivo(campo, false); });
  });

  var nit = $('#nit'), dv = $('#digito_verificacion'), telefono = $('#telefono');
  var matricula = $('#matricula_mercantil'), persona = $('#tipo_persona');
  if (nit) {
    nit.addEventListener('blur', revisarNit);
    nit.addEventListener('input', function () { if (dv && dv.value) revisarDv(); });
  }
  if (dv) dv.addEventListener('input', revisarDv);
  if (telefono) {
    telefono.addEventListener('blur', revisarTelefono);
    telefono.addEventListener('input', function () { if (telefono.value.length === 10) revisarTelefono(); });
  }
  if (matricula) matricula.addEventListener('blur', revisarMatricula);
  if (persona && matricula) {
    persona.addEventListener('change', function () { if (persona.value !== 'Juridica') marcar(matricula, ''); });
  }

  // Formulario de la solicitud: el borrador se guarda como este; el envio debe ir completo.
  var formulario = $('form[data-solicitud]');
  if (formulario) {
    formulario.addEventListener('submit', function (e) {
      var accion = e.submitter ? e.submitter.value : 'enviar';
      if (accion !== 'enviar') return;
      var ok = true;
      ok = revisarNit() && ok;
      ok = revisarDv() && ok;
      ok = revisarTelefono() && ok;
      ok = revisarMatricula() && ok;
      var juridica = persona ? persona.value === 'Juridica' : false;
      $$('[data-documento]', formulario).forEach(function (caja) { ok = revisarDocumento(caja, juridica) && ok; });
      mostrarResumen(formulario, !ok);
      if (!ok) e.preventDefault();
    });
  }

  // Formulario "Agregar documento" del detalle de la solicitud.
  var agregar = $('form[data-documento-suelto]');
  if (agregar) {
    var selector = $('select[name="tipo_documento"]', agregar);
    if (selector) selector.addEventListener('change', function () { $$('[data-fecha]', agregar).forEach(revisarFecha); });
    agregar.addEventListener('submit', function (e) {
      var ok = true;
      $$('[data-fecha]', agregar).forEach(function (campo) { ok = revisarFecha(campo) && ok; });
      var pdf = $('input[type="file"]', agregar);
      if (pdf) ok = revisarArchivo(pdf, false) && ok;
      var tipo = selector ? selector.value : '';
      var titular = $('[name="titular"]', agregar), horas = $('[name="horas_capacitacion"]', agregar);
      if (titular && (tipo === 'Certificado manipulacion' || tipo === 'Certificado medico')) {
        ok = marcar(titular, titular.value.trim() ? '' : 'Escribe el nombre del manipulador.') && ok;
      }
      if (horas && tipo === 'Certificado manipulacion') {
        ok = marcar(horas, horas.value ? '' : 'Escribe las horas del curso.') && ok;
      }
      mostrarResumen(agregar, !ok);
      if (!ok) e.preventDefault();
    });
  }
})();
