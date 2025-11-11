/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.laboratorio.repository;

import com.laboratorio.model.Inventario;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
@EnableJpaRepositories
public interface InventarioRepository extends JpaRepository<Inventario, Long>{
    public List<Inventario> findByFechaVencimientoBetween(LocalDate hoy, LocalDate fechaLimite);
    public List<Inventario> findByFechaVencimientoBefore(LocalDate hoy);
     public Inventario findByInsumo_IdInsumo(Long idInsumo);
}
