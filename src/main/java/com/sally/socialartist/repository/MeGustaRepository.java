package com.sally.socialartist.repository;

import com.sally.socialartist.model.MeGusta;
import com.sally.socialartist.model.MeGustaId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface MeGustaRepository extends JpaRepository<MeGusta, MeGustaId> {

    @Query("select count(m) from MeGusta m where m.id.idPublicacion = :idPublicacion")
    long contarPorPublicacion(@Param("idPublicacion") Long idPublicacion);
}
