package com.sally.socialartist.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import com.sally.socialartist.model.TipoObra;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

// Se recibe como multipart/form-data (campos: titulo, descripcion, tipoObra, imagen)
@Data
public class PublicacionRequestDTO {

    @NotBlank(message = "El título es obligatorio")
    @Size(max = 100, message = "El título no puede exceder 100 caracteres")
    private String titulo;

    @Size(max = 500, message = "La descripción no puede exceder 500 caracteres")
    private String descripcion;

    @NotNull(message = "Indica si la obra es digital o física")
    private TipoObra tipoObra;

    @NotNull(message = "Selecciona una imagen")
    private MultipartFile imagen;
}
