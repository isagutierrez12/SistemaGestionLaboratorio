/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.laboratorio.services.impl;

import com.laboratorio.model.Rol;
import com.laboratorio.repository.RolRepository;
import com.laboratorio.service.RolService;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RolServiceImpl  implements RolService{
    private final RolRepository rolRepository;
    public RolServiceImpl (RolRepository rolRepository){
        this.rolRepository = rolRepository;
    }
   

    @Override
      @Transactional(readOnly = true)
    public List<Rol> getAll() {
        return rolRepository.findAll();
    }

    @Override
      @Transactional(readOnly = true)
    public Rol get(Rol entity) {
        return rolRepository.findById(entity.getIdRol()).orElse(null);
    }

    @Override
      @Transactional(readOnly = true)
    public void save(Rol entity) {
        rolRepository.save(entity);
    }

    @Override
      @Transactional(readOnly = true)
    public void delete(Rol entity) {
        rolRepository.delete(entity);
    }
}
