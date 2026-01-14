/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.laboratorio.services.impl;

import com.laboratorio.model.Pago;
import com.laboratorio.repository.PagoRepository;
import com.laboratorio.service.PagoService;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class PagoServiceImpl implements PagoService {

    private final PagoRepository pagoRepository;

    public PagoServiceImpl(PagoRepository pagoRepository) {
        this.pagoRepository = pagoRepository;
    }

    @Override
    public void save(Pago pago) {
        pagoRepository.save(pago);
    }

    @Override
    public boolean existsByCita(Long idCita) {
        return pagoRepository.existsByCita_IdCita(idCita);
    }
}