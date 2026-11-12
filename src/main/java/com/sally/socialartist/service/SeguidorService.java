package com.sally.socialartist.service;

import com.sally.socialartist.dto.response.SeguidorResponseDTO;
import com.sally.socialartist.model.Seguidor;
import com.sally.socialartist.model.SeguidorId;
import com.sally.socialartist.repository.SeguidorRepository;
import com.sally.socialartist.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class SeguidorService {

    private final SeguidorRepository seguidorRepository;
    private final UsuarioRepository usuarioRepository;

    @Transactional(readOnly = true)
    public SeguidorResponseDTO consultar(Long idObjetivo, Long idActual) {
        validarUsuario(idObjetivo);
        boolean siguiendo = seguidorRepository.existsById(new SeguidorId(idActual, idObjetivo));
        return armar(idObjetivo, siguiendo);
    }

    /** Si ya lo seguia deja de seguirlo; si no, empieza a seguirlo. */
    @Transactional
    public SeguidorResponseDTO alternar(Long idObjetivo, Long idActual) {
        if (idObjetivo.equals(idActual)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No puedes seguirte a ti mismo");
        }
        validarUsuario(idObjetivo);
        SeguidorId id = new SeguidorId(idActual, idObjetivo);

        boolean siguiendo;
        if (seguidorRepository.existsById(id)) {
            seguidorRepository.deleteById(id);
            siguiendo = false;
        } else {
            Seguidor nuevo = new Seguidor();
            nuevo.setId(id);
            nuevo.setSeguidor(usuarioRepository.getReferenceById(idActual));
            nuevo.setSeguido(usuarioRepository.getReferenceById(idObjetivo));
            seguidorRepository.save(nuevo);
            siguiendo = true;
        }
        return armar(idObjetivo, siguiendo);
    }

    private SeguidorResponseDTO armar(Long idObjetivo, boolean siguiendo) {
        return new SeguidorResponseDTO(idObjetivo, siguiendo,
                seguidorRepository.contarSeguidores(idObjetivo),
                seguidorRepository.contarSiguiendo(idObjetivo));
    }

    private void validarUsuario(Long idUsuario) {
        if (!usuarioRepository.existsById(idUsuario)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "El usuario no existe");
        }
    }
}
