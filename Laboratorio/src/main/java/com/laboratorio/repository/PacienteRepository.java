/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.laboratorio.repository;

import com.laboratorio.model.Paciente;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PacienteRepository extends JpaRepository<Paciente, String> {

    @Query("SELECT COALESCE(MAX(CAST(SUBSTRING(p.idPaciente, 5, 4) AS int)), 0) FROM Paciente p WHERE p.idPaciente LIKE CONCAT('P', :anio, '%')")
    int getMaxSequenceForYear(@Param("anio") String anio);
    
    @Query("SELECT p FROM Paciente p " +
           "WHERE LOWER(p.nombre) LIKE LOWER(CONCAT('%', :query, '%')) " +
           "OR LOWER(p.cedula) LIKE LOWER(CONCAT('%', :query, '%')) " +
           "OR LOWER(p.email) LIKE LOWER(CONCAT('%', :query, '%'))")
    List<Paciente> buscarPorQuery(@Param("query") String query);
}
