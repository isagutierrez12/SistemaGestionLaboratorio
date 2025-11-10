/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.laboratorio.repository;

import com.laboratorio.model.Insumo;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;


public interface InsumoRepository extends JpaRepository<Insumo, Long>{
  
}
