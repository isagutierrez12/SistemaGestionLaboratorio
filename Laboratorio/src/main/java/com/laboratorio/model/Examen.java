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
import java.math.BigDecimal;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import lombok.Data;

@Entity
@Data
@Table(name = "examen")
public class Examen {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_examen", nullable = false, updatable = false)
    private Long idExamen;

    @Column(name = "codigo", nullable = false, updatable = false, insertable = false)
    private String codigo;

    @NotBlank(message = "El nombre es obligatorio")
    @Size(max = 100, message = "Máximo 100 caracteres")
    @Pattern(regexp = "^[A-Za-zÁÉÍÓÚáéíóúñÑ ]+$", message = "El nombre solo puede contener letras y espacios")
    private String nombre;

    @NotBlank(message = "El área es obligatoria")
    @Size(max = 50, message = "Máximo 50 caracteres")
    @Pattern(regexp = "^[A-Za-zÁÉÍÓÚáéíóúñÑ ]+$", message = "El área solo puede contener letras y espacios")
    private String area;

    @NotNull(message = "El precio es obligatorio")
    @DecimalMin(value = "0.0", inclusive = false, message = "El precio debe ser mayor que 0")
    private BigDecimal precio;

    @Size(max = 255)
    @Pattern(regexp = "^[A-Za-zÁÉÍÓÚáéíóúñÑ ]*$", message = "Las condiciones solo pueden contener letras y espacios")
    private String condiciones;

    @Size(max = 10, message = "Máximo 10 caracteres")
    @Pattern(regexp = "^[A-Za-z0-9/]+$", message = "Unidad inválida")
    private String unidad;

    @DecimalMin(value = "0.0", inclusive = true, message = "El valor mínimo debe ser mayor o igual a 0")
    private float valorMinimo;

    @DecimalMin(value = "0.0", inclusive = true, message = "El valor máximo debe ser mayor o igual a 0")
    private float valorMaximo;

    private Boolean activo;
}
