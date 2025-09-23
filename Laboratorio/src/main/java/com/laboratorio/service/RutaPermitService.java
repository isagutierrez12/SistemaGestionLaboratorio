/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.laboratorio.service;

import com.laboratorio.model.RutaPermit;
import org.springframework.stereotype.Service;

@Service
public interface RutaPermitService extends CrudService<RutaPermit>{
     public String[] getRutaPermitsString();
}
