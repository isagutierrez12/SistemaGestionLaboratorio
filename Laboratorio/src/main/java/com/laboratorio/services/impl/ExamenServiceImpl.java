/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.laboratorio.services.impl;

import com.laboratorio.model.Examen;
import com.laboratorio.repository.ExamenRepository;
import com.laboratorio.service.ExamenService;
import java.util.List;

/**
 *
 * @author melanie
 */
public class ExamenServiceImpl implements ExamenService{
    private ExamenRepository examenRepository;

    @Override
    public List<Examen> getAll() {
         return examenRepository.findAll();
    }

    @Override
    public Examen get(Examen entity) {
         return examenRepository.findById(entity.getIdExamen()).orElse(null);
    }

    @Override
    public void save(Examen entity) {
        examenRepository.save(entity);
    }

    @Override
    public void delete(Examen entity) {
        examenRepository.delete(entity);
    }
    
}
