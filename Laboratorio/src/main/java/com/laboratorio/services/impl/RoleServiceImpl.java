/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.laboratorio.services.impl;

import com.laboratorio.model.Rol;
import com.laboratorio.model.Role;
import com.laboratorio.repository.RoleRepository;
import com.laboratorio.service.RoleService;
import java.util.List;

/**
 *
 * @author melanie
 */
public class RoleServiceImpl implements RoleService {

    private RoleRepository roleRepository;
    
    @Override
    public List<Role> getAll() {
        return roleRepository.findAll();
    }
    
    @Override
    public Role get(Role entity) {
        return roleRepository.findById(entity.getRol()).orElse(null);
    }
    
    @Override
    public void save(Role entity) {
        roleRepository.save(entity);
    }
    
    @Override
    public void delete(Role entity) {
        roleRepository.delete(entity);
    }
    
}
