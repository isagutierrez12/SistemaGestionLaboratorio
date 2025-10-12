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
@Table(name = "solicitud")
public class Solicitud {
       private static final long serialVersionUID = 1L;
           @Id
    @Column(name = "id_solicitud", nullable = false, updatable = false)
           private Long idSolicitud;
           private String idPaciente;
           private Long idUsuario;
           private Date fechaSolicitud;
           private double precioTotal;
           private String estado;
           
           
}
