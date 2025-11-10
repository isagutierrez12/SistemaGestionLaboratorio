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

    @Query("SELECT a FROM AuditoriaCriticos a " +
           "WHERE (:inicio IS NULL OR a.fechaHora >= :inicio) " +
           "AND (:fin IS NULL OR a.fechaHora <= :fin) " +
           "ORDER BY a.fechaHora DESC")
    List<AuditoriaCriticos> buscarPorRangoFechas(@Param("inicio") LocalDateTime inicio,
                                                  @Param("fin") LocalDateTime fin);

    @Query("SELECT a FROM AuditoriaCriticos a WHERE " +
           "LOWER(a.usuario) LIKE LOWER(CONCAT('%', :query, '%')) " +
           "OR LOWER(a.tipoEvento) LIKE LOWER(CONCAT('%', :query, '%')) " +
           "OR LOWER(a.descripcion) LIKE LOWER(CONCAT('%', :query, '%')) " +
           "ORDER BY a.fechaHora DESC")
    List<AuditoriaCriticos> buscarPorQuery(@Param("query") String query);
}