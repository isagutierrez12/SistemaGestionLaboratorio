/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.laboratorio.service;

import com.laboratorio.model.Examen;
import com.laboratorio.model.RutaPermit;
import java.util.List;

public interface ExamenService extends CrudService<Examen> {
    
    List<Examen> buscarExamenes(String query); 
    public List<Examen> findById(List<Long> ids);
}
