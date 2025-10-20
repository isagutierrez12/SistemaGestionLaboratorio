package com.laboratorio.services.impl;

import com.laboratorio.model.Examen;
import com.laboratorio.model.Solicitud;
import com.laboratorio.model.SolicitudDetalle;
import com.laboratorio.repository.ExamenRepository;
import com.laboratorio.repository.SolicitudDetalleRepository;

import com.laboratorio.repository.SolicitudRepository;
import com.laboratorio.service.SolicitudService;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SolicitudServiceImpl implements SolicitudService {

    private final SolicitudRepository solicitudRepository;
  
    private SolicitudDetalleRepository solicitudDetalleRepository;
    
    private ExamenRepository examenRepository;


    @Autowired
    public SolicitudServiceImpl(SolicitudRepository solicitudRepository,
                                SolicitudDetalleRepository detalleRepository,
                                ExamenRepository examenRepository) {
        this.solicitudRepository = solicitudRepository;
        this.solicitudDetalleRepository = detalleRepository;
        this.examenRepository = examenRepository;
    }

    @Override
    public List<Solicitud> getAll() {
        return solicitudRepository.findAll();
    }

    @Override
    public Solicitud get(Solicitud solicitud) {
        return solicitudRepository.findById(solicitud.getIdSolicitud()).orElse(null);
    }

    @Override
    public void save(Solicitud solicitud) {
        // Establece fecha y estado si es nueva solicitud
        if (solicitud.getFechaSolicitud() == null) {
            solicitud.setFechaSolicitud(LocalDateTime.now());
        }
        if (solicitud.getEstado() == null) {
            solicitud.setEstado("Pendiente");
        }
        solicitudRepository.save(solicitud);
    }

    @Override
    public void delete(Solicitud solicitud) {
        solicitudRepository.delete(solicitud);
    }

    @Override
    public List<Solicitud> buscarSolicitudes(String query) {
        return solicitudRepository.buscarPorQuery(query);
    }
    @Transactional
    @Override
    public void guardarConDetalles(Solicitud solicitud, List<Long> examenesSeleccionados) {
        
    // Establecer fecha y estado si es necesario
    if (solicitud.getFechaSolicitud() == null) {
        solicitud.setFechaSolicitud(LocalDateTime.now());
    }
    if (solicitud.getEstado() == null) {
        solicitud.setEstado("Pendiente");
    }

    // Crear lista de detalles y agregarlos a la solicitud
    for (Long idExamen : examenesSeleccionados) {
        Examen examen = examenRepository.findById(idExamen)
                .orElseThrow(() -> new IllegalArgumentException("Examen no encontrado"));

        SolicitudDetalle detalle = new SolicitudDetalle();
        detalle.setExamen(examen);

        solicitud.addDetalle(detalle); // importante
    }

    // Guarda solicitud y detalles en cascada
    solicitudRepository.save(solicitud);
    }
}
