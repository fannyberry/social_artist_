package com.sally.socialartist.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "seguidores")
@Getter
@Setter
@NoArgsConstructor
public class Seguidor {

    @EmbeddedId
    private SeguidorId id;

    // Quien sigue
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @MapsId("idSeguidor")
    @JoinColumn(name = "id_seguidor")
    private Usuario seguidor;

    // A quien sigue
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @MapsId("idSeguido")
    @JoinColumn(name = "id_seguido")
    private Usuario seguido;

    @Column(name = "fecha", nullable = false, updatable = false)
    private LocalDateTime fecha;

    @PrePersist
    void antesDeGuardar() {
        if (fecha == null) {
            fecha = LocalDateTime.now();
        }
    }
}
