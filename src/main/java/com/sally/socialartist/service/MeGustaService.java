package com.sally.socialartist.service;

import com.sally.socialartist.dto.response.MeGustaResponseDTO;
import com.sally.socialartist.model.MeGusta;
import com.sally.socialartist.model.MeGustaId;
import com.sally.socialartist.repository.MeGustaRepository;
import com.sally.socialartist.repository.PublicacionRepository;
import com.sally.socialartist.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class MeGustaService {

    private final MeGustaRepository meGustaRepository;
    private final PublicacionRepository publicacionRepository;
    private final UsuarioRepository usuarioRepository;

    @Transactional(readOnly = true)
    public MeGustaResponseDTO consultar(Long idPublicacion, Long idUsuario) {
        validarPublicacion(idPublicacion);
        boolean meGusta = meGustaRepository.existsById(new MeGustaId(idUsuario, idPublicacion));
        return new MeGustaResponseDTO(meGusta, meGustaRepository.contarPorPublicacion(idPublicacion));
    }

    /** Si ya tenia me gusta lo quita; si no, lo agrega. */
    @Transactional
    public MeGustaResponseDTO alternar(Long idPublicacion, Long idUsuario) {
        validarPublicacion(idPublicacion);
        MeGustaId id = new MeGustaId(idUsuario, idPublicacion);

        boolean meGusta;
        if (meGustaRepository.existsById(id)) {
            meGustaRepository.deleteById(id);
            meGusta = false;
        } else {
            MeGusta nuevo = new MeGusta();
            nuevo.setId(id);
            nuevo.setUsuario(usuarioRepository.getReferenceById(idUsuario));
            nuevo.setPublicacion(publicacionRepository.getReferenceById(idPublicacion));
            meGustaRepository.save(nuevo);
            meGusta = true;
        }
        return new MeGustaResponseDTO(meGusta, meGustaRepository.contarPorPublicacion(idPublicacion));
    }

    private void validarPublicacion(Long idPublicacion) {
        if (!publicacionRepository.existsById(idPublicacion)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "La publicación no existe");
        }
    }
}
