package com.sally.socialartist.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PublicacionResponseDTO {

    private Long id;
    private String titulo;
    private String descripcion;
    private String urlImagen;
    private Long idUsuario;
    private String nombreUsuario;
    private LocalDateTime fechaPublicacion;
    private String tipoObra;        // "DIGITAL" o "FISICA"
    private String estado;          // "PENDIENTE", "APROBADA" o "RECHAZADA"
    private String motivoRechazo;
}
