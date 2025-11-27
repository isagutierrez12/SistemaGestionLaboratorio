package com.laboratorio.repository;

import com.laboratorio.model.Paciente;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.repository.query.Param;

@EnableJpaRepositories
public interface PacienteRepository extends JpaRepository<Paciente, String> {

    @Query("SELECT COALESCE(MAX(CAST(SUBSTRING(p.idPaciente, 5, 4) AS int)), 0) FROM Paciente p WHERE p.idPaciente LIKE CONCAT('P', :anio, '%')")
    int getMaxSequenceForYear(@Param("anio") String anio);
    
    @Query("SELECT p FROM Paciente p " +
           "WHERE p.activo = true AND (" +
           "LOWER(p.nombre) LIKE LOWER(CONCAT('%', :query, '%')) " +
           "OR LOWER(p.cedula) LIKE LOWER(CONCAT('%', :query, '%')) " +
           "OR LOWER(p.email) LIKE LOWER(CONCAT('%', :query, '%')))")
    List<Paciente> buscarActivosPorQuery(@Param("query") String query);
    
    @Query("SELECT p FROM Paciente p " +
           "WHERE p.activo = false AND (" +
           "LOWER(p.nombre) LIKE LOWER(CONCAT('%', :query, '%')) " +
           "OR LOWER(p.cedula) LIKE LOWER(CONCAT('%', :query, '%')) " +
           "OR LOWER(p.email) LIKE LOWER(CONCAT('%', :query, '%')))")
    List<Paciente> buscarInactivosPorQuery(@Param("query") String query);
    
    public Paciente findByIdPaciente(String id);
    
    List<Paciente> findByActivoTrue();
    
    List<Paciente> findByActivoFalse();
        
    Optional<Paciente> findByCedula(String cedula);
    
    boolean existsByCedula(String cedula);

    boolean existsByTelefono(String telefono);

    boolean existsByEmail(String email);

}
