package com.sally.socialartist.controller;

import com.sally.socialartist.dto.response.SeguidorResponseDTO;
import com.sally.socialartist.dto.response.UsuarioResponseDTO;
import com.sally.socialartist.service.SeguidorService;
import com.sally.socialartist.service.SesionService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/usuarios/{idUsuario}/seguidores")
@RequiredArgsConstructor
public class SeguidorController {

    private final SeguidorService seguidorService;
    private final SesionService sesionService;

    @GetMapping
    public SeguidorResponseDTO consultar(@PathVariable Long idUsuario, HttpSession sesion) {
        UsuarioResponseDTO actual = sesionService.requerirUsuario(sesion);
        return seguidorService.consultar(idUsuario, actual.getId());
    }

    @PostMapping
    public SeguidorResponseDTO alternar(@PathVariable Long idUsuario, HttpSession sesion) {
        UsuarioResponseDTO actual = sesionService.requerirUsuarioComun(sesion);
        return seguidorService.alternar(idUsuario, actual.getId());
    }
}
