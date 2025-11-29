package com.laboratorio.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.Table;
import java.io.Serializable;
import lombok.Data;

@Entity
@Data
@Table(name = "insumo")
public class Insumo implements Serializable {
     private static final long serialVersionUID = 1l;
       @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_insumo")
    private Long idInsumo; 
    private String nombre; 
    private String tipo; 
    private int cantidadPorUnidad;
    private String unidadMedida; 
    private boolean activo; 
    
}
