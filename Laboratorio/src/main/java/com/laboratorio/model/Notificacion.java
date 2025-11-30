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
   
    private String tipo; 
    private String titulo; 
    private String mensaje; 
    private String ipOrigen;
    private LocalDateTime fechaCreacion; 
    private boolean leida; 
    @ManyToOne
    @JoinColumn(name = "id_inventario", nullable = true)
    private Inventario inventario; 
    
    public Notificacion(String tipo, String titulo, String mensaje, String ipOrigen) {
        this.tipo = tipo;
        this.titulo = titulo;
        this.mensaje = mensaje;
        this.ipOrigen = ipOrigen;
        this.fechaCreacion = LocalDateTime.now();
        this.leida = false;
        this.inventario = null;
    }
    public Notificacion(){
        
    }
    
}
