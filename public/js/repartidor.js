/* ============================
   URBAN EATS - Repartidor JS
   ============================ */

// Convertir placa a mayúsculas en tiempo real
const placaInput = document.getElementById('rp-placa');
if (placaInput) {
  placaInput.addEventListener('input', function () {
    const pos = this.selectionStart;
    this.value = this.value.toUpperCase();
    this.setSelectionRange(pos, pos);
  });
}
