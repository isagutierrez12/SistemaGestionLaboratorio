package com.laboratorio.repository;

import com.laboratorio.model.Paquete;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@EnableJpaRepositories
public interface PaqueteRepository extends JpaRepository<Paquete, Long> {

    @Query(
            value = "SELECT COALESCE(MAX(CAST(SUBSTRING(codigo FROM 4) AS INTEGER)), 0) FROM paquete",
            nativeQuery = true
    )
    Integer findMaxCorrelativo();

    @Query("SELECT p FROM Paquete p "
            + "WHERE LOWER(p.nombre) LIKE LOWER(CONCAT('%', :q, '%')) "
            + "   OR LOWER(p.codigo) LIKE LOWER(CONCAT('%', :q, '%')) "
            + "   OR LOWER(p.descripcion) LIKE LOWER(CONCAT('%', :q, '%'))")
    List<Paquete> buscar(String q);

    boolean existsByCodigo(String codigo);

    boolean existsByNombre(String nombre);

    boolean existsByNombreAndIdPaqueteNot(String nombre, Long idPaquete);

    @Query("""
       SELECT DISTINCT p
       FROM Paquete p
       LEFT JOIN FETCH p.detalles d
       LEFT JOIN FETCH d.examen e
       WHERE p.activo = true
       ORDER BY p.nombre ASC
       """)
    List<Paquete> findActivosConExamenes();
}
