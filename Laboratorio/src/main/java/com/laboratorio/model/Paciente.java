/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.laboratorio.model;

import jakarta.persistence.*;
import java.io.Serializable;
import java.util.Date;
import lombok.Data;

@Entity
@Data
@Table(name = "paciente")
public class Paciente implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "id_paciente", nullable = false, updatable = false)
    private String idPaciente;


    private String nombre;

    @Column(name = "primer_apellido")
    private String primerApellido;

    @Column(name = "segundo_apellido")
    private String segundoApellido;

    @Column(name = "fecha_nacimiento")
    @Temporal(TemporalType.DATE)
    private Date fechaNacimiento;

    private String telefono;

    private String email;

    private boolean activo;

    @Column(name = "fecha_creacion")
    @Temporal(TemporalType.TIMESTAMP)
    private Date fechaCreacion;
    
    @Column(name = "cedula", length = 10)
    private String cedula;
    
    @Column(name = "contacto_emergencia", length = 15)
    private String contactoEmergencia;

    @Column(name = "padecimiento", length = 255)
    private String padecimiento;

    @Column(name = "alergia", length = 255)
    private String alergia;
}
