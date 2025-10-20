package com.laboratorio.services.impl;

import com.laboratorio.model.Examen;
import com.laboratorio.model.Paquete;
import com.laboratorio.model.DetallePaquete;
import com.laboratorio.repository.PaqueteRepository;
import com.laboratorio.repository.DetallePaqueteRepository;
import com.laboratorio.service.ExamenService;
import com.laboratorio.service.PaqueteService;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PaqueteServiceImpl implements PaqueteService {

    private final PaqueteRepository paqueteRepository;
    private final DetallePaqueteRepository detalleRepository;
    private final ExamenService examenService;

    @Autowired
    public PaqueteServiceImpl(PaqueteRepository paqueteRepository,
                              DetallePaqueteRepository detalleRepository,
                              ExamenService examenService) {
        this.paqueteRepository = paqueteRepository;
        this.detalleRepository = detalleRepository;
        this.examenService = examenService;
    }

    @Override
    public List<Paquete> getAll() { 
        return paqueteRepository.findAll(); 
    }

    @Override
    public Paquete get(Paquete p) {
        return paqueteRepository.findById(p.getIdPaquete()).orElse(null);
    }

    @Override
    @Transactional
    public void save(Paquete p) {
        if (p.getCodigo() == null || p.getCodigo().isBlank()) {
            p.setCodigo("PCK-" + System.currentTimeMillis());
        }
        paqueteRepository.save(p);
    }

    @Override
    @Transactional
    public void delete(Paquete p) {
        paqueteRepository.delete(p);
    }


    @Override
    public List<Paquete> buscarPaquetes(String query) {
        if (query == null || query.isBlank()) return getAll();
        return paqueteRepository.buscar(query.trim());
    }

    @Override
    @Transactional
    public void agregarExamen(Long idPaquete, Long idExamen) {
        Paquete paquete = paqueteRepository.findById(idPaquete)
                .orElseThrow(() -> new IllegalArgumentException("Paquete no encontrado"));
        Examen examen = new Examen();
        examen.setIdExamen(idExamen);
        //evitar duplicados
        if (!detalleRepository.existsByPaqueteAndExamen(paquete, examen)) {
            DetallePaquete dp = new DetallePaquete();
            dp.setPaquete(paquete);
            dp.setExamen(examen);
            detalleRepository.save(dp);
        }
    }

    @Override
    @Transactional
    public void quitarExamen(Long idPaquete, Long idExamen) {
        Paquete paquete = paqueteRepository.findById(idPaquete)
                .orElseThrow(() -> new IllegalArgumentException("Paquete no encontrado"));
        Examen examen = new Examen();
        examen.setIdExamen(idExamen);
        detalleRepository.deleteByPaqueteAndExamen(paquete, examen);
    }
}