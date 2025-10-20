package com.laboratorio.service;

import com.laboratorio.model.Paciente;
import java.util.List;

public interface PacienteService extends CrudService<Paciente>{

    int getMaxSequenceForYear(String anio);
    List<Paciente> buscarPacientes(String query);
    
    List<Paciente> buscarPacientesInactivos(String query);
    
    public Paciente getPaciente(String id);
    
    List<Paciente> getPacientesActivos();
    
    List<Paciente> getPacientesInactivos();
    
    Paciente get(String id); // ID tipo String

}
