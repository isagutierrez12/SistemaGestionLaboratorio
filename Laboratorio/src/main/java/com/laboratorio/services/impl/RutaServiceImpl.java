/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.laboratorio.services.impl;

import com.laboratorio.model.Ruta;
import com.laboratorio.repository.RutaRepository;
import com.laboratorio.service.RutaService;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RutaServiceImpl implements RutaService{

    private final RutaRepository rutaRepository;
    public RutaServiceImpl(RutaRepository rutaRepository){
        this.rutaRepository = rutaRepository;
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<Ruta> getAll() {
        return rutaRepository.findAll();
    }
    
    @Override
    @Transactional(readOnly = true)
    public Ruta get(Ruta entity) {
        return rutaRepository.findById(entity.getIdRuta()).orElse(null);
    }

    @Override
    public void save(Ruta entity) {
        rutaRepository.save(entity);
    }

    @Override
    public void delete(Ruta entity) {
        rutaRepository.delete(entity);
    }
}
