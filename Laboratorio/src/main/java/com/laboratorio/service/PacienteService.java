package com.laboratorio.service;

import com.laboratorio.model.Paciente;
import java.util.List;

public interface PacienteService {

    List<Paciente> findAll();
    Paciente findById(String id); 
    Paciente save(Paciente paciente);
    void delete(String id);
    
    int getMaxSequenceForYear(String anio);
    List<Paciente> buscarPacientes(String query);
}
