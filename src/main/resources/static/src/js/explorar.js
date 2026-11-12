let usuario = null, esAdmin = false, publicaciones = [], actual = null, filtroTipo = '';
const cuadricula = $('cuadricula');
const intentar = fn => fn().catch(e => alert(e.message));

(async () => {
  usuario = await requerirSesion();
  if (!usuario) return;
  esAdmin = usuario.rol === 'ADMIN';
  pintarNavbar(usuario, 'explorar');
  intentar(cargar);
})();

// Filtros: todas / digitales / físicas
$('filtros').addEventListener('click', e => {
  const chip = e.target.closest('.chip');
  if (!chip) return;
  filtroTipo = chip.dataset.tipo;
  document.querySelectorAll('#filtros .chip').forEach(c => c.classList.toggle('activo', c === chip));
  intentar(cargar);
});

// El backend ya entrega las publicaciones agrupadas por usuario
async function cargar() {
  publicaciones = await api('/api/publicaciones' + (filtroTipo ? '?tipo=' + filtroTipo : ''));
  cuadricula.innerHTML = publicaciones.length ? publicaciones.map(p => `
    <div class="celda" data-id="${p.id}">
      <span class="badge ${esc(p.tipoObra)}">${esc(p.tipoObra === 'FISICA' ? 'Física' : 'Digital')}</span>
      <img src="${esc(p.urlImagen)}" alt="${esc(p.titulo)}" loading="lazy">
      <div class="celda-info"><b>${esc(p.titulo)}</b><span>@${esc(p.nombreUsuario)}</span></div>
      ${esAdmin ? `<button class="btn-peligro borrar" data-id="${p.id}">Eliminar</button>` : ''}
    </div>`).join('') : '<p class="vacio">Aún no hay obras en esta categoría.</p>';
}

cuadricula.addEventListener('click', e => {
  const borrar = e.target.closest('.borrar');
  if (borrar) return eliminar(Number(borrar.dataset.id));
  const celda = e.target.closest('.celda');
  if (celda) abrir(Number(celda.dataset.id));
});

function eliminar(id) {
  if (!confirm('¿Eliminar esta publicación? Esta acción no se puede deshacer.')) return;
  intentar(async () => {
    await api('/api/publicaciones/' + id, { method: 'DELETE' });
    cerrarModal();
    await cargar();
  });
}

// ---------- Detalle ----------
async function abrir(id) {
  actual = publicaciones.find(p => p.id === id);
  if (!actual) return;
  const propia = actual.idUsuario === usuario.id;

  $('m-img').src = actual.urlImagen;
  $('m-titulo').textContent = actual.titulo;
  $('m-tipo').textContent = etiquetaTipo(actual.tipoObra);
  $('m-autor').textContent = '@' + actual.nombreUsuario;
  $('m-fecha').textContent = new Date(actual.fechaPublicacion).toLocaleDateString('es-MX', { dateStyle: 'long' });
  $('m-desc').textContent = actual.descripcion || '';
  $('m-seguidores').textContent = '';
  $('m-eliminar').classList.toggle('oculto', !esAdmin);
  $('m-like').classList.toggle('oculto', esAdmin);
  $('m-reportar').classList.toggle('oculto', esAdmin || propia);
  $('m-seguir').classList.toggle('oculto', esAdmin || propia);
  $('m-form').classList.toggle('oculto', esAdmin);
  $('modal').classList.remove('oculto');

  intentar(async () => {
    const [mg, seg] = await Promise.all([
      api(`/api/publicaciones/${id}/me-gusta`),
      api(`/api/usuarios/${actual.idUsuario}/seguidores`)
    ]);
    pintarLike(mg);
    pintarSeguir(seg);
    await cargarComentarios();
  });
}

function cerrarModal() {
  $('modal').classList.add('oculto');
  $('m-img').removeAttribute('src');
  actual = null;
}

const pintarLike = mg => { $('m-like').textContent = (mg.meGusta ? '♥ ' : '♡ ') + mg.total; };
const pintarSeguir = s => {
  $('m-seguir').textContent = s.siguiendo ? 'Siguiendo' : 'Seguir';
  $('m-seguidores').textContent = s.totalSeguidores + ' seguidores';
};

async function cargarComentarios() {
  const lista = await api(`/api/publicaciones/${actual.id}/comentarios`);
  $('m-comentarios').innerHTML = lista.map(c => `
    <div class="comentario"><span><b>@${esc(c.nombreUsuario)}</b> ${esc(c.texto)}</span>
      ${esAdmin || c.idUsuario === usuario.id ? `<button class="x" data-id="${c.id}" title="Eliminar">×</button>` : ''}
    </div>`).join('') || '<p class="vacio">Sin comentarios todavía.</p>';
}

$('cerrar').onclick = cerrarModal;
$('modal').addEventListener('click', e => { if (e.target.id === 'modal') cerrarModal(); });
document.addEventListener('keydown', e => { if (e.key === 'Escape') cerrarModal(); });

$('m-eliminar').onclick = () => eliminar(actual.id);

$('m-like').onclick = () => intentar(async () =>
  pintarLike(await api(`/api/publicaciones/${actual.id}/me-gusta`, { method: 'POST' })));

$('m-seguir').onclick = () => intentar(async () =>
  pintarSeguir(await api(`/api/usuarios/${actual.idUsuario}/seguidores`, { method: 'POST' })));

$('m-reportar').onclick = () => {
  const motivo = prompt('¿Por qué reportas esta publicación?');
  if (!motivo || !motivo.trim()) return;
  intentar(async () => {
    await api(`/api/publicaciones/${actual.id}/reportes`, enviarJson('POST', { motivo }));
    alert('Reporte enviado. Gracias.');
  });
};

$('m-form').onsubmit = e => {
  e.preventDefault();
  intentar(async () => {
    await api(`/api/publicaciones/${actual.id}/comentarios`, enviarJson('POST', { texto: $('m-texto').value }));
    $('m-texto').value = '';
    await cargarComentarios();
  });
};

$('m-comentarios').addEventListener('click', e => {
  const boton = e.target.closest('.x');
  if (!boton) return;
  intentar(async () => {
    await api('/api/comentarios/' + boton.dataset.id, { method: 'DELETE' });
    await cargarComentarios();
  });
});
