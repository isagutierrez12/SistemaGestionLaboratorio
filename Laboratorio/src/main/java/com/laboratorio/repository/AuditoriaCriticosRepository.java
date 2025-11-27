/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.laboratorio.repository;

import com.laboratorio.model.AuditoriaCriticos;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface AuditoriaCriticosRepository extends JpaRepository<AuditoriaCriticos, Long> {

    @Query("SELECT a FROM AuditoriaCriticos a ORDER BY a.fechaHora DESC")
    List<AuditoriaCriticos> findAllOrderByFechaHoraDesc();

    @Query("SELECT a FROM AuditoriaCriticos a WHERE a.usuario LIKE %:usuario% ORDER BY a.fechaHora DESC")
    List<AuditoriaCriticos> buscarPorUsuario(@Param("usuario") String usuario);

    @Query("SELECT a FROM AuditoriaCriticos a WHERE a.tipoEvento LIKE %:tipoEvento% ORDER BY a.fechaHora DESC")
    List<AuditoriaCriticos> buscarPorTipoEvento(@Param("tipoEvento") String tipoEvento);

    @Query("SELECT a FROM AuditoriaCriticos a WHERE a.fechaHora >= :inicio AND a.fechaHora <= :fin ORDER BY a.fechaHora DESC")
    List<AuditoriaCriticos> buscarPorRangoFechas(@Param("inicio") LocalDateTime inicio, @Param("fin") LocalDateTime fin);

    @Query("SELECT a FROM AuditoriaCriticos a WHERE a.fechaHora >= :inicio ORDER BY a.fechaHora DESC")
    List<AuditoriaCriticos> buscarDesdeFecha(@Param("inicio") LocalDateTime inicio);

    @Query("SELECT a FROM AuditoriaCriticos a WHERE a.fechaHora <= :fin ORDER BY a.fechaHora DESC")
    List<AuditoriaCriticos> buscarHastaFecha(@Param("fin") LocalDateTime fin);
}
