/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.laboratorio.services.impl;

import com.laboratorio.model.Cita;
import com.laboratorio.repository.CitaRepository;
import com.laboratorio.service.CitaService;
import java.time.LocalDate;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class CitaServiceImpl implements CitaService {

    private CitaRepository citaRepository;

    public CitaServiceImpl(CitaRepository citaRepository) {
        this.citaRepository = citaRepository;
    }

    @Override
    public List<Cita> getAll() {
        return citaRepository.findAll();
    }

    @Override
    public Cita get(Cita entity) {
        return citaRepository.findById(entity.getIdCita()).orElse(null);
    }

    @Override
    public void save(Cita entity) {
        citaRepository.save(entity);
    }

    @Override
    public void delete(Cita entity) {
        citaRepository.delete(entity);
    }

    @Override
    public Cita getById(Long id) {
        return citaRepository.findById(id).orElse(null);
    }

    @Override
    public List<Cita> findHistorialPorPaciente(String idPaciente) {
        return citaRepository.findHistorialPorPaciente(idPaciente);
    }
    @Override
    public List<String> obtenerHorasOcupadas(LocalDate fecha) {
        return citaRepository.horasOcupadasPorFecha(fecha);
    }
}
