package com.sally.socialartist.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ReporteResponseDTO {

    private Long id;
    private String motivo;
    private LocalDateTime fecha;
    private Long idPublicacion;
    private String tituloPublicacion;
    private String urlImagen;
    private String nombreUsuarioReporta;
}
