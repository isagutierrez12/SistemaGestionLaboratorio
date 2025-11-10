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

}
