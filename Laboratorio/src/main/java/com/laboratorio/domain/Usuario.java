/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package com.laboratorio.domain;

import jakarta.persistence.*;
import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 *
 * @author melanie
 */
@Data
@Entity
@Table(name="usuario")
public class Usuario implements Serializable{
    private static final long serialVersionUID = 1l;
    @Id
    @GeneratedValue(strategy =  GenerationType.IDENTITY)
    @Column(name="usuario_id")
    private Long idUsuario;
    private String nombre;
    private String primerApellido;
    private String segundoApellido;
    private String usuario;
    private String password;
    private boolean activo;
    private Date fechaCreacion;
   
    
}
