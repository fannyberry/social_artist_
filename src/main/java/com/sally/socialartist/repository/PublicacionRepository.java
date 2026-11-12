package com.sally.socialartist.repository;

import com.sally.socialartist.model.EstadoPublicacion;
import com.sally.socialartist.model.Publicacion;
import com.sally.socialartist.model.TipoObra;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PublicacionRepository extends JpaRepository<Publicacion, Long> {

    // EntityGraph trae el usuario en el mismo SELECT (evita una consulta por publicacion)
    @EntityGraph(attributePaths = "usuario")
    List<Publicacion> findByEstadoOrderByFechaPublicacionDescIdDesc(EstadoPublicacion estado);

    @EntityGraph(attributePaths = "usuario")
    List<Publicacion> findByEstadoAndTipoObraOrderByFechaPublicacionDescIdDesc(EstadoPublicacion estado, TipoObra tipoObra);

    // Pendientes: las mas antiguas primero (orden de llegada)
    @EntityGraph(attributePaths = "usuario")
    List<Publicacion> findByEstadoOrderByFechaPublicacionAscIdAsc(EstadoPublicacion estado);

    @EntityGraph(attributePaths = "usuario")
    List<Publicacion> findByUsuarioIdOrderByFechaPublicacionDescIdDesc(Long idUsuario);

    long countByEstado(EstadoPublicacion estado);
}
