package com.laboratorio.services.impl;

import com.laboratorio.model.Insumo;
import com.laboratorio.model.Inventario;
import com.laboratorio.repository.InsumoRepository;
import com.laboratorio.service.InsumoService;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class InsumoServiceImpl implements InsumoService {

    @Autowired
    private InsumoRepository insumoRepository;

    @Override
    public List<Insumo> getAll() {
        return insumoRepository.findAll();
    }

    @Override
    public Insumo get(Insumo entity) {
        return insumoRepository.findById(entity.getIdInsumo()).orElse(null);
    }

    @Override
    public void save(Insumo entity) {
        if (entity.getIdInsumo() == null) {
            if (insumoRepository.existsByNombre(entity.getNombre())) {
                throw new IllegalArgumentException("Ya existe un insumo con el mismo nombre.");
            }
        }
        insumoRepository.save(entity);
    }

    @Override
    public void delete(Insumo entity) {
        insumoRepository.delete(entity);
    }

    @Override
    public List<Insumo> buscarPorQuery(String query) {
        return insumoRepository.buscarPorQuery(query);
    }

}
