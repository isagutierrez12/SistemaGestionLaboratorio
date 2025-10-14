/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.laboratorio.repository;


import com.laboratorio.model.Solicitud;
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
public interface SolicitudRepository extends JpaRepository<Solicitud, Long>{
    
    @Query("SELECT s FROM Solicitud s " +
       "WHERE LOWER(str(s.idSolicitud)) LIKE LOWER(CONCAT('%', :query, '%')) " +
       "OR LOWER(s.estado) LIKE LOWER(CONCAT('%', :query, '%'))")
List<Solicitud> buscarPorQuery(@Param("query") String query);

}
