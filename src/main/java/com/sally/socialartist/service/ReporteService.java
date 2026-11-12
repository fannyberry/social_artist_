package com.sally.socialartist.service;

import com.sally.socialartist.dto.request.ReporteRequestDTO;
import com.sally.socialartist.dto.response.ReporteResponseDTO;
import com.sally.socialartist.model.Publicacion;
import com.sally.socialartist.model.Reporte;
import com.sally.socialartist.model.Usuario;
import com.sally.socialartist.repository.PublicacionRepository;
import com.sally.socialartist.repository.ReporteRepository;
import com.sally.socialartist.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ReporteService {

    private final ReporteRepository reporteRepository;
    private final PublicacionRepository publicacionRepository;
    private final UsuarioRepository usuarioRepository;
    private final BitacoraAdminService bitacoraAdminService;

    @Transactional
    public ReporteResponseDTO crear(Long idPublicacion, ReporteRequestDTO dto, Long idUsuario) {
        Publicacion publicacion = publicacionRepository.findById(idPublicacion)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "La publicación no existe"));
        if (publicacion.getUsuario().getId().equals(idUsuario)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No puedes reportar tu propia publicación");
        }
        if (reporteRepository.existsByPublicacionIdAndUsuarioId(idPublicacion, idUsuario)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Ya reportaste esta publicación");
        }
        Usuario usuario = usuarioRepository.findById(idUsuario)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Sesión inválida"));

        Reporte reporte = new Reporte();
        reporte.setMotivo(dto.getMotivo().trim());
        reporte.setPublicacion(publicacion);
        reporte.setUsuario(usuario);
        return aDTO(reporteRepository.save(reporte));
    }

    @Transactional(readOnly = true)
    public List<ReporteResponseDTO> listar() {
        return reporteRepository.findAllByOrderByFechaDescIdDesc().stream()
                .map(this::aDTO)
                .toList();
    }

    /** El admin descarta el reporte (la publicacion se elimina por el flujo normal de publicaciones). */
    @Transactional
    public void descartar(Long idReporte, Long idAdmin) {
        Reporte reporte = reporteRepository.findById(idReporte)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "El reporte no existe"));
        bitacoraAdminService.registrar(usuarioRepository.getReferenceById(idAdmin),
                "DESCARTAR_REPORTE",
                "Reporte #" + reporte.getId() + " sobre publicación #" + reporte.getPublicacion().getId());
        reporteRepository.delete(reporte);
    }

    private ReporteResponseDTO aDTO(Reporte r) {
        return new ReporteResponseDTO(r.getId(), r.getMotivo(), r.getFecha(),
                r.getPublicacion().getId(), r.getPublicacion().getTitulo(),
                "/uploads/" + r.getPublicacion().getRutaImagen(),
                r.getUsuario().getNombreUsuario());
    }
}
