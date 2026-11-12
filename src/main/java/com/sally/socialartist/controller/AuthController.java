package com.sally.socialartist.controller;

import com.sally.socialartist.dto.request.LoginRequestDTO;
import com.sally.socialartist.dto.request.RegistroRequestDTO;
import com.sally.socialartist.dto.response.UsuarioResponseDTO;
import com.sally.socialartist.service.SesionService;
import com.sally.socialartist.service.UsuarioService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UsuarioService usuarioService;
    private final SesionService sesionService;

    @PostMapping("/registro")
    @ResponseStatus(HttpStatus.CREATED)
    public UsuarioResponseDTO registro(@Valid @RequestBody RegistroRequestDTO dto) {
        return usuarioService.registrar(dto);
    }

    @PostMapping("/login")
    public UsuarioResponseDTO login(@Valid @RequestBody LoginRequestDTO dto, HttpSession sesion) {
        UsuarioResponseDTO usuario = usuarioService.autenticar(dto);
        sesionService.iniciar(sesion, usuario);
        return usuario; // el frontend redirige segun usuario.rol
    }

    @PostMapping("/logout")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void logout(HttpSession sesion) {
        sesion.invalidate();
    }

    /** Para que cada pagina valide si hay sesion y de que rol. */
    @GetMapping("/me")
    public UsuarioResponseDTO me(HttpSession sesion) {
        return sesionService.requerirUsuario(sesion);
    }
}
