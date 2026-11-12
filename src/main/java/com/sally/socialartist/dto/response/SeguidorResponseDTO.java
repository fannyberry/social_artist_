package com.sally.socialartist.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class SeguidorResponseDTO {

    private Long idUsuario;
    private boolean siguiendo;       // si el usuario en sesion sigue a idUsuario
    private long totalSeguidores;    // cuantos siguen a idUsuario
    private long totalSiguiendo;     // a cuantos sigue idUsuario
}
