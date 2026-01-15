/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.laboratorio.repository;

import com.laboratorio.model.Cita;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.repository.query.Param;

@EnableJpaRepositories
public interface CitaRepository extends JpaRepository<Cita, Long> {

    @Query("SELECT c FROM Cita c WHERE c.usuario.idUsuario = :idUsuario")
    List<Cita> findByUsuario(Long idUsuario);

    @Query("SELECT c FROM Cita c WHERE c.estado = :estado")
    List<Cita> findByEstado(String estado);

    @Query("""
           SELECT COALESCE(SUM(c.solicitud.precioTotal), 0)
           FROM Cita c
           WHERE c.fechaCita BETWEEN :inicio AND :fin
             AND (c.estado IS NULL OR UPPER(c.estado) <> 'CANCELADA')
           """)
    Double calcularIngresosPorCita(@Param("inicio") LocalDateTime inicio,
            @Param("fin") LocalDateTime fin);

    @Query("""
           SELECT COUNT(DISTINCT c.solicitud.paciente.idPaciente)
           FROM Cita c
           WHERE c.fechaCita BETWEEN :inicio AND :fin
             AND (c.estado IS NULL OR UPPER(c.estado) <> 'CANCELADA')
           """)
    Long contarPacientesAtendidosPorCita(@Param("inicio") LocalDateTime inicio,
            @Param("fin") LocalDateTime fin);

    @Query("""
           SELECT DISTINCT c
           FROM Cita c
           JOIN FETCH c.solicitud s
           LEFT JOIN FETCH s.paciente p
           LEFT JOIN FETCH s.detalles d
           LEFT JOIN FETCH d.examen e
           LEFT JOIN FETCH d.paquete pa
           WHERE c.fechaCita BETWEEN :inicio AND :fin
           """)
    List<Cita> findCitasConDetallesEnRango(
            @Param("inicio") LocalDateTime inicio,
            @Param("fin") LocalDateTime fin
    );

    @Query("""
       SELECT DISTINCT c
       FROM Cita c
       JOIN FETCH c.solicitud s
       JOIN FETCH s.paciente p
       LEFT JOIN FETCH s.detalles d
       LEFT JOIN FETCH d.examen e
       LEFT JOIN FETCH d.paquete paq
       WHERE p.idPaciente = :idPaciente
       ORDER BY c.fechaCita DESC
       """)
    List<Cita> findHistorialPorPaciente(@Param("idPaciente") String idPaciente);
    @Query("""
       SELECT FUNCTION('to_char', c.fechaCita, 'HH24:MI')
       FROM Cita c
       WHERE DATE(c.fechaCita) = :fecha
    """)
    List<String> horasOcupadasPorFecha(@Param("fecha") LocalDate fecha);
}
