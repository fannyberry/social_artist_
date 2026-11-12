package com.sally.socialartist.controller;

import com.sally.socialartist.dto.request.ReporteRequestDTO;
import com.sally.socialartist.dto.response.ReporteResponseDTO;
import com.sally.socialartist.dto.response.UsuarioResponseDTO;
import com.sally.socialartist.service.ReporteService;
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
public class ReporteController {

    private final ReporteService reporteService;
    private final SesionService sesionService;

    @PostMapping("/publicaciones/{idPublicacion}/reportes")
    @ResponseStatus(HttpStatus.CREATED)
    public ReporteResponseDTO reportar(@PathVariable Long idPublicacion,
                                       @Valid @RequestBody ReporteRequestDTO dto,
                                       HttpSession sesion) {
        UsuarioResponseDTO usuario = sesionService.requerirUsuarioComun(sesion);
        return reporteService.crear(idPublicacion, dto, usuario.getId());
    }

    @GetMapping("/reportes")
    public List<ReporteResponseDTO> listar(HttpSession sesion) {
        sesionService.requerirAdmin(sesion);
        return reporteService.listar();
    }

    @DeleteMapping("/reportes/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void descartar(@PathVariable Long id, HttpSession sesion) {
        UsuarioResponseDTO admin = sesionService.requerirAdmin(sesion);
        reporteService.descartar(id, admin.getId());
    }
}
