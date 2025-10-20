/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.laboratorio.service;

import com.laboratorio.model.Paquete;
import com.laboratorio.model.RutaPermit;
import java.util.List;

public interface PaqueteService extends CrudService<Paquete> {
    
    List<Paquete> buscarPaquetes(String query);

    void agregarExamen(Long idPaquete, Long idExamen);

    void quitarExamen(Long idPaquete, Long idExamen);
}
