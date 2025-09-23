package com.laboratorio.services.impl;

import com.laboratorio.model.Rol;
import com.laboratorio.model.RutaPermit;
import com.laboratorio.repository.RutaPermitRepository;
import com.laboratorio.service.RutaPermitService;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RutaPermitServiceImpl implements RutaPermitService {

    @Autowired
    private RutaPermitRepository repository;

 

    @Override
    public List<RutaPermit> getAll() {
        return repository.findAll();
    }

    @Override
    public RutaPermit get(RutaPermit entity) {
        return repository.findById(entity.getIdRutaPermit()).orElse(null);
    }

    @Override
    public void save(RutaPermit entity) {
        repository.save(entity);
    }

    @Override
    public void delete(RutaPermit entity) {
        repository.delete(entity);
    }

     @Override
    @Transactional(readOnly=true)
    public String[] getRutaPermitsString(){
        var lista = repository.findAll();
        String[] rutasPermit = new String[lista.size()];
        int i = 0;
        for (RutaPermit rp : lista) {
            rutasPermit[i] = rp.getRutaPermit();
            i++;
        }
        return rutasPermit;
    }
    
}
