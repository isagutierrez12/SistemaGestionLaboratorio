/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.laboratorio.model;

import jakarta.persistence.*;
import jakarta.persistence.Table;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.Data;

@Entity
@Data
@Table(name = "notificacion")
public class Notificacion {
      @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
  
    private Long idNotificacion; 
   
    private String titulo; 
    private String mensaje; 
    private LocalDateTime fechaCreacion; 
    private boolean leida; 
    @ManyToOne
    @JoinColumn(name = "id_inventario", nullable = false)
    private Inventario inventario; 
}
