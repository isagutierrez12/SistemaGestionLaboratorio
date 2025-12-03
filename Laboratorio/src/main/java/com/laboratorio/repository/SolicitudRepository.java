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
import java.time.LocalDateTime; 


@EnableJpaRepositories
public interface SolicitudRepository extends JpaRepository<Solicitud, Long>{
    
   @Query("SELECT s FROM Solicitud s " +
       "WHERE LOWER(s.estado) LIKE LOWER(CONCAT('%', :query, '%')) " +
       "   OR LOWER(s.paciente.idPaciente) LIKE LOWER(CONCAT('%', :query, '%')) " +
       "   OR CAST(s.idSolicitud AS string) LIKE CONCAT('%', :query, '%')")
    List<Solicitud> buscarPorQuery(@Param("query") String query);
    
    @Query("""
           SELECT COALESCE(SUM(s.precioTotal), 0)
           FROM Solicitud s
           WHERE s.fechaSolicitud BETWEEN :inicio AND :fin
           """)
    Double calcularIngresos(@Param("inicio") LocalDateTime inicio,
                            @Param("fin") LocalDateTime fin);

    @Query("""
           SELECT COUNT(DISTINCT s.paciente.idPaciente)
           FROM Solicitud s
           WHERE s.fechaSolicitud BETWEEN :inicio AND :fin
           """)
    Long contarPacientesAtendidos(@Param("inicio") LocalDateTime inicio,
                                  @Param("fin") LocalDateTime fin);
}
