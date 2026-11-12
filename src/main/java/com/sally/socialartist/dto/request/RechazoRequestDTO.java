package com.sally.socialartist.dto.request;

import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RechazoRequestDTO {

    @Size(max = 255, message = "El motivo no puede exceder 255 caracteres")
    private String motivo;
}
