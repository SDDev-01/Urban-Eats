/* ============================
   URBAN EATS - Menú JS
   ============================ */

let contadorPlatos = 0;
const wrap = document.getElementById('nuevos-platos-wrap');
const tpl  = document.getElementById('tpl-plato');

document.getElementById('btn-agregar-plato').addEventListener('click', () => {
  const clone = tpl.content.cloneNode(true);
  const fila  = clone.querySelector('.plato-fila');

  fila.querySelectorAll('[name]').forEach(el => {
    el.name = el.name.replace('__i__', contadorPlatos);
  });

  fila.querySelector('.btn-quitar-plato').addEventListener('click', () => {
    fila.remove();
  });

  wrap.appendChild(fila);
  contadorPlatos++;
});

document.getElementById('btn-crear-menu').addEventListener('click', () => {
  const msg      = document.getElementById('msg-verificacion');
  const categoria = document.getElementById('m-categoria').value.trim();
  const errCat   = document.getElementById('err-m-categoria');

  errCat.textContent = '';
  msg.style.display  = 'none';

  if (!categoria) {
    errCat.textContent = '⚠ La categoría del menú es obligatoria.';
    msg.style.display  = 'flex';
    msg.className      = 'msg-verificacion msg-error';
    msg.innerHTML      = '<i class="fas fa-exclamation-triangle"></i> Corrige los errores antes de continuar.';
    return;
  }

  document.getElementById('form-menu').submit();
});
