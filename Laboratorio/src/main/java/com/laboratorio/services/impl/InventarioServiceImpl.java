/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.laboratorio.services.impl;

import com.laboratorio.model.Inventario;
import com.laboratorio.repository.InventarioRepository;
import com.laboratorio.service.InventarioService;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class InventarioServiceImpl implements InventarioService{
    @Autowired
    private InventarioRepository inventarioRepository;
    

    @Override
    public List<Inventario> getAll() {
        return inventarioRepository.findAll();
    }

    @Override
    public Inventario get(Inventario entity) {
        return inventarioRepository.findById(entity.getIdInventario()).orElse(null);
    }

    @Override
    public void save(Inventario entity) {
         inventarioRepository.save(entity);
    }

    @Override
    public void delete(Inventario entity) {
        inventarioRepository.delete(entity);
    }
    
}
