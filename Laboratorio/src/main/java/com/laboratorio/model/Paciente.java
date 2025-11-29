/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.laboratorio.model;

import jakarta.persistence.*;
import java.io.Serializable;
import java.util.Date;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

@Entity
@Data
@Table(name = "paciente")
public class Paciente implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "id_paciente", nullable = false, updatable = false)
    private String idPaciente;

    @NotBlank(message = "El nombre es obligatorio")
    @Size(max = 25, message = "Máximo 50 caracteres")
    @Pattern(regexp = "^[A-Za-zÁÉÍÓÚáéíóúñÑ ]+$", message = "El nombre solo puede contener letras y espacios")
    private String nombre;

    @NotBlank(message = "El primer apellido es obligatorio")
    @Size(max = 25)
    @Pattern(regexp = "^[A-Za-zÁÉÍÓÚáéíóúñÑ ]+$", message = "El primer apellido solo puede contener letras y espacios")
    private String primerApellido;

    @Size(max = 5)
    @Pattern(regexp = "^[A-Za-zÁÉÍÓÚáéíóúñÑ ]*$", message = "El segundo apellido solo puede contener letras y espacios")
    private String segundoApellido;

    @NotNull(message = "La fecha de nacimiento es obligatoria")
    @Temporal(TemporalType.DATE)
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date fechaNacimiento;

    @NotBlank(message = "El teléfono es obligatorio")
    @Pattern(regexp = "\\d{8,12}", message = "El teléfono debe tener entre 8 y 12 números")
    private String telefono;

    @Email(message = "El correo debe ser válido")
    @Pattern(regexp = "^[\\w.-]+@[\\w.-]+\\.[a-zA-Z]{2,6}$", message = "Formato de correo inválido")
    private String email;

    private Boolean activo;

    @Temporal(TemporalType.TIMESTAMP)
    private Date fechaCreacion;

    @NotBlank(message = "La cédula es obligatoria")
    @Pattern(regexp = "\\d{1,9}", message = "La cédula debe tener máximo 9 dígitos numéricos")
    private String cedula;

    @Pattern(regexp = "\\d{8,12}", message = "El contacto de emergencia debe tener entre 8 y 12 números")
    private String contactoEmergencia;

    @Size(max = 255)
    @Pattern(regexp = "^[A-Za-zÁÉÍÓÚáéíóúñÑ ]*$", message = "El padecimiento solo puede contener letras y espacios")
    private String padecimiento;

    @Size(max = 255)
    @Pattern(regexp = "^[A-Za-zÁÉÍÓÚáéíóúñÑ ]*$", message = "La alergia solo puede contener letras y espacios")
    private String alergia;

}
