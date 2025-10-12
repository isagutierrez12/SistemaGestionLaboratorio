/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.laboratorio.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.util.Date;
import lombok.Data;

@Entity
@Data
@Table(name = "cita")
public class Cita {
    private static final long serialVersionUID = 1L;
       @Id
    @Column(name = "id_cita", nullable = false, updatable = false)
     private Long idCita; 
       private Long idSolicitud; 
       private Long idUsuario; 
       private Date fechaCita;
       private String notas; 
       private String estado;
    
}
