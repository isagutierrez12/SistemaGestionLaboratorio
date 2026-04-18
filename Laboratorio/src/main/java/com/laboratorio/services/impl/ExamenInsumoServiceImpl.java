package com.laboratorio.services.impl;

import com.laboratorio.model.Examen;
import com.laboratorio.model.ExamenInsumo;
import com.laboratorio.model.Insumo;
import com.laboratorio.repository.ExamenInsumoRepository;
import com.laboratorio.repository.ExamenRepository;
import com.laboratorio.repository.InsumoRepository;
import com.laboratorio.service.ExamenInsumoService;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ExamenInsumoServiceImpl implements ExamenInsumoService {

    @Autowired
    private ExamenInsumoRepository examenInsumoRepository;

    @Autowired
    private ExamenRepository examenRepository;

    @Autowired
    private InsumoRepository insumoRepository;

    @Override
    public List<ExamenInsumo> listarPorExamen(Long idExamen) {
        return examenInsumoRepository.findByIdExamen(idExamen);
    }

    @Override
    public void guardarRelacion(Long idExamen, Long idInsumo, int cantidadNecesaria) {

        if (cantidadNecesaria <= 0) {
            throw new IllegalArgumentException("La cantidad necesaria debe ser mayor a 0.");
        }

        Examen examen = examenRepository.findById(idExamen).orElse(null);
        if (examen == null) {
            throw new IllegalArgumentException("El examen no existe.");
        }

        Insumo insumo = insumoRepository.findById(idInsumo).orElse(null);
        if (insumo == null) {
            throw new IllegalArgumentException("El insumo no existe.");
        }

        if (!insumo.isActivo()) {
            throw new IllegalArgumentException("Solo se pueden asociar insumos activos.");
        }

        Optional<ExamenInsumo> existente = examenInsumoRepository
                .findByIdExamenAndIdInsumo(idExamen, idInsumo);

        if (existente.isPresent()) {
            ExamenInsumo relacion = existente.get();
            relacion.setCantidadNecesaria(cantidadNecesaria);
            examenInsumoRepository.save(relacion);
            return;
        }

        ExamenInsumo nueva = new ExamenInsumo();
        nueva.setIdExamen(idExamen);
        nueva.setIdInsumo(idInsumo);
        nueva.setCantidadNecesaria(cantidadNecesaria);

        examenInsumoRepository.save(nueva);
    }

    @Override
    public void eliminarRelacion(Long idExamenInsumo) {
        examenInsumoRepository.deleteById(idExamenInsumo);
    }
}