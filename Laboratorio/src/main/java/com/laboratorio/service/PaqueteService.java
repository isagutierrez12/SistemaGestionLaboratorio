package com.laboratorio.service;

import com.laboratorio.model.Paquete;
import java.util.List;

public interface PaqueteService extends CrudService<Paquete> {
    
    List<Paquete> buscarPaquetes(String query);

    void agregarExamen(Long idPaquete, Long idExamen);

    void quitarExamen(Long idPaquete, Long idExamen);
    
    Paquete getById(Long idPaquete);
    
    boolean existsByNombre(String nombre);

}
