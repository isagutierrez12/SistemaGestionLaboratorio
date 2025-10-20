package com.laboratorio.repository;

import com.laboratorio.model.Paquete;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.repository.query.Param;

@EnableJpaRepositories
public interface PaqueteRepository extends JpaRepository<Paquete, Long> {

    @Query("SELECT p FROM Paquete p " +
           "WHERE LOWER(p.nombre) LIKE LOWER(CONCAT('%', :q, '%')) " +
           "   OR LOWER(p.codigo) LIKE LOWER(CONCAT('%', :q, '%')) " +
           "   OR LOWER(p.descripcion) LIKE LOWER(CONCAT('%', :q, '%'))")
    List<Paquete> buscar(String q);

    boolean existsByCodigo(String codigo);
}
