/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.laboratorio.model;

import java.time.LocalDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
@Data
@AllArgsConstructor 
@NoArgsConstructor
public class CitaCalendarioDTO {
    private Long idCita;
    private String paciente;
    private LocalDateTime fechaCita;
    private String estado;
    private String notas;
    private List<String> examenes;
    private List<String> paquetes;

}
