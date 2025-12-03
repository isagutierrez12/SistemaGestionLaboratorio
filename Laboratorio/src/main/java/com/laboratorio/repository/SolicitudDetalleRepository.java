/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.laboratorio.repository;

import com.laboratorio.model.SolicitudDetalle;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.repository.query.Param;
import com.laboratorio.model.ExamenTop;
import java.time.LocalDateTime;
import org.springframework.data.domain.Pageable;

/**
 *
 * @author soportetecnico
 */
@EnableJpaRepositories
public interface SolicitudDetalleRepository extends JpaRepository<SolicitudDetalle, Long> {

    List<SolicitudDetalle> findBySolicitudIdSolicitud(Long idSolicitud);

    @Query(value = """
    SELECT sd.id_examen
    FROM solicitud_detalle sd
    JOIN cita c ON c.id_solicitud = sd.id_solicitud
    WHERE c.id_cita = ?1
      AND sd.id_examen IS NOT NULL
""", nativeQuery = true)
    List<Long> findExamenesByCita(Long idCita);

    @Query("""
       SELECT COUNT(d)
       FROM Cita c
       JOIN c.solicitud s
       JOIN s.detalles d
       WHERE c.fechaCita BETWEEN :inicio AND :fin
         AND d.examen IS NOT NULL
         AND (c.estado IS NULL OR UPPER(c.estado) <> 'CANCELADA')
       """)
    Long contarExamenesIndividualesEnPeriodo(@Param("inicio") LocalDateTime inicio,
            @Param("fin") LocalDateTime fin);

    @Query("""
       SELECT COUNT(dp)
       FROM Cita c
       JOIN c.solicitud s
       JOIN s.detalles d
       JOIN d.paquete p
       JOIN p.detalles dp
       WHERE c.fechaCita BETWEEN :inicio AND :fin
         AND (c.estado IS NULL OR UPPER(c.estado) <> 'CANCELADA')
       """)
    Long contarExamenesEnPaquetesEnPeriodo(@Param("inicio") LocalDateTime inicio,
            @Param("fin") LocalDateTime fin);

    @Query("""
       SELECT new com.laboratorio.model.ExamenTop(
                d.examen.nombre,
                COUNT(d)
              )
       FROM Cita c
       JOIN c.solicitud s
       JOIN s.detalles d
       WHERE c.fechaCita BETWEEN :inicio AND :fin
         AND d.examen IS NOT NULL
         AND (c.estado IS NULL OR UPPER(c.estado) <> 'CANCELADA')
         AND (:area IS NULL OR LOWER(d.examen.area) = LOWER(:area))
       GROUP BY d.examen.nombre
       """)
    List<ExamenTop> topExamenesIndividuales(@Param("inicio") LocalDateTime inicio,
            @Param("fin") LocalDateTime fin,
            @Param("area") String area);

    @Query("""
       SELECT new com.laboratorio.model.ExamenTop(
                dp.examen.nombre,
                COUNT(dp)
              )
       FROM Cita c
       JOIN c.solicitud s
       JOIN s.detalles d
       JOIN d.paquete p
       JOIN p.detalles dp
       WHERE c.fechaCita BETWEEN :inicio AND :fin
         AND (c.estado IS NULL OR UPPER(c.estado) <> 'CANCELADA')
         AND (:area IS NULL OR LOWER(dp.examen.area) = LOWER(:area))
       GROUP BY dp.examen.nombre
       """)
    List<ExamenTop> topExamenesDesdePaquetes(@Param("inicio") LocalDateTime inicio,
            @Param("fin") LocalDateTime fin,
            @Param("area") String area);

}
