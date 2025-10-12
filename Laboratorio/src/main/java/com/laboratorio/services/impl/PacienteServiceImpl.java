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
    public List<Paciente> findAll() {
        return pacienteRepository.findAll();
    }

    @Override
    public Paciente findById(String id) {
        return pacienteRepository.findById(id).orElse(null);
    }

    @Override
    public Paciente save(Paciente paciente) {
        return pacienteRepository.save(paciente);
    }

    @Override
    public void delete(String id) {
        pacienteRepository.deleteById(id);
    }

    @Override
    public int getMaxSequenceForYear(String anio) {
        return pacienteRepository.getMaxSequenceForYear(anio);
    }

    @Override
    public List<Paciente> buscarPacientes(String query) {
        return pacienteRepository.buscarPorQuery(query);
    }
}
