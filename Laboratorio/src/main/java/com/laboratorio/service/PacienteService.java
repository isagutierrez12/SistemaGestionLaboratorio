/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.laboratorio.service;

import com.laboratorio.model.Paciente;
import java.util.List;

public interface PacienteService {
    List<Paciente> getPacientes();
    Paciente save(Paciente paciente);
    int getMaxSequenceForYear(String anio);
    
    List<Paciente> buscarPacientes(String query);
    
    List<Paciente> buscarPacientesInactivos(String query);
    
    public Paciente getPaciente(String id);
    
    List<Paciente> getPacientesActivos();
    
    List<Paciente> getPacientesInactivos();
}

