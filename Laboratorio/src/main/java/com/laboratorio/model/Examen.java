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
@Table(name = "examen")
public class Examen {
    private static final long serialVersionUID = 1L;
    
    @Id
    @Column(name = "id_examen", nullable = false, updatable = false)
    private long idExamen; 
    private String codig;
    private String nombre; 
    private String area; 
    private double precio; 
    private String condiciones; 
    private String unidad; 
    private int valorMinimo; 
    private int valorMaximo; 
    private boolean activo; 
}
