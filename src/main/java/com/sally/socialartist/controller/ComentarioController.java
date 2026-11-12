package com.sally.socialartist.controller;

import com.sally.socialartist.dto.request.ComentarioRequestDTO;
import com.sally.socialartist.dto.response.ComentarioResponseDTO;
import com.sally.socialartist.dto.response.UsuarioResponseDTO;
import com.sally.socialartist.service.ComentarioService;
import com.sally.socialartist.service.SesionService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ComentarioController {

    private final ComentarioService comentarioService;
    private final SesionService sesionService;

    @GetMapping("/publicaciones/{idPublicacion}/comentarios")
    public List<ComentarioResponseDTO> listar(@PathVariable Long idPublicacion, HttpSession sesion) {
        sesionService.requerirUsuario(sesion);
        return comentarioService.listar(idPublicacion);
    }

    @PostMapping("/publicaciones/{idPublicacion}/comentarios")
    @ResponseStatus(HttpStatus.CREATED)
    public ComentarioResponseDTO crear(@PathVariable Long idPublicacion,
                                       @Valid @RequestBody ComentarioRequestDTO dto,
                                       HttpSession sesion) {
        UsuarioResponseDTO usuario = sesionService.requerirUsuarioComun(sesion);
        return comentarioService.crear(idPublicacion, dto, usuario.getId());
    }

    @DeleteMapping("/comentarios/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminar(@PathVariable Long id, HttpSession sesion) {
        comentarioService.eliminar(id, sesionService.requerirUsuario(sesion));
    }
}
