/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.laboratorio.service;

import com.laboratorio.model.Cita;
import java.time.LocalDate;
import java.util.List;

/**
 *
 * @author melanie
 */
public interface CitaService  extends CrudService<Cita>{
    Cita getById(Long idCita);
    
    List<Cita> findHistorialPorPaciente(String idPaciente);
    
     List<String> obtenerHorasOcupadas(LocalDate fecha);
}
