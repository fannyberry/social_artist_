package com.sally.socialartist.repository;

import com.sally.socialartist.model.Seguidor;
import com.sally.socialartist.model.SeguidorId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface SeguidorRepository extends JpaRepository<Seguidor, SeguidorId> {

    @Query("select count(s) from Seguidor s where s.id.idSeguido = :idUsuario")
    long contarSeguidores(@Param("idUsuario") Long idUsuario);

    @Query("select count(s) from Seguidor s where s.id.idSeguidor = :idUsuario")
    long contarSiguiendo(@Param("idUsuario") Long idUsuario);
}
