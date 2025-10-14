/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.laboratorio.model;

import jakarta.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.List;
import lombok.Data;
import org.hibernate.annotations.processing.Pattern;

@Entity
@Data
@Table(name = "usuario")
public class Usuario implements Serializable {

    private static final long serialVersionUID = 1l;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_usuario")
    private Long idUsuario;
    private String nombre;
    private String primerApellido;
    private String segundoApellido;
    private String username;
    private String password;
    private boolean activo;
    private Date fechaCreacion;
    
    @Column(length = 9)
    private String cedula;
  
    @OneToMany
    @JoinColumn(name = "id_usuario", updatable = false)
    private List<Rol> roles;

}
