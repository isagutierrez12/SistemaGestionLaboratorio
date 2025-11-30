package com.laboratorio.repository;

import com.laboratorio.model.Insumo;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface InsumoRepository extends JpaRepository<Insumo, Long> {

    @Query("""
        SELECT i FROM Insumo i
        WHERE LOWER(i.nombre) LIKE LOWER(CONCAT('%', :query, '%'))
           OR LOWER(i.tipo) LIKE LOWER(CONCAT('%', :query, '%'))
    """)
    List<Insumo> buscarPorQuery(@Param("query") String query);
    
    public boolean existsByNombre(String nombre);
}
