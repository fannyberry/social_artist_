// Utilidades compartidas por todas las páginas
const $ = id => document.getElementById(id);

// Escapa texto de usuarios antes de usarlo en innerHTML
const esc = texto => String(texto ?? '').replace(/[&<>"']/g,
  c => ({ '&': '&amp;', '<': '&lt;', '>': '&gt;', '"': '&quot;', "'": '&#39;' }[c]));

async function api(url, opciones) {
  const res = await fetch(url, opciones);
  const texto = await res.text();
  let datos = null;
  try { datos = texto ? JSON.parse(texto) : null; } catch { /* respuesta sin JSON */ }
  if (!res.ok) throw new Error((datos && datos.mensaje) || 'Ocurrió un error inesperado');
  return datos;
}

const enviarJson = (metodo, cuerpo) => ({
  method: metodo,
  headers: { 'Content-Type': 'application/json' },
  body: JSON.stringify(cuerpo)
});

// Devuelve el usuario en sesión o manda al login
async function requerirSesion() {
  try { return await api('/api/auth/me'); }
  catch { location.href = '/index.html'; return null; }
}

// Barra superior: usuario común (Explorar, Publicar, Mis obras) o administrador (Galería, Moderación)
async function pintarNavbar(usuario, activa) {
  const cls = nombre => (activa === nombre ? 'activo' : '');
  const esAdmin = usuario.rol === 'ADMIN';
  const enlaces = esAdmin
    ? `<a class="${cls('explorar')}" href="/src/views/explorar.html">Galería</a>
       <a class="${cls('moderar')}" href="/src/views/moderar.html">Moderación<span id="contador" class="contador oculto"></span></a>`
    : `<a class="${cls('explorar')}" href="/src/views/explorar.html">Explorar</a>
       <a class="${cls('publicar')}" href="/src/views/publicar.html">Publicar</a>
       <a class="${cls('mis-obras')}" href="/src/views/mis-obras.html">Mis obras</a>`;
  $('navbar').innerHTML = `
    <div class="nav-izq"><span class="nav-marca">SocialArtist</span><nav class="nav-links">${enlaces}</nav></div>
    <div class="nav-user">@${esc(usuario.nombreUsuario)}${esAdmin ? ' · admin' : ''} <button id="salir" class="btn-sec">Salir</button></div>`;
  $('salir').onclick = async () => {
    await api('/api/auth/logout', { method: 'POST' }).catch(() => {});
    location.href = '/index.html';
  };
  if (esAdmin) {
    try {
      const { total } = await api('/api/publicaciones/pendientes/total');
      if (total > 0) { $('contador').textContent = total; $('contador').classList.remove('oculto'); }
    } catch { /* sin contador */ }
  }
}

const etiquetaTipo = t => (t === 'FISICA' ? '🖌️ Física' : '🖥️ Digital');
const etiquetaEstado = e => ({ PENDIENTE: '⏳ En revisión', APROBADA: '✓ Aprobada', RECHAZADA: '✕ Rechazada' }[e] || e);
