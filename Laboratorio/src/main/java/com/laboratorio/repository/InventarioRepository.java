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

    boolean existsByInsumo_IdInsumoAndActivoTrue(Long idInsumo);

    boolean existsByCodigoBarras(String codigoBarras);

    public Inventario findByCodigoBarras(String codigoBarras);

    @Query(value = """
        SELECT *
        FROM inventario i
        WHERE i.id_insumo = :idInsumo
          AND i.activo = true
          AND (i.fecha_vencimiento IS NULL OR i.fecha_vencimiento >= :hoy)
          AND (i.stock_actual - i.stock_bloqueado) >= :cantidadNecesaria
        ORDER BY i.fecha_apertura ASC NULLS LAST
        LIMIT 1
    """, nativeQuery = true)
    Inventario buscarInventarioParaBloqueo(@Param("idInsumo") Long idInsumo,
            @Param("hoy") LocalDate hoy,
            @Param("cantidadNecesaria") int cantidadNecesaria);

    @Query(value = """
        SELECT *
        FROM inventario i
        WHERE i.id_insumo = :idInsumo
          AND i.activo = true
          AND (i.fecha_vencimiento IS NULL OR i.fecha_vencimiento >= :hoy)
          AND i.stock_bloqueado >= :cantidadNecesaria
        ORDER BY i.fecha_apertura ASC NULLS LAST
        LIMIT 1
    """, nativeQuery = true)
    Inventario buscarInventarioConBloqueadoSuficiente(@Param("idInsumo") Long idInsumo,
            @Param("hoy") LocalDate hoy,
            @Param("cantidadNecesaria") int cantidadNecesaria);

    @Query(value = """
    SELECT *
    FROM inventario i
    WHERE i.id_insumo = :idInsumo
      AND i.activo = true
      AND (i.fecha_vencimiento IS NULL OR i.fecha_vencimiento >= :hoy)
      AND i.stock_bloqueado > 0
    ORDER BY i.fecha_apertura ASC NULLS LAST
    """, nativeQuery = true)
    List<Inventario> buscarLotesConBloqueado(@Param("idInsumo") Long idInsumo,
            @Param("hoy") LocalDate hoy);

    @Query(value = """
    SELECT *
    FROM inventario i
    WHERE i.id_insumo = :idInsumo
      AND i.activo = true
      AND (i.fecha_vencimiento IS NULL OR i.fecha_vencimiento >= :hoy)
      AND (i.stock_actual - i.stock_bloqueado) > 0
    ORDER BY i.fecha_apertura ASC NULLS LAST
    """, nativeQuery = true)
    List<Inventario> buscarLotesDisponiblesParaDescuento(@Param("idInsumo") Long idInsumo,
            @Param("hoy") LocalDate hoy);
}
