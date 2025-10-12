/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.laboratorio.repository;

import com.laboratorio.model.Inventario;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 *
 * @author melanie
 */
public interface InventarioRepository extends JpaRepository<Inventario, Long>{
    
}
