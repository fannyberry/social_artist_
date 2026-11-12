package com.sally.socialartist.repository;

import com.sally.socialartist.model.Reporte;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReporteRepository extends JpaRepository<Reporte, Long> {

    boolean existsByPublicacionIdAndUsuarioId(Long idPublicacion, Long idUsuario);

    @EntityGraph(attributePaths = {"usuario", "publicacion"})
    List<Reporte> findAllByOrderByFechaDescIdDesc();
}
