package com.sally.socialartist.controller;

import com.sally.socialartist.dto.request.PublicacionRequestDTO;
import com.sally.socialartist.dto.request.RechazoRequestDTO;
import com.sally.socialartist.model.Rol;
import com.sally.socialartist.model.TipoObra;
import java.util.Map;
import com.sally.socialartist.dto.response.PublicacionResponseDTO;
import com.sally.socialartist.dto.response.UsuarioResponseDTO;
import com.sally.socialartist.service.PublicacionService;
import com.sally.socialartist.service.SesionService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/publicaciones")
@RequiredArgsConstructor
public class PublicacionController {

    private final PublicacionService publicacionService;
    private final SesionService sesionService;

    // ?tipo=DIGITAL | FISICA (sin parametro = todas). Solo devuelve obras aprobadas.
    @GetMapping
    public List<PublicacionResponseDTO> explorar(@RequestParam(required = false) TipoObra tipo, HttpSession sesion) {
        sesionService.requerirUsuario(sesion);
        return publicacionService.listarExplorar(tipo);
    }

    @GetMapping("/mias")
    public List<PublicacionResponseDTO> mias(HttpSession sesion) {
        UsuarioResponseDTO usuario = sesionService.requerirUsuarioComun(sesion);
        return publicacionService.listarMias(usuario.getId());
    }

    @GetMapping("/pendientes")
    public List<PublicacionResponseDTO> pendientes(HttpSession sesion) {
        sesionService.requerirAdmin(sesion);
        return publicacionService.listarPendientes();
    }

    @GetMapping("/pendientes/total")
    public Map<String, Long> totalPendientes(HttpSession sesion) {
        sesionService.requerirAdmin(sesion);
        return Map.of("total", publicacionService.contarPendientes());
    }

    @GetMapping("/{id}")
    public PublicacionResponseDTO obtener(@PathVariable Long id, HttpSession sesion) {
        UsuarioResponseDTO usuario = sesionService.requerirUsuario(sesion);
        return publicacionService.obtener(id, usuario.getId(), Rol.ADMIN.equals(usuario.getRol()));
    }

    @PatchMapping("/{id}/aprobar")
    public PublicacionResponseDTO aprobar(@PathVariable Long id, HttpSession sesion) {
        UsuarioResponseDTO admin = sesionService.requerirAdmin(sesion);
        return publicacionService.aprobar(id, admin.getId());
    }

    @PatchMapping("/{id}/rechazar")
    public PublicacionResponseDTO rechazar(@PathVariable Long id,
                                           @Valid @RequestBody(required = false) RechazoRequestDTO dto,
                                           HttpSession sesion) {
        UsuarioResponseDTO admin = sesionService.requerirAdmin(sesion);
        return publicacionService.rechazar(id, dto, admin.getId());
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public PublicacionResponseDTO publicar(@Valid @ModelAttribute PublicacionRequestDTO dto, HttpSession sesion) {
        UsuarioResponseDTO usuario = sesionService.requerirUsuarioComun(sesion);
        return publicacionService.crear(dto, usuario.getId());
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminar(@PathVariable Long id, HttpSession sesion) {
        UsuarioResponseDTO admin = sesionService.requerirAdmin(sesion);
        publicacionService.eliminar(id, admin.getId());
    }
}
