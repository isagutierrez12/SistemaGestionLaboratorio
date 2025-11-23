/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.laboratorio.services.impl;

import com.laboratorio.model.SolicitudDetalle;
import com.laboratorio.repository.SolicitudDetalleRepository;
import com.laboratorio.service.SolicitudDetalleService;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SolicitudDetalleServiceImpl implements SolicitudDetalleService{
     
    private final SolicitudDetalleRepository solicitudDetalleRepository;

    @Autowired
    public SolicitudDetalleServiceImpl(SolicitudDetalleRepository solicitudDetalletRepository) {
        this.solicitudDetalleRepository = solicitudDetalletRepository;
    }

    @Override
    public List<SolicitudDetalle> getAll() {
        return solicitudDetalleRepository.findAll();
    }

    @Override
    public SolicitudDetalle get(SolicitudDetalle solicitudDetalle) {
        return solicitudDetalleRepository.findById(solicitudDetalle.getIdSolicitudDetalle()).orElse(null);
    }

    @Override
    public void save(SolicitudDetalle solicitudDetalle) {
        solicitudDetalleRepository.save(solicitudDetalle);

    }

    @Override
    public void delete(SolicitudDetalle solicitudDetalle) {
        solicitudDetalleRepository.delete(solicitudDetalle);
    }
}
