package com.sally.socialartist.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ReporteRequestDTO {

    @NotBlank(message = "Indica el motivo del reporte")
    @Size(max = 255, message = "El motivo no puede exceder 255 caracteres")
    private String motivo;
}
