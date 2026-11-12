package com.sally.socialartist.service;

import com.sally.socialartist.dto.request.PublicacionRequestDTO;
import com.sally.socialartist.dto.response.PublicacionResponseDTO;
import com.sally.socialartist.dto.request.RechazoRequestDTO;
import com.sally.socialartist.model.EstadoPublicacion;
import com.sally.socialartist.model.Publicacion;
import com.sally.socialartist.model.TipoObra;
import com.sally.socialartist.model.Usuario;
import com.sally.socialartist.repository.PublicacionRepository;
import com.sally.socialartist.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PublicacionService {

    private final PublicacionRepository publicacionRepository;
    private final UsuarioRepository usuarioRepository;
    private final AlmacenamientoService almacenamientoService;
    private final BitacoraAdminService bitacoraAdminService;

    /**
     * Explorar: solo publicaciones APROBADAS, agrupadas por usuario.
     * Si tipo es null se devuelven todas; si no, solo las digitales o las fisicas.
     */
    @Transactional(readOnly = true)
    public List<PublicacionResponseDTO> listarExplorar(TipoObra tipo) {
        List<Publicacion> lista = tipo == null
                ? publicacionRepository.findByEstadoOrderByFechaPublicacionDescIdDesc(EstadoPublicacion.APROBADA)
                : publicacionRepository.findByEstadoAndTipoObraOrderByFechaPublicacionDescIdDesc(EstadoPublicacion.APROBADA, tipo);
        return lista.stream()
                .collect(Collectors.groupingBy(p -> p.getUsuario().getId(), LinkedHashMap::new, Collectors.toList()))
                .values().stream()
                .flatMap(List::stream)
                .map(this::aDTO)
                .toList();
    }

    /** Las obras del propio usuario con su estado (pendiente, aprobada o rechazada). */
    @Transactional(readOnly = true)
    public List<PublicacionResponseDTO> listarMias(Long idUsuario) {
        return publicacionRepository.findByUsuarioIdOrderByFechaPublicacionDescIdDesc(idUsuario).stream()
                .map(this::aDTO)
                .toList();
    }

    /** Cola de moderacion del administrador. */
    @Transactional(readOnly = true)
    public List<PublicacionResponseDTO> listarPendientes() {
        return publicacionRepository.findByEstadoOrderByFechaPublicacionAscIdAsc(EstadoPublicacion.PENDIENTE).stream()
                .map(this::aDTO)
                .toList();
    }

    @Transactional(readOnly = true)
    public long contarPendientes() {
        return publicacionRepository.countByEstado(EstadoPublicacion.PENDIENTE);
    }

    /** Una obra no aprobada solo la puede ver su autor o el administrador. */
    @Transactional(readOnly = true)
    public PublicacionResponseDTO obtener(Long id, Long idSolicitante, boolean esAdmin) {
        Publicacion p = publicacionRepository.findById(id).orElseThrow(this::noEncontrada);
        boolean visible = p.getEstado() == EstadoPublicacion.APROBADA
                || esAdmin
                || p.getUsuario().getId().equals(idSolicitante);
        if (!visible) {
            throw noEncontrada();
        }
        return aDTO(p);
    }

    @Transactional
    public PublicacionResponseDTO crear(PublicacionRequestDTO dto, Long idUsuario) {
        Usuario usuario = usuarioRepository.findById(idUsuario)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Sesión inválida"));

        String archivo = almacenamientoService.guardar(dto.getImagen());

        Publicacion publicacion = new Publicacion();
        publicacion.setTitulo(dto.getTitulo().trim());
        String descripcion = dto.getDescripcion() == null ? "" : dto.getDescripcion().trim();
        publicacion.setDescripcion(descripcion.isEmpty() ? null : descripcion);
        publicacion.setRutaImagen(archivo);
        publicacion.setTipoObra(dto.getTipoObra());
        publicacion.setEstado(EstadoPublicacion.PENDIENTE); // siempre nace pendiente
        publicacion.setUsuario(usuario);
        return aDTO(publicacionRepository.save(publicacion));
    }

    @Transactional
    public PublicacionResponseDTO aprobar(Long idPublicacion, Long idAdmin) {
        Publicacion publicacion = publicacionRepository.findById(idPublicacion).orElseThrow(this::noEncontrada);
        publicacion.setEstado(EstadoPublicacion.APROBADA);
        publicacion.setMotivoRechazo(null);
        bitacoraAdminService.registrar(usuarioRepository.getReferenceById(idAdmin), "APROBAR_PUBLICACION",
                "Publicación #" + publicacion.getId() + " '" + publicacion.getTitulo()
                        + "' de @" + publicacion.getUsuario().getNombreUsuario());
        return aDTO(publicacion);
    }

    @Transactional
    public PublicacionResponseDTO rechazar(Long idPublicacion, RechazoRequestDTO dto, Long idAdmin) {
        Publicacion publicacion = publicacionRepository.findById(idPublicacion).orElseThrow(this::noEncontrada);
        String motivo = dto == null || dto.getMotivo() == null ? "" : dto.getMotivo().trim();
        publicacion.setEstado(EstadoPublicacion.RECHAZADA);
        publicacion.setMotivoRechazo(motivo.isEmpty() ? null : motivo);
        bitacoraAdminService.registrar(usuarioRepository.getReferenceById(idAdmin), "RECHAZAR_PUBLICACION",
                "Publicación #" + publicacion.getId() + " '" + publicacion.getTitulo()
                        + "' de @" + publicacion.getUsuario().getNombreUsuario());
        return aDTO(publicacion);
    }

    @Transactional
    public void eliminar(Long idPublicacion, Long idAdmin) {
        Publicacion publicacion = publicacionRepository.findById(idPublicacion)
                .orElseThrow(this::noEncontrada);
        Usuario admin = usuarioRepository.getReferenceById(idAdmin);

        bitacoraAdminService.registrar(admin, "ELIMINAR_PUBLICACION",
                "Publicación #" + publicacion.getId() + " '" + publicacion.getTitulo()
                        + "' de @" + publicacion.getUsuario().getNombreUsuario());

        publicacionRepository.delete(publicacion);
        almacenamientoService.eliminar(publicacion.getRutaImagen());
    }

    private ResponseStatusException noEncontrada() {
        return new ResponseStatusException(HttpStatus.NOT_FOUND, "La publicación no existe");
    }

    private PublicacionResponseDTO aDTO(Publicacion p) {
        return new PublicacionResponseDTO(
                p.getId(),
                p.getTitulo(),
                p.getDescripcion(),
                "/uploads/" + p.getRutaImagen(),
                p.getUsuario().getId(),
                p.getUsuario().getNombreUsuario(),
                p.getFechaPublicacion(),
                p.getTipoObra().name(),
                p.getEstado().name(),
                p.getMotivoRechazo());
    }
}
