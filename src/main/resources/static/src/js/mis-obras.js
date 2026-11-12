(async () => {
  const usuario = await requerirSesion();
  if (!usuario) return;
  if (usuario.rol === 'ADMIN') { location.href = '/src/views/moderar.html'; return; }
  pintarNavbar(usuario, 'mis-obras');

  const lista = $('lista');
  try {
    const obras = await api('/api/publicaciones/mias');
    const aviso = new URLSearchParams(location.search).has('enviada')
      ? '<p class="aviso" style="grid-column:1/-1">¡Obra enviada! Aparecerá en la galería cuando el administrador la apruebe.</p>' : '';
    lista.innerHTML = aviso + (obras.map(p => `
      <article class="tarjeta-mod">
        <div class="foto"><span class="badge ${esc(p.tipoObra)}">${esc(p.tipoObra === 'FISICA' ? 'Física' : 'Digital')}</span>
          <img src="${esc(p.urlImagen)}" alt="${esc(p.titulo)}" loading="lazy"></div>
        <div class="cuerpo">
          <h3>${esc(p.titulo)}</h3>
          <div><span class="estado ${esc(p.estado)}">${etiquetaEstado(p.estado)}</span></div>
          ${p.estado === 'RECHAZADA' && p.motivoRechazo ? `<p class="motivo">Motivo: ${esc(p.motivoRechazo)}</p>` : ''}
          <p class="meta">${new Date(p.fechaPublicacion).toLocaleDateString('es-MX', { dateStyle: 'long' })}</p>
        </div>
      </article>`).join('') || '<p class="vacio" style="grid-column:1/-1">Todavía no has publicado obras.</p>');
  } catch (e) { alert(e.message); }
})();
