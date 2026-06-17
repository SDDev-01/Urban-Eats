 // LOGIN
const formLogin = document.getElementById('form-login');
if (formLogin) {
  formLogin.addEventListener('submit', (e) => {
    e.preventDefault();

    const email = document.getElementById('email-login');
    const password = document.getElementById('password-login');
    const errorEmail = document.getElementById('error-email-login');
    const errorPassword = document.getElementById('error-password-login');

    errorEmail.textContent = '';
    errorPassword.textContent = '';

    let valido = true;

    if (!email.value.includes('@')) {
      errorEmail.textContent = 'Correo inválido.';
      valido = false;
    }

    if (password.value.length < 4) {
      errorPassword.textContent = 'La contraseña debe tener mínimo 4 caracteres.';
      valido = false;
    }

    if (valido) {
      formLogin.submit();
    }
  });
}
