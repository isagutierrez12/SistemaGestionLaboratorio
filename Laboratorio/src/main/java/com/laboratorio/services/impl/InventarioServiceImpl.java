/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.laboratorio.services.impl;

import com.laboratorio.model.ExamenInsumo;
import com.laboratorio.model.Inventario;
import com.laboratorio.repository.ExamenInsumoRepository;
import com.laboratorio.repository.InventarioRepository;
import com.laboratorio.repository.SolicitudDetalleRepository;
import com.laboratorio.service.InventarioService;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class InventarioServiceImpl implements InventarioService{
    
      @Autowired
    private InventarioRepository inventarioRepository;

    @Autowired
    private ExamenInsumoRepository examenInsumoRepository;

    @Autowired
    private SolicitudDetalleRepository solicitudDetalleRepository;
    
    

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
    
    @Override
    public void ajustarInventarioPorCita(Long idCita, String nuevoEstado) {

      
        List<Long> examenes = solicitudDetalleRepository.findExamenesByCita(idCita);

        if (examenes.isEmpty()) return;

        List<ExamenInsumo> insumosRequeridos = examenInsumoRepository.findByExamenInList(examenes);

        for (ExamenInsumo req : insumosRequeridos) {
            Inventario inv = inventarioRepository.findByInsumo_IdInsumo(req.getIdInsumo());

            if (inv == null) continue;

            switch (nuevoEstado.toUpperCase()) {
                case "AGENDADA":
                   
                    inv.setStockBloqueado(inv.getStockBloqueado() + req.getCantidadNecesaria());
                    break;

                case "TERMINADA":
                
                    inv.setStockActual(inv.getStockActual() - req.getCantidadNecesaria());
                    inv.setStockBloqueado(Math.max(0, inv.getStockBloqueado() - req.getCantidadNecesaria()));
                    break;

                case "CANCELADA":
                   
                    inv.setStockBloqueado(Math.max(0, inv.getStockBloqueado() - req.getCantidadNecesaria()));
                    break;
            }

            inventarioRepository.save(inv);
        }
    }
    
}
