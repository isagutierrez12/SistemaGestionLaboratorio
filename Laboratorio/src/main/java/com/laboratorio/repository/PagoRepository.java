/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.laboratorio.repository;

import com.laboratorio.model.Pago;
import com.laboratorio.model.PagoRow;
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
          and (:tipo is null or :tipo = '' or p.tipoPago = :tipo)
    order by c.fechaCita desc
    """)
    List<PagoRow> listarPagosDashboard(
            @Param("desde") LocalDateTime desde,
            @Param("hasta") LocalDateTime hasta,
            @Param("tipo") String tipo
    );

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
          and (:tipo is null or :tipo = '' or p.tipoPago = :tipo)
          and (
              :paciente is null or :paciente = '' or
              lower(concat(
                  coalesce(pac.nombre, ''),
                  case when pac.primerApellido is not null then concat(' ', pac.primerApellido) else '' end,
                  case when pac.segundoApellido is not null then concat(' ', pac.segundoApellido) else '' end
              )) like lower(concat('%', :paciente, '%'))
          )
        order by c.fechaCita desc
        """)
    List<PagoRow> listarPagosDashboardExport(
            @Param("desde") LocalDateTime desde,
            @Param("hasta") LocalDateTime hasta,
            @Param("tipo") String tipo,
            @Param("paciente") String paciente
    );

    @Query(value = """
        select 
            c.fecha_cita as fechaCita,
            concat(
                coalesce(pac.nombre, ''),
                case when pac.primer_apellido is not null then concat(' ', pac.primer_apellido) else '' end,
                case when pac.segundo_apellido is not null then concat(' ', pac.segundo_apellido) else '' end
            ) as paciente,
            p.monto as monto,
            p.tipo_pago as tipoPago
        from pago p
        join cita c on c.id_cita = p.id_cita
        join solicitud s on s.id_solicitud = c.id_solicitud
        join paciente pac on pac.id_paciente = s.id_paciente
        where c.fecha_cita between :desde and :hasta
          and (:tipo is null or :tipo = '' or p.tipo_pago = :tipo)
          and (:paciente is null or :paciente = '' or 
               lower(concat(coalesce(pac.nombre,''),' ',coalesce(pac.primer_apellido,''),' ',coalesce(pac.segundo_apellido,'')))
               like lower(concat('%', :paciente, '%')))
        order by c.fecha_cita desc
        """, nativeQuery = true)
    List<Object[]> listarPagosExportRaw(
            @Param("desde") LocalDateTime desde,
            @Param("hasta") LocalDateTime hasta,
            @Param("tipo") String tipo,
            @Param("paciente") String paciente
    );

}
