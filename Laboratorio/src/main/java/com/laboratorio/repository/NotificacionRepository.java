/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.laboratorio.repository;


import com.laboratorio.model.Inventario;
import com.laboratorio.model.Notificacion;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@EnableJpaRepositories
public interface NotificacionRepository extends JpaRepository<Notificacion, Long>{
    public boolean existsByInventarioAndLeidaFalse(Inventario inventario);
    public List<Notificacion> findByFechaCreacionAfterOrderByFechaCreacionDesc(LocalDateTime fecha);
     boolean existsByInventario_IdInventarioAndTituloContaining(Long idInventario, String titulo);
}
