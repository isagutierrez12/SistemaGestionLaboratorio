/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.laboratorio.repository;

import com.laboratorio.model.SolicitudDetalle;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

/**
 *
 * @author soportetecnico
 */

@EnableJpaRepositories
public interface SolicitudDetalleRepository extends JpaRepository<SolicitudDetalle,Long>{
        List<SolicitudDetalle> findBySolicitudIdSolicitud(Long idSolicitud);

}
