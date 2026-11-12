package com.sally.socialartist.service;

import com.sally.socialartist.dto.request.ComentarioRequestDTO;
import com.sally.socialartist.dto.response.ComentarioResponseDTO;
import com.sally.socialartist.dto.response.UsuarioResponseDTO;
import com.sally.socialartist.model.Comentario;
import com.sally.socialartist.model.Rol;
import com.sally.socialartist.model.Usuario;
import com.sally.socialartist.repository.ComentarioRepository;
import com.sally.socialartist.repository.PublicacionRepository;
import com.sally.socialartist.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ComentarioService {

    private final ComentarioRepository comentarioRepository;
    private final PublicacionRepository publicacionRepository;
    private final UsuarioRepository usuarioRepository;
    private final BitacoraAdminService bitacoraAdminService;

    @Transactional(readOnly = true)
    public List<ComentarioResponseDTO> listar(Long idPublicacion) {
        validarPublicacion(idPublicacion);
        return comentarioRepository.findByPublicacionIdOrderByFechaAscIdAsc(idPublicacion).stream()
                .map(this::aDTO)
                .toList();
    }

    @Transactional
    public ComentarioResponseDTO crear(Long idPublicacion, ComentarioRequestDTO dto, Long idUsuario) {
        validarPublicacion(idPublicacion);
        Usuario usuario = usuarioRepository.findById(idUsuario)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Sesión inválida"));

        Comentario comentario = new Comentario();
        comentario.setTexto(dto.getTexto().trim());
        comentario.setPublicacion(publicacionRepository.getReferenceById(idPublicacion));
        comentario.setUsuario(usuario);
        return aDTO(comentarioRepository.save(comentario));
    }

    /** Lo borra su autor o el administrador (este ultimo queda en bitacora). */
    @Transactional
    public void eliminar(Long idComentario, UsuarioResponseDTO actor) {
        Comentario comentario = comentarioRepository.findById(idComentario)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "El comentario no existe"));

        boolean esAdmin = Rol.ADMIN.equals(actor.getRol());
        if (!esAdmin && !comentario.getUsuario().getId().equals(actor.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Solo puedes eliminar tus propios comentarios");
        }
        if (esAdmin) {
            bitacoraAdminService.registrar(usuarioRepository.getReferenceById(actor.getId()),
                    "ELIMINAR_COMENTARIO",
                    "Comentario #" + comentario.getId() + " de @" + comentario.getUsuario().getNombreUsuario()
                            + " en publicación #" + comentario.getPublicacion().getId());
        }
        comentarioRepository.delete(comentario);
    }

    private void validarPublicacion(Long idPublicacion) {
        if (!publicacionRepository.existsById(idPublicacion)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "La publicación no existe");
        }
    }

    private ComentarioResponseDTO aDTO(Comentario c) {
        return new ComentarioResponseDTO(c.getId(), c.getTexto(), c.getFecha(),
                c.getUsuario().getId(), c.getUsuario().getNombreUsuario());
    }
}
