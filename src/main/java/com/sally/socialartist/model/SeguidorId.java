package com.sally.socialartist.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

// Llave primaria compuesta de seguidores
@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class SeguidorId implements Serializable {

    @Column(name = "id_seguidor")
    private Long idSeguidor;

    @Column(name = "id_seguido")
    private Long idSeguido;
}
