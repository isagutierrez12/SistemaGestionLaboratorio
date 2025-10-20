/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.laboratorio.model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import lombok.Data;

@Entity
@Data
@Table(name = "solicitud")
public class Solicitud {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_solicitud", nullable = false, updatable = false)
    private Long idSolicitud;

    // Relación con Paciente
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_paciente", referencedColumnName = "id_paciente")
    private Paciente paciente;

    // Relación con Usuario
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_usuario", referencedColumnName = "id_usuario")
    private Usuario usuario;
    
    @Column(name = "fecha_solicitud")
    private LocalDateTime fechaSolicitud;

    @Column(name = "precio_total")
    private double precioTotal;

    @Column(name = "estado", length = 5)
    private String estado;
    
    @OneToMany(mappedBy = "solicitud", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SolicitudDetalle> detalles = new ArrayList<>();    
    
    public void addDetalle(SolicitudDetalle detalle) {
    if (detalles == null) {
        detalles = new ArrayList<>();
    }
    detalle.setSolicitud(this);
    detalles.add(detalle);
    }
    // Relación con solicitud_detalle (exámenes y paquetes)
    //@OneToMany(mappedBy = "solicitud", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
   // private List<SolicitudDetalle> detalles;
           
}
