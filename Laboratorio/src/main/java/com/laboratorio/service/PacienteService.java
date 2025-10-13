package com.laboratorio.service;

import com.laboratorio.model.Paciente;
import java.util.List;

public interface PacienteService extends CrudService<Paciente>{


    
    int getMaxSequenceForYear(String anio);
    List<Paciente> buscarPacientes(String query);
}
