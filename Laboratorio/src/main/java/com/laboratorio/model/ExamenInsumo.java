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

@Data
@Entity
@Table(name = "examen_insumo")
public class ExamenInsumo {
     private static final long serialVersionUID = 1L;
       @Id
    @Column(name = "id_examen_insumo", nullable = false, updatable = false)
    private Long idExamenInsumo; 
    private Long idExamen;
    private Long idInsumo; 
    private int cantidadNecesaria; 
}
