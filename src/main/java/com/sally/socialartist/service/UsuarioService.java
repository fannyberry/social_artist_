package com.sally.socialartist.service;

import com.sally.socialartist.dto.request.LoginRequestDTO;
import com.sally.socialartist.dto.request.RegistroRequestDTO;
import com.sally.socialartist.dto.response.UsuarioResponseDTO;
import com.sally.socialartist.model.Rol;
import com.sally.socialartist.model.Usuario;
import com.sally.socialartist.repository.RolRepository;
import com.sally.socialartist.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final RolRepository rolRepository;

    @Transactional
    public UsuarioResponseDTO registrar(RegistroRequestDTO dto) {
        String nombreUsuario = dto.getNombreUsuario().trim();
        if (usuarioRepository.existsByNombreUsuario(nombreUsuario)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Ese nombre de usuario ya está en uso");
        }
        Rol rol = rolRepository.findByNombre(Rol.USUARIO)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
                        "El rol USUARIO no existe. Ejecuta db/schema.sql"));

        Usuario usuario = new Usuario();
        usuario.setNombre(dto.getNombre().trim());
        usuario.setApellidos(dto.getApellidos().trim());
        usuario.setNombreUsuario(nombreUsuario);
        usuario.setContrasena(dto.getContrasena());
        usuario.setRol(rol);
        return aDTO(usuarioRepository.save(usuario));
    }

    @Transactional(readOnly = true)
    public UsuarioResponseDTO autenticar(LoginRequestDTO dto) {
        return usuarioRepository.findByNombreUsuario(dto.getNombreUsuario().trim())
                .filter(u -> u.getContrasena().equals(dto.getContrasena()))
                .map(this::aDTO)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED,
                        "Usuario o contraseña incorrectos"));
    }

    private UsuarioResponseDTO aDTO(Usuario u) {
        return new UsuarioResponseDTO(u.getId(), u.getNombre(), u.getApellidos(),
                u.getNombreUsuario(), u.getRol().getNombre());
    }
}
