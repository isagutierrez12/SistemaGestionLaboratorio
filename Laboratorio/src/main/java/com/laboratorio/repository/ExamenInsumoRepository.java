/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.laboratorio.repository;

import com.laboratorio.model.ExamenInsumo;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ExamenInsumoRepository extends JpaRepository<ExamenInsumo, Long> {

    @Query(value = """
          SELECT * FROM examen_insumo
          WHERE id_examen IN (?1)
      """, nativeQuery = true)
    List<ExamenInsumo> findByExamenInList(List<Long> examenes);
}
