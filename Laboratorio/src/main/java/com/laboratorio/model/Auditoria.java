/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.laboratorio.model;

import jakarta.persistence.*;
import java.io.Serializable;
import java.util.Date;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

@Entity
@Data
@Table(name = "auditoria")
public class Auditoria implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "usuario", nullable = false, length = 100)
    private String usuario;

    @Column(name = "modulo", nullable = false, length = 100)
    private String modulo;

    @Column(name = "accion", nullable = false, length = 50)
    private String accion;

    @Column(name = "descripcion", columnDefinition = "TEXT", nullable = true)
    private String descripcion;

    @Column(name = "fecha_hora", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date fechaHora = new Date();

    @Column(name = "ip_origen", length = 45)
    private String ipOrigen;

    private String entidadAfectada;

    private String idEntidad;

    @Column(name = "campo_afectado", length = 100, nullable = true)
    private String campoAfectado;

    @Column(name = "valor_anterior", columnDefinition = "TEXT", nullable = true)
    private String valorAnterior;

    @Column(name = "valor_nuevo", columnDefinition = "TEXT", nullable = true)
    private String valorNuevo;
    
    @Column(name = "datos_adicionales", columnDefinition = "TEXT")
    private String datosAdicionales;

}
