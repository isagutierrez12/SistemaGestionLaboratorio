package com.laboratorio.services.impl;

import com.laboratorio.model.Examen;
import com.laboratorio.repository.ExamenRepository;
import com.laboratorio.service.ExamenService;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ExamenServiceImpl implements ExamenService {

    private final ExamenRepository examenRepository;

    @Autowired
    public ExamenServiceImpl(ExamenRepository examenRepository) {
        this.examenRepository = examenRepository;
    }

    @Override
    public List<Examen> findAll() {
        return examenRepository.findAll();
    }

    @Override
    public Examen findById(Long id) {
        return examenRepository.findById(id).orElse(null);
    }

    @Override
    public Examen save(Examen examen) {
        return examenRepository.save(examen);
    }

    @Override
    public void delete(Long id) {
        examenRepository.deleteById(id);
    }
    
    @Override
    public List<Examen> buscarExamenes(String query) {
        return examenRepository.buscarPorQuery(query);
    }
}
