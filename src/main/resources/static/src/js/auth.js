const formLogin = $('form-login');
const formRegistro = $('form-registro');

if (formLogin) {
  if (new URLSearchParams(location.search).has('registrado')) {
    $('mensaje').textContent = 'Cuenta creada. Inicia sesión.';
    $('mensaje').classList.add('ok');
  }
  formLogin.addEventListener('submit', async e => {
    e.preventDefault();
    $('mensaje').classList.remove('ok');
    $('mensaje').textContent = '';
    try {
      await api('/api/auth/login', enviarJson('POST', {
        nombreUsuario: formLogin.nombreUsuario.value,
        contrasena: formLogin.contrasena.value
      }));
      // Usuario común -> Explorar; administrador -> Administración (misma vista)
      location.href = '/src/views/explorar.html';
    } catch (err) {
      $('mensaje').textContent = err.message;
    }
  });
}

if (formRegistro) {
  formRegistro.addEventListener('submit', async e => {
    e.preventDefault();
    $('mensaje').textContent = '';
    try {
      await api('/api/auth/registro', enviarJson('POST', {
        nombre: formRegistro.nombre.value,
        apellidos: formRegistro.apellidos.value,
        nombreUsuario: formRegistro.nombreUsuario.value,
        contrasena: formRegistro.contrasena.value
      }));
      location.href = '/index.html?registrado=1';
    } catch (err) {
      $('mensaje').textContent = err.message;
    }
  });
}
