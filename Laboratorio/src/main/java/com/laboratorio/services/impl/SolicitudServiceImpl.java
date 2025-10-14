package com.laboratorio.services.impl;

import com.laboratorio.model.Solicitud;
import com.laboratorio.model.SolicitudDetalle;
import com.laboratorio.repository.SolicitudRepository;
import com.laboratorio.service.SolicitudService;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SolicitudServiceImpl implements SolicitudService {

    private final SolicitudRepository solicitudRepository;

    @Autowired
    public SolicitudServiceImpl(SolicitudRepository solicitudRepository) {
        this.solicitudRepository = solicitudRepository;
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

   
}
