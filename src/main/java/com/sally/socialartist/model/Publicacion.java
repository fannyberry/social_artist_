package com.sally.socialartist.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "publicaciones")
@Getter
@Setter
@NoArgsConstructor
public class Publicacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "titulo", nullable = false, length = 100)
    private String titulo;

    @Column(name = "descripcion", length = 500)
    private String descripcion;

    // Solo el nombre del archivo (ej. "a1b2.jpg"); la URL se arma en el servicio
    @Column(name = "ruta_imagen", nullable = false, length = 255)
    private String rutaImagen;

    @Column(name = "fecha_publicacion", nullable = false, updatable = false)
    private LocalDateTime fechaPublicacion;

    // DIGITAL o FISICA
    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_obra", nullable = false, length = 10)
    private TipoObra tipoObra = TipoObra.DIGITAL;

    // PENDIENTE hasta que el administrador la apruebe
    @Enumerated(EnumType.STRING)
    @Column(name = "estado", nullable = false, length = 10)
    private EstadoPublicacion estado = EstadoPublicacion.PENDIENTE;

    @Column(name = "motivo_rechazo", length = 255)
    private String motivoRechazo;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_usuario", nullable = false)
    private Usuario usuario;

    @PrePersist
    void antesDeGuardar() {
        if (fechaPublicacion == null) {
            fechaPublicacion = LocalDateTime.now();
        }
    }
}
