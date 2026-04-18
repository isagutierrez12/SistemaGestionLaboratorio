package com.laboratorio.model;


import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ExamenInsumoForm {

    @NotNull(message = "Debe seleccionar un insumo.")
    private Long idInsumo;

    @Min(value = 1, message = "La cantidad necesaria debe ser mayor a 0.")
    private int cantidadNecesaria;
}
