package com.laboratorio.service;

import com.laboratorio.model.ExamenInsumo;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public interface ExamenInsumoService {

    List<ExamenInsumo> listarPorExamen(Long idExamen);

    void guardarRelacion(Long idExamen, Long idInsumo, int cantidadNecesaria);

    void eliminarRelacion(Long idExamenInsumo);
}
