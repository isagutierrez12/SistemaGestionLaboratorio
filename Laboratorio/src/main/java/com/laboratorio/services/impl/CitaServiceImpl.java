/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.laboratorio.services.impl;

import com.laboratorio.model.Cita;
import com.laboratorio.repository.CitaRepository;
import com.laboratorio.service.CitaService;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class CitaServiceImpl implements CitaService{

    
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
    
}
