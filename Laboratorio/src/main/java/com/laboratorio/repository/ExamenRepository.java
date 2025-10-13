package com.laboratorio.repository;

import com.laboratorio.model.Examen;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.repository.query.Param;

@EnableJpaRepositories
public interface ExamenRepository extends JpaRepository<Examen, Long> {

    @Query("SELECT e FROM Examen e " +
           "WHERE LOWER(e.nombre) LIKE LOWER(CONCAT('%', :query, '%')) " +
           "OR LOWER(e.area) LIKE LOWER(CONCAT('%', :query, '%'))")
    List<Examen> buscarPorQuery(@Param("query") String query);

}
