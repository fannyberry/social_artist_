package com.sally.socialartist.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;

// Serializable porque tambien se guarda en la HttpSession
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UsuarioResponseDTO implements Serializable {

    private Long id;
    private String nombre;
    private String apellidos;
    private String nombreUsuario;
    private String rol; // "ADMIN" o "USUARIO"
}
