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

    @Query("""
    select new com.laboratorio.model.PagoRow(
        c.fechaCita,
        concat(
            coalesce(pac.nombre, ''),
            case when pac.primerApellido is not null then concat(' ', pac.primerApellido) else '' end,
            case when pac.segundoApellido is not null then concat(' ', pac.segundoApellido) else '' end
        ),
        p.monto,
        p.tipoPago
    )
    from Pago p
    join p.cita c
    join c.solicitud s
    join s.paciente pac
    where c.fechaCita between :desde and :hasta
      and (:tipo is null or p.tipoPago = :tipo)
    order by c.fechaCita desc
    """)
    List<com.laboratorio.model.PagoRow> listarPagosDashboard(
            @Param("desde") LocalDateTime desde,
            @Param("hasta") LocalDateTime hasta,
            @Param("tipo") String tipo
    );

}
