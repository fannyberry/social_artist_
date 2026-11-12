package com.sally.socialartist.controller;

import com.sally.socialartist.dto.response.MeGustaResponseDTO;
import com.sally.socialartist.dto.response.UsuarioResponseDTO;
import com.sally.socialartist.service.MeGustaService;
import com.sally.socialartist.service.SesionService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/publicaciones/{idPublicacion}/me-gusta")
@RequiredArgsConstructor
public class MeGustaController {

    private final MeGustaService meGustaService;
    private final SesionService sesionService;

    @GetMapping
    public MeGustaResponseDTO consultar(@PathVariable Long idPublicacion, HttpSession sesion) {
        UsuarioResponseDTO usuario = sesionService.requerirUsuario(sesion);
        return meGustaService.consultar(idPublicacion, usuario.getId());
    }

    @PostMapping
    public MeGustaResponseDTO alternar(@PathVariable Long idPublicacion, HttpSession sesion) {
        UsuarioResponseDTO usuario = sesionService.requerirUsuarioComun(sesion);
        return meGustaService.alternar(idPublicacion, usuario.getId());
    }
}
