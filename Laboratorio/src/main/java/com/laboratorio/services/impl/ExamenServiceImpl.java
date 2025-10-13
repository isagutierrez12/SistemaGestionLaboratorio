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
    public List<Examen> getAll() {
        return examenRepository.findAll();
    }

    @Override
    public Examen get(Examen examen) {
        return examenRepository.findById(examen.getIdExamen()).orElse(null);
    }

    @Override
    public void save(Examen examen) {
       examenRepository.save(examen);
    }

    @Override
    public void delete(Examen examen) {
        examenRepository.delete(examen);
    }
    
    @Override
    public List<Examen> buscarExamenes(String query) {
        return examenRepository.buscarPorQuery(query);
    }
}
