package com.laboratorio.service;

import com.laboratorio.model.Paquete;
import java.util.List;

public interface PaqueteService extends CrudService<Paquete> {
    
    List<Paquete> buscarPaquetes(String query);

    void agregarExamen(Long idPaquete, Long idExamen);

    void quitarExamen(Long idPaquete, Long idExamen);
    
    Paquete getById(Long idPaquete);

    Paquete getByIdConDetalles(Long idPaquete);
    
    boolean existsByNombre(String nombre);
    
    List<Paquete> getActivosConExamenes();
    
    void actualizarExamenesDelPaquete(Long idPaquete, String examenesSeleccionados);
}