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
import javax.validation.constraints.Digits;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
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

    @Column(name = "codigo", nullable = false, unique = true)
    private String codigo;

    @NotBlank(message = "El nombre es obligatorio.")
    @Pattern(regexp = "^[A-Za-zÁÉÍÓÚáéíóúñÑ ]+$", message = "El nombre solo puede contener letras o espacios.")
    @Column(unique = true)
    private String nombre;

    @NotBlank(message = "La descripción es obligatoria.")
    @Pattern(regexp = "^[A-Za-zÁÉÍÓÚáéíóúñÑ ]+$", message = "La descripción solo puede contener letras o espacios.")
    private String descripcion;

    @NotNull(message = "El precio es obligatorio.")
    @Digits(integer = 10, fraction = 2, message = "El precio debe ser numérico.")
    private BigDecimal precio;

    private Boolean activo;

    @OneToMany(mappedBy = "paquete", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private Set<DetallePaquete> detalles = new LinkedHashSet<>();

    /**
     * Helpers
     */
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
