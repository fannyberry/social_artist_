package com.sally.socialartist.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ComentarioResponseDTO {

    private Long id;
    private String texto;
    private LocalDateTime fecha;
    private Long idUsuario;
    private String nombreUsuario;
}
