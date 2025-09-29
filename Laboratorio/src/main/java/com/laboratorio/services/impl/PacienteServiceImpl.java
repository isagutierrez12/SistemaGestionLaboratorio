/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
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
    public List<Paciente> getPacientes() {
        return pacienteRepository.findAll();
    }

    @Override
    public Paciente save(Paciente paciente) {
        return pacienteRepository.save(paciente);
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
