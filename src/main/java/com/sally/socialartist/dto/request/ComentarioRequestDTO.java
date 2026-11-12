package com.sally.socialartist.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ComentarioRequestDTO {

    @NotBlank(message = "El comentario no puede estar vacío")
    @Size(max = 300, message = "El comentario no puede exceder 300 caracteres")
    private String texto;
}
