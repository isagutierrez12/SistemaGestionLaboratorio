/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.laboratorio.repository;

import com.laboratorio.model.Auditoria;
import com.laboratorio.model.AuditoriaCriticos;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@EnableJpaRepositories
public interface AuditoriaRepository extends JpaRepository<Auditoria, Long> {

@Query("SELECT a FROM Auditoria a WHERE "
            + "LOWER(a.usuario) LIKE LOWER(CONCAT('%', :query, '%')) "
            + "OR LOWER(a.modulo) LIKE LOWER(CONCAT('%', :query, '%')) "
            + "OR LOWER(a.accion) LIKE LOWER(CONCAT('%', :query, '%')) "
            + "ORDER BY a.fechaHora DESC")
    List<Auditoria> buscarPorQuery(@Param("query") String query);

    @Query("SELECT a FROM Auditoria a ORDER BY a.fechaHora DESC")
    List<Auditoria> findAllOrderByFechaHoraDesc();

    List<Auditoria> findByFechaHoraBeforeOrderByFechaHoraDesc(LocalDateTime fin);

    List<Auditoria> findByFechaHoraAfterOrderByFechaHoraDesc(LocalDateTime inicio);

    List<Auditoria> findByFechaHoraBetweenOrderByFechaHoraDesc(LocalDateTime inicio, LocalDateTime fin);
}

