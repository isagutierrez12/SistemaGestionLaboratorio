/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.laboratorio.services.impl;

import com.laboratorio.model.Cita;
import com.laboratorio.model.Pago;
import com.laboratorio.repository.PagoRepository;
import com.laboratorio.service.CitaService;
import com.laboratorio.service.PagoService;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PagoServiceImpl implements PagoService {

    private final PagoRepository pagoRepository;
    private final CitaService citaService;

    public PagoServiceImpl(PagoRepository pagoRepository, CitaService citaService) {
        this.pagoRepository = pagoRepository;
        this.citaService = citaService;
    }

    @Override
    public void save(Pago pago) {
        pagoRepository.save(pago);
    }

    @Override
    public boolean existsByCita(Long idCita) {
        return pagoRepository.existsByCita_IdCita(idCita);
    }
    
    @Override
    @Transactional
    public Pago saveOrUpdateByCita(Long idCita, Double monto, String tipoPago) {

        Pago pago = pagoRepository.findByCita_IdCita(idCita)
                .orElseGet(Pago::new);

        // si es nuevo, amarrarlo a la cita
        if (pago.getIdPago() == null) {
            Cita cita = citaService.getById(idCita);
            pago.setCita(cita);
        }

        pago.setFechaPago(LocalDateTime.now());
        pago.setMonto(monto);
        pago.setTipoPago(tipoPago);

        return pagoRepository.save(pago);
    }
}