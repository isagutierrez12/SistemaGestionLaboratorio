/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.laboratorio.model;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import lombok.Data;

@Data
public class PerfilForm {

    @NotBlank(message = "El nombre es obligatorio.")
    @Size(max = 50, message = "El nombre no puede tener más de 50 caracteres.")
    @Pattern(
        regexp = "^[A-Za-zÁÉÍÓÚáéíóúñÑ ]+$",
        message = "El nombre solo puede contener letras y espacios."
    )
    private String nombre;

    @NotBlank(message = "El primer apellido es obligatorio.")
    @Size(max = 50, message = "El primer apellido no puede tener más de 50 caracteres.")
    @Pattern(
        regexp = "^[A-Za-zÁÉÍÓÚáéíóúñÑ ]+$",
        message = "El primer apellido solo puede contener letras y espacios."
    )
    private String primerApellido;

    @NotBlank(message = "El segundo apellido es obligatorio.")
    @Size(max = 50, message = "El segundo apellido no puede tener más de 50 caracteres.")
    @Pattern(
        regexp = "^[A-Za-zÁÉÍÓÚáéíóúñÑ ]+$",
        message = "El segundo apellido solo puede contener letras y espacios."
    )
    private String segundoApellido;
}
