package com.sally.socialartist.service;

import com.sally.socialartist.dto.response.UsuarioResponseDTO;
import com.sally.socialartist.model.Rol;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class SesionService {

    private static final String ATRIBUTO = "usuarioSesion";

    public void iniciar(HttpSession sesion, UsuarioResponseDTO usuario) {
        sesion.setAttribute(ATRIBUTO, usuario);
    }

    /** Cualquier usuario con sesion (comun o admin). */
    public UsuarioResponseDTO requerirUsuario(HttpSession sesion) {
        Object actual = sesion.getAttribute(ATRIBUTO);
        if (actual == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Debes iniciar sesión");
        }
        return (UsuarioResponseDTO) actual;
    }

    public UsuarioResponseDTO requerirAdmin(HttpSession sesion) {
        UsuarioResponseDTO usuario = requerirUsuario(sesion);
        if (!Rol.ADMIN.equals(usuario.getRol())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Solo el administrador puede realizar esta acción");
        }
        return usuario;
    }

    public UsuarioResponseDTO requerirUsuarioComun(HttpSession sesion) {
        UsuarioResponseDTO usuario = requerirUsuario(sesion);
        if (Rol.ADMIN.equals(usuario.getRol())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "El administrador no puede realizar esta acción");
        }
        return usuario;
    }
}
