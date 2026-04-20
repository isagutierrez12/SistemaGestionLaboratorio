package com.laboratorio.repository;

import com.laboratorio.model.DetallePaquete;
import com.laboratorio.model.Paquete;
import com.laboratorio.model.Examen;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.repository.query.Param;

@EnableJpaRepositories
public interface DetallePaqueteRepository extends JpaRepository<DetallePaquete, Long> {

    List<DetallePaquete> findByPaqueteIdPaquete(Long idPaquete);

    boolean existsByPaqueteIdPaqueteAndExamenIdExamen(Long idPaquete, Long idExamen);

    long deleteByPaqueteIdPaqueteAndExamenIdExamen(Long idPaquete, Long idExamen);
    
    void deleteByPaqueteIdPaquete(Long idPaquete);

    @Query("""
           SELECT dp.paquete.idPaquete, dp.examen.idExamen, dp.paquete.nombre
           FROM DetallePaquete dp
           WHERE dp.paquete.idPaquete IN :idsPaquete
           """)
    List<Object[]> findExamenesDePaquetes(@Param("idsPaquete") List<Long> idsPaquete);
}