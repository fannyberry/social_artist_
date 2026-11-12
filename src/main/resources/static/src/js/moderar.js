(async () => {
  const admin = await requerirSesion();
  if (!admin) return;
  if (admin.rol !== 'ADMIN') { location.href = '/src/views/explorar.html'; return; }
  pintarNavbar(admin, 'moderar');
  cargar();
})();

const lista = $('lista');

async function cargar() {
  try {
    const obras = await api('/api/publicaciones/pendientes');
    lista.innerHTML = obras.map(p => `
      <article class="tarjeta-mod" data-id="${p.id}">
        <div class="foto"><span class="badge ${esc(p.tipoObra)}">${esc(p.tipoObra === 'FISICA' ? 'Física' : 'Digital')}</span>
          <img src="${esc(p.urlImagen)}" alt="${esc(p.titulo)}" loading="lazy" onclick="window.open(this.src, '_blank')"></div>
        <div class="cuerpo">
          <h3>${esc(p.titulo)}</h3>
          <p class="meta">@${esc(p.nombreUsuario)} · ${new Date(p.fechaPublicacion).toLocaleString('es-MX', { dateStyle: 'medium', timeStyle: 'short' })}</p>
          ${p.descripcion ? `<p class="desc">${esc(p.descripcion)}</p>` : ''}
          <div class="botones">
            <button class="btn-ok" data-accion="aprobar">Aprobar</button>
            <button class="btn-peligro" data-accion="rechazar">Rechazar</button>
          </div>
        </div>
      </article>`).join('') || '<p class="vacio" style="grid-column:1/-1">No hay obras pendientes. ¡Todo al día! 🎨</p>';
  } catch (e) { alert(e.message); }
}

lista.addEventListener('click', async e => {
  const boton = e.target.closest('button[data-accion]');
  if (!boton) return;
  const id = boton.closest('.tarjeta-mod').dataset.id;
  try {
    if (boton.dataset.accion === 'aprobar') {
      await api(`/api/publicaciones/${id}/aprobar`, { method: 'PATCH' });
    } else {
      const motivo = prompt('Motivo del rechazo (opcional, lo verá el autor):');
      if (motivo === null) return; // canceló
      await api(`/api/publicaciones/${id}/rechazar`, enviarJson('PATCH', { motivo }));
    }
    await cargar();
    pintarNavbar(await api('/api/auth/me'), 'moderar'); // refresca el contador
  } catch (err) { alert(err.message); }
});
