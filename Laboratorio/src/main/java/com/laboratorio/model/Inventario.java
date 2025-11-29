package com.laboratorio.model;

import jakarta.persistence.Column;
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
import javax.validation.constraints.Min;
import javax.validation.constraints.Pattern;
import lombok.Data;

@Entity
@Data
@Table(name = "inventario")
public class Inventario implements Serializable {

    private static final long serialVersionUID = 1l;
    @Id

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_inventario")
    private Long idInventario;
    @ManyToOne
    @JoinColumn(name = "id_insumo", referencedColumnName = "id_insumo")
    private Insumo insumo;

    @Pattern(regexp = "^[0-9]+$", message = "El código de barras solo puede contener números.")
    private String codigoBarras;

    @Min(value = 0, message = "Stock Actual no puede ser negativo.")
    private int stockActual;

    @Min(value = 0, message = "Stock Bloqueado no puede ser negativo.")
    private int stockBloqueado;

    @Min(value = 0, message = "Stock Mínimo no puede ser negativo.")
    private int stockMinimo;

    private LocalDate fechaVencimiento;
    private LocalDate fechaApertura;
    private Boolean activo;

}
