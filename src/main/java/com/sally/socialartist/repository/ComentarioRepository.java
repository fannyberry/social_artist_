package com.sally.socialartist.repository;

import com.sally.socialartist.model.Comentario;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ComentarioRepository extends JpaRepository<Comentario, Long> {

    @EntityGraph(attributePaths = "usuario")
    List<Comentario> findByPublicacionIdOrderByFechaAscIdAsc(Long idPublicacion);
}
