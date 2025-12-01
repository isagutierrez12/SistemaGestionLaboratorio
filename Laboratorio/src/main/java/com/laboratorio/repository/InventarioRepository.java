package com.laboratorio.repository;

import com.laboratorio.model.Inventario;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.repository.query.Param;

@EnableJpaRepositories
public interface InventarioRepository extends JpaRepository<Inventario, Long> {

    public List<Inventario> findByFechaVencimientoBetween(LocalDate hoy, LocalDate fechaLimite);

    public List<Inventario> findByFechaVencimientoBefore(LocalDate hoy);

    public Inventario findByInsumo_IdInsumo(Long idInsumo);

    @Query("""
    SELECT i FROM Inventario i
    LEFT JOIN i.insumo ins
    WHERE 
        LOWER(i.codigoBarras) LIKE LOWER(CONCAT('%', :query, '%'))
        OR LOWER(ins.nombre) LIKE LOWER(CONCAT('%', :query, '%'))
        OR LOWER(ins.tipo) LIKE LOWER(CONCAT('%', :query, '%'))
    """)
    List<Inventario> buscarInventarioPorQuery(@Param("query") String query);

    boolean existsByInsumo_IdInsumo(Long idInsumo);

}
