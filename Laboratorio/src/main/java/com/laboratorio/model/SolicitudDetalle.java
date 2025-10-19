/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.laboratorio.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;


@Entity
@Data
@Table(name = "solicitud_detalle")
public class SolicitudDetalle {
      private static final long serialVersionUID = 1L;
    
    @Id
    @Column(name = "id_solicitud_detalle", nullable = false, updatable = false)
    private Long idSolicitudDetalle; 
    private Long idSolicitud;
    private Long idExamen;
    private Long idPaquete;
}
