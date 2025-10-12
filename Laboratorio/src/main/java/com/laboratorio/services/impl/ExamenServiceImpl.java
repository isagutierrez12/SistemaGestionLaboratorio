/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.laboratorio.services.impl;

import com.laboratorio.model.Examen;
import com.laboratorio.repository.ExamenRepository;
import com.laboratorio.service.ExamenService;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ExamenServiceImpl implements ExamenService{
    @Autowired
    private ExamenRepository examenRepository;

    @Override
      @Transactional(readOnly = true)
    public List<Examen> getAll() {
         return examenRepository.findAll();
    }

    @Override
      @Transactional(readOnly = true)
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
