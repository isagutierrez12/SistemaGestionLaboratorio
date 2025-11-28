package com.laboratorio.service;

import com.laboratorio.model.Examen;
import com.laboratorio.model.RutaPermit;
import java.util.List;

public interface ExamenService extends CrudService<Examen> {
    
    List<Examen> buscarExamenes(String query); 
    public List<Examen> findById(List<Long> ids);

    public boolean existsByNombre(String nombre);

}
