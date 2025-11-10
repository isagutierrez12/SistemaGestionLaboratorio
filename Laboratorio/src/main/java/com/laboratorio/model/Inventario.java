/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.laboratorio.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import lombok.Data;

@Entity
@Data
@Table(name = "inventario")
public class Inventario implements Serializable{

    private static final long serialVersionUID = 1l;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idInventario;
     @ManyToOne
    @JoinColumn(name = "id_insumo", referencedColumnName = "id_insumo")
    private Insumo insumo;
    private String codigoBarras; 
    private int stockActual; 
    private int stockBloqueado; 
    private int stockMinimo; 
    private LocalDate fechaVencimiento; 
    private LocalDate fechaApertura; 
    private boolean activo; 

}
