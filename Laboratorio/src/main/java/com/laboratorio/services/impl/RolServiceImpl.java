/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.laboratorio.services.impl;

import com.laboratorio.model.Rol;
import com.laboratorio.repository.RolRepository;
import com.laboratorio.service.RolService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RolServiceImpl implements RolService{

    @Autowired 
    RolRepository rolRepository;
    
    @Override
    public Rol findRolByIdUsuario(Long idUSuario) {
        return rolRepository.findRolByIdUsuario(idUSuario);
    }
    
}
