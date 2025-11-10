/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.laboratorio.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDate;
import java.util.Date;
import lombok.Data;

@Entity
@Data
@Table(name = "inventario")
public class Inventario {
@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_inventario")
    private Long idInventario;

    /*@ManyToOne
    @JoinColumn(name = "id_insumo", nullable = false)
    private Insumo insumo; // Asumiendo que tienes una entidad Insumo*/

    @Column(name = "codigo_barras", nullable = false)
    private String codigoBarras;

    @Column(name = "stock_actual")
    private int stockActual;

    @Column(name = "stock_bloqueado")
    private int stockBloqueado;

    @Column(name = "stock_minimo")
    private int stockMinimo;

    @Column(name = "fecha_vencimiento")
    private LocalDate  fechaVencimiento;

    @Column(name = "fecha_apertura")
    private LocalDate   fechaApertura;

    @Column(name = "activo")
    private boolean activo;
}
