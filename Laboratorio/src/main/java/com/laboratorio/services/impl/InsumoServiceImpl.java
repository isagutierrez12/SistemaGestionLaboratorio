/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.laboratorio.services.impl;

import com.laboratorio.model.Insumo;
import com.laboratorio.repository.InsumoRepository;
import com.laboratorio.service.InsumoService;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class InsumoServiceImpl implements InsumoService{
    @Autowired
    private InsumoRepository insumoRepository;

    @Override
    public List<Insumo> getAll() {
        return insumoRepository.findAll();
    }

    @Override
    public Insumo get(Insumo entity) {
        return insumoRepository.findById(entity.getIdInsumo()).orElse(null);
    }

    @Override
    public void save(Insumo entity) {
        insumoRepository.save(entity);
    }

    @Override
    public void delete(Insumo entity) {
       insumoRepository.delete(entity);
    }
    
}
