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
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.util.LinkedHashSet;
import java.util.Set;
import lombok.Data;

@Entity
@Data
@Table(name = "paquete")
public class Paquete {
    private static final long serialVersionUID = 1L;
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_paquete", nullable = false, updatable = false)
    private Long idPaquete; 
    private String codigo;
    private String nombre; 
    private String descripcion; 
    private BigDecimal precio;  
    private boolean activo; 
    
    @OneToMany(mappedBy = "paquete", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private Set<DetallePaquete> detalles = new LinkedHashSet<>();

    /** Helpers */
    public void addDetalle(DetallePaquete dp) {
        detalles.add(dp);
        dp.setPaquete(this);
    }
    public void removeDetalle(DetallePaquete dp) {
        detalles.remove(dp);
        dp.setPaquete(null);
    }
      public Paquete() {
    }

    public Paquete(Long idPaquete) {
        this.idPaquete = idPaquete;
    }
}
