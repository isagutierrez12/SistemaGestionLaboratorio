package com.laboratorio.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Entity
@Data
@Table(name = "usuario")
public class Usuario implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_usuario")
    private Long idUsuario;

    @NotBlank(message = "El nombre es obligatorio")
    @Size(max = 50, message = "Máximo 50 caracteres")
    @Pattern(regexp = "^[A-Za-zÁÉÍÓÚáéíóúñÑ ]+$",
            message = "El nombre solo puede contener letras y espacios")
    private String nombre;

    @NotBlank(message = "El primer apellido es obligatorio")
    @Size(max = 50)
    @Pattern(regexp = "^[A-Za-zÁÉÍÓÚáéíóúñÑ ]+$",
            message = "El apellido solo puede contener letras y espacios")
    private String primerApellido;

    @NotBlank(message = "El segundo apellido es obligatorio")
    @Size(max = 50)
    @Pattern(regexp = "^[A-Za-zÁÉÍÓÚáéíóúñÑ ]+$",
            message = "El apellido solo puede contener letras y espacios")
    private String segundoApellido;

    @NotBlank(message = "El nombre de usuario es obligatorio")
    @Size(min = 4, max = 20)
    private String username;

    @NotBlank(message = "La contraseña es obligatoria")
    @Size(min = 6, message = "La contraseña debe tener al menos 6 caracteres")
    private String password;

    private Boolean activo;

    private Date fechaCreacion;

    @NotBlank(message = "La cédula es obligatoria")
    @Pattern(regexp = "\\d{1,9}", message = "La cédula debe tener máximo 9 dígitos numéricos")
    private String cedula;

    @OneToMany
    @JoinColumn(name = "id_usuario", updatable = false)
    private List<Rol> roles;

    public String getNombreCompleto() {
        return nombre + " " + primerApellido;
    }
}
