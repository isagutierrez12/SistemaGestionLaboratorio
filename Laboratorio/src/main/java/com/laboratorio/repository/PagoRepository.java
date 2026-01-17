/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.laboratorio.repository;

import com.laboratorio.model.Pago;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.repository.query.Param;

@EnableJpaRepositories
public interface PagoRepository extends JpaRepository<Pago, Long> {
    Optional<Pago> findByCita_IdCita(Long idCita);
    boolean existsByCita_IdCita(Long idCita);
    
    @Query("""
        select coalesce(sum(p.monto), 0)
        from Pago p
        join p.cita c
        where c.fechaCita between :desde and :hasta
    """)
    Double sumMontoEnRango(@Param("desde") LocalDateTime desde,
                           @Param("hasta") LocalDateTime hasta);
}