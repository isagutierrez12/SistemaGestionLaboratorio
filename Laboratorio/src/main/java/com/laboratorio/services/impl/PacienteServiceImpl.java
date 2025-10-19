package com.laboratorio.services.impl;

import com.laboratorio.model.Paciente;
import com.laboratorio.repository.PacienteRepository;
import com.laboratorio.service.PacienteService;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PacienteServiceImpl implements PacienteService {

    private final PacienteRepository pacienteRepository;

    @Autowired
    public PacienteServiceImpl(PacienteRepository pacienteRepository) {
        this.pacienteRepository = pacienteRepository;
    }

    @Override
    public List<Paciente> getAll() {
        return pacienteRepository.findAll();
    }

    @Override
    public Paciente get(Paciente paciente) {
        return pacienteRepository.findById(paciente.getIdPaciente()).orElse(null);
    }

    @Override
    public void save(Paciente paciente) {
        pacienteRepository.save(paciente);
    }

    @Override
    public void delete(Paciente paciente) {
        pacienteRepository.delete(paciente);
    }

    @Override
    public int getMaxSequenceForYear(String anio) {
        return pacienteRepository.getMaxSequenceForYear(anio);
    }

    @Override
    public List<Paciente> buscarPacientes(String query) {

        return pacienteRepository.buscarActivosPorQuery(query);
    }
    
    @Override
    public List<Paciente> buscarPacientesInactivos(String query) {
        return pacienteRepository.buscarInactivosPorQuery(query);
    }
    
    @Override
    public Paciente getPaciente(String id) {
        return pacienteRepository.findByIdPaciente(id);
    }
    
    @Override
    public List<Paciente> getPacientesActivos() {
        return pacienteRepository.findByActivoTrue();
    }
    
    @Override
    public List<Paciente> getPacientesInactivos() {
        return pacienteRepository.findByActivoFalse();
    }


}
