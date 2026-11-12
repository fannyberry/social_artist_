(async () => {
  const usuario = await requerirSesion();
  if (!usuario) return;
  if (usuario.rol === 'ADMIN') { location.href = '/src/views/explorar.html'; return; }
  pintarNavbar(usuario, 'publicar');
})();

const form = $('form-publicar');

form.imagen.addEventListener('change', () => {
  const archivo = form.imagen.files[0];
  const vista = $('vista-previa');
  if (!archivo) { vista.classList.add('oculto'); return; }
  vista.src = URL.createObjectURL(archivo);
  vista.classList.remove('oculto');
});

form.addEventListener('submit', async e => {
  e.preventDefault();
  const mensaje = $('mensaje');
  mensaje.textContent = '';
  const archivo = form.imagen.files[0];
  if (archivo && archivo.size > 5 * 1024 * 1024) {
    mensaje.textContent = 'La imagen supera el máximo de 5 MB';
    return;
  }
  try {
    // El usuario y la fecha los agrega el backend automáticamente
    await api('/api/publicaciones', { method: 'POST', body: new FormData(form) });
    // Queda pendiente: se lleva al usuario a ver el estado de su obra
    location.href = '/src/views/mis-obras.html?enviada=1';
  } catch (err) {
    mensaje.textContent = err.message;
  }
});
